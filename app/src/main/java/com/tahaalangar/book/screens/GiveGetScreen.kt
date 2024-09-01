package com.tahaalangar.book.screens

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tahaalangar.book.databinding.ActivityGiveGetScreenBinding
import com.tahaalangar.book.entities.TransactionEntity
import com.tahaalangar.book.roomdb.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GiveGetScreen : AppCompatActivity() {
    private lateinit var binding:ActivityGiveGetScreenBinding
    private lateinit var database: AppDatabase
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var transactionType: String? = null
    private var customerId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGiveGetScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = AppDatabase.getDatabase(this)

        // Get the transaction type and customerId from the Intent
        transactionType = intent.getStringExtra("transactionType")
        customerId = intent.getIntExtra("customerId", 0)

        // Initialize the current date and set it to the TextView
        val todayDate = dateFormat.format(calendar.time)


        binding.save.setOnClickListener {
            saveTransaction()
        }
    }

    private fun saveTransaction() {
        val amountStr = binding.amountEdt.text.toString()
        val dateStr =dateFormat.format(calendar.time)
        val reason = binding.reasonEdt.text.toString()

        if (amountStr.isEmpty() || dateStr.isEmpty()) {
            // Handle validation errors
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toDoubleOrNull()
        val date = dateFormat.parse(dateStr)

        if (amount == null || date == null) {
            // Handle parsing errors
            Toast.makeText(this, "Invalid amount or date format", Toast.LENGTH_SHORT).show()
            return
        }

        // Capture the current time when the user clicks the "Save" button
        val currentTimestamp = System.currentTimeMillis()

        val transaction = TransactionEntity(
            customerId = customerId,
            amount = amount,
            reason = reason,
            type = transactionType ?: "Unknown",
            timestamp = currentTimestamp // Save the current time
        )

        // Save the transaction to the database
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    database.transactionDao().insertTransaction(transaction)
                }
                finish() // Close the activity
            } catch (e: Exception) {
                Log.e("GiveGetScreen", "Error saving transaction", e)
                // Handle error
            }
        }
    }



}