package com.tahaalangar.book.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tahaalangar.book.adapters.TransactionAdapter
import com.tahaalangar.book.databinding.ActivityTransactionDetailBinding
import com.tahaalangar.book.entities.TransactionEntity
import com.tahaalangar.book.roomdb.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionDetail : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionDetailBinding
    private lateinit var database: AppDatabase
    private lateinit var adapter: TransactionAdapter

    private var customerId: Int = 0
    private var initialAmount: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve customer ID and initial amount from the intent
        customerId = intent.getIntExtra("customer", 0)
        initialAmount = intent.getDoubleExtra("initialAmount", 0.0)

        database = AppDatabase.getDatabase(this)
        setupRecyclerView()
        fetchCustomerDetails()
        fetchCustomerTransactions()

        binding.detailYouGetBtn.setOnClickListener {
            navigateToGiveGetScreen("Get")
        }

        binding.detailYouGaveBtn.setOnClickListener {
            navigateToGiveGetScreen("Gave")
        }
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter(emptyList()) { transaction ->
            showDeleteTransactionConfirmationDialog(transaction)
        }
        binding.transactionRV.layoutManager = LinearLayoutManager(this)
        binding.transactionRV.adapter = adapter
    }

    private fun fetchCustomerTransactions() {
        database.transactionDao().getTransactionsByCustomerId(customerId)
            .observe(this) { transactions ->
                adapter.updateTransactions(transactions)
                updateTransactionSummary(transactions)
            }
    }

    private fun fetchCustomerDetails() {
        lifecycleScope.launch {
            val customer = withContext(Dispatchers.IO) {
                database.customerDao().getCustomerById(customerId)
            }
            binding.tvDetailName.text = customer?.name ?: "Customer not found"
        }
    }

    private fun updateTransactionSummary(transactions: List<TransactionEntity>) {
        val totalGiven = transactions.filter { it.type == "Gave" }.sumOf { it.amount }
        val totalReceived = transactions.filter { it.type == "Get" }.sumOf { it.amount }
        val netAmount = totalReceived - totalGiven

        when {
            netAmount > 0 -> {
                binding.youWillGiveOrGetTv.text = "You will give"
                binding.tvDetailAmount.text = netAmount.toString()
            }
            netAmount < 0 -> {
                binding.youWillGiveOrGetTv.text = "You will get"
                binding.tvDetailAmount.text = (-netAmount).toString()
            }
            else -> {
                binding.youWillGiveOrGetTv.text = "No balance"
                binding.tvDetailAmount.text = "0"
            }
        }
    }

    private fun showDeleteTransactionConfirmationDialog(transaction: TransactionEntity) {
        AlertDialog.Builder(this)
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction?")
            .setPositiveButton("Yes") { _, _ -> deleteTransaction(transaction) }
            .setNegativeButton("No", null)
            .create()
            .show()
    }

    private fun deleteTransaction(transaction: TransactionEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            database.transactionDao().deleteTransaction(transaction)
            withContext(Dispatchers.Main) {
                fetchCustomerTransactions() // Refresh transactions after deletion
            }
        }
    }

    private fun navigateToGiveGetScreen(transactionType: String) {
        val intent = Intent(this, GiveGetScreen::class.java).apply {
            putExtra("transactionType", transactionType)
            putExtra("customerId", customerId)
        }
        startActivity(intent)
    }
}