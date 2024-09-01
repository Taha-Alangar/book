package com.tahaalangar.book.screens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tahaalangar.book.adapters.ContactAdapter
import com.tahaalangar.book.databinding.ActivityHomeBinding
import com.tahaalangar.book.entities.CustomerEntity
import com.tahaalangar.book.entities.TransactionEntity
import com.tahaalangar.book.roomdb.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class HomeScreen : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var database: AppDatabase
    private lateinit var adapter: ContactAdapter

    companion object {
        private const val TRANSACTION_TYPE_GAVE = "Gave"
        private const val TRANSACTION_TYPE_GET = "Get"
        private const val DIALOG_TITLE_DELETE_CUSTOMER = "Delete Customer"
        private const val DIALOG_MESSAGE_CONFIRM_DELETE = "Are you sure you want to delete this customer and all associated transactions?"
        private const val POSITIVE_BUTTON_YES = "Yes"
        private const val NEGATIVE_BUTTON_NO = "No"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)
        initializeAdapter()
        setupRecyclerView()
        observeCustomerData()

        binding.floatingButton.setOnClickListener {
            it.animate().apply {
                duration = 1000
                rotationBy(360f)
                startActivity(Intent(this@HomeScreen, AddContact::class.java))
            }.start()
        }
    }
    private fun initializeAdapter() {
        adapter = ContactAdapter(
            customers = emptyList(),
            transactions = emptyList(),
            onItemClicked = { customer, totalAmount ->
                navigateToTransactionDetail(customer.customerId, totalAmount)
            },
            longClickListener = { customer ->
                showDeleteConfirmationDialog(customer)
            }
        )
    }
    private fun setupRecyclerView() {
        binding.homeRv.apply {
            layoutManager = LinearLayoutManager(this@HomeScreen)
            adapter = this@HomeScreen.adapter
        }
    }

    private fun observeCustomerData() {
        database.customerDao().getAllCustomers().observe(this) { customers ->
            adapter.updateCustomers(customers)
            observeTransactionData()
        }
    }

    private fun observeTransactionData() {
        database.transactionDao().getAllTransactions().observe(this) { transactions ->
            adapter.updateTransactions(transactions)
            updateTotalAmounts(transactions)
        }
    }
    private fun updateTotalAmounts(transactions: List<TransactionEntity>) {
        val customerTotals = transactions.groupBy { it.customerId.toLong() }
            .mapValues { entry ->
                entry.value.fold(Pair(0.0, 0.0)) { totals, transaction ->
                    when (transaction.type) {
                        TRANSACTION_TYPE_GAVE -> totals.copy(first = totals.first + transaction.amount)
                        TRANSACTION_TYPE_GET -> totals.copy(second = totals.second + transaction.amount)
                        else -> totals
                    }
                }
            }

        val (totalToGive, totalToGet) = calculateTotals(customerTotals)

        binding.youWillGive.text = totalToGet.takeIf { it > 0 }?.toString() ?: "0"
        binding.youWillGet.text = totalToGive.takeIf { it > 0 }?.toString() ?: "0"
    }

    private fun calculateTotals(customerTotals: Map<Long, Pair<Double, Double>>): Pair<Double, Double> {
        var totalToGive = 0.0
        var totalToGet = 0.0

        customerTotals.forEach { (_, totals) ->
            val (totalGiven, totalReceived) = totals
            when {
                totalGiven > totalReceived -> totalToGive += (totalGiven - totalReceived)
                totalReceived > totalGiven -> totalToGet += (totalReceived - totalGiven)
            }
        }

        return Pair(totalToGive, totalToGet)
    }

    private fun showDeleteConfirmationDialog(customer: CustomerEntity) {
        AlertDialog.Builder(this)
            .setTitle(DIALOG_TITLE_DELETE_CUSTOMER)
            .setMessage(DIALOG_MESSAGE_CONFIRM_DELETE)
            .setPositiveButton(POSITIVE_BUTTON_YES) { _, _ ->
                deleteCustomerAndTransactions(customer.customerId)
            }
            .setNegativeButton(NEGATIVE_BUTTON_NO, null)
            .create()
            .show()
    }

    private fun deleteCustomerAndTransactions(customerId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            database.transactionDao().deleteTransactionsByCustomerId(customerId)
            database.customerDao().deleteCustomerById(customerId)
            withContext(Dispatchers.Main) {
                observeCustomerData()
            }
        }
    }

    private fun navigateToTransactionDetail(customerId: Int, totalAmount: Double) {
        Intent(this, TransactionDetail::class.java).apply {
            putExtra("customer", customerId)
            putExtra("initialAmount", totalAmount)
            startActivity(this)
        }
    }

}
