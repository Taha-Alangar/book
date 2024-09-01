package com.tahaalangar.book.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tahaalangar.book.databinding.ItemViewDetailBinding
import com.tahaalangar.book.entities.TransactionEntity
import com.tahaalangar.book.formatTime
import com.tahaalangar.book.formatTimestamp
import kotlin.math.abs

class TransactionAdapter(
    private var transactions: List<TransactionEntity>,
    private val onItemLongClicked: (TransactionEntity) -> Unit
) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
    inner class TransactionViewHolder(val binding: ItemViewDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: TransactionEntity, currentBalance: Double) {
            // Format timestamp to date and time
            val formattedDate = formatTimestamp(transaction.timestamp)
            val formattedTime = formatTime(transaction.timestamp)

            // Set formatted date and time
            binding.detailDate.text = formattedDate
            binding.detailTime.text = formattedTime


            // Set the reason
            binding.textView.text = transaction.reason

            // Set the amounts based on the transaction type
            if (transaction.type == "Gave") {
                binding.detailGetAmount.text = ""
                binding.detailGaveAmount.text = transaction.amount.toString()
            } else if (transaction.type == "Get") {
                binding.detailGetAmount.text = transaction.amount.toString()
                binding.detailGaveAmount.text = ""
            }

            // Handle long click event
            binding.root.setOnLongClickListener {
                onItemLongClicked(transaction)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemViewDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        // Calculate the previous balance up to the current position
        val previousBalance = calculatePreviousBalance(position)

        // Calculate the current balance after the transaction
        val currentBalance = if (transaction.type == "Gave") {
            previousBalance - transaction.amount
        } else {
            previousBalance + transaction.amount
        }

        // Bind data to the ViewHolder
        holder.bind(transaction, currentBalance)
    }

    private fun calculatePreviousBalance(position: Int): Double {
        var balance = 0.0
        for (i in 0 until position) {
            val transaction = transactions[i]
            balance += if (transaction.type == "Gave") {
                -transaction.amount
            } else {
                transaction.amount
            }
        }
        return balance
    }

    override fun getItemCount(): Int = transactions.size

    fun updateTransactions(newTransactions: List<TransactionEntity>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
