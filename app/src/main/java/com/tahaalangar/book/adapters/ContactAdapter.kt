package com.tahaalangar.book.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tahaalangar.book.databinding.ItemViewHomeContactBinding
import com.tahaalangar.book.entities.CustomerEntity
import com.tahaalangar.book.entities.TransactionEntity
import com.tahaalangar.book.roomdb.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class ContactAdapter(
    private var customers: List<CustomerEntity>,
    private var transactions: List<TransactionEntity>,
    val onItemClicked: (CustomerEntity, Double) -> Unit ,// Updated lambda to include amount
    private val longClickListener: (CustomerEntity) -> Unit

) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
    inner class ContactViewHolder(val binding: ItemViewHomeContactBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(customer: CustomerEntity) {
            binding.name.text = customer.name

            // Calculate total amounts for "Gave" and "Get" transactions
            val totalGiven = transactions.filter { it.customerId == customer.customerId && it.type == "Gave" }
                .sumOf { it.amount }
            val totalReceived = transactions.filter { it.customerId == customer.customerId && it.type == "Get" }
                .sumOf { it.amount }

            // Determine the net balance
            val netBalance = totalReceived - totalGiven

            // Set the amount text and color based on the net balance
            binding.amount.text = abs(netBalance).toString()
            binding.amount.setTextColor(
                when {
                    netBalance < 0 -> Color.RED   // Customer owes money
                    netBalance > 0 -> Color.GREEN // Customer is owed money
                    else -> Color.BLACK           // Balance is zero
                }
            )

            // Handle item click
            binding.root.setOnClickListener {
                onItemClicked(customer, netBalance)
            }

            // Handle item long click
            binding.root.setOnLongClickListener {
                longClickListener(customer)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemViewHomeContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(customers[position])
    }

    override fun getItemCount(): Int = customers.size

    fun updateCustomers(newCustomers: List<CustomerEntity>) {
        customers = newCustomers
        notifyDataSetChanged()
    }

    fun updateTransactions(newTransactions: List<TransactionEntity>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
