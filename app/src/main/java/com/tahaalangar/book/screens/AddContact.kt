package com.tahaalangar.book.screens

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tahaalangar.book.databinding.ActivityAddContactBinding
import com.tahaalangar.book.entities.CustomerEntity
import com.tahaalangar.book.entities.TransactionEntity
import com.tahaalangar.book.roomdb.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddContact : AppCompatActivity() {
    private lateinit var binding:ActivityAddContactBinding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize the database
        database = AppDatabase.getDatabase(this)

        binding.saveContactbtn.setOnClickListener {
            saveDataInDB()
        }
    }

    private fun saveDataInDB() {
        val name = binding.ContactName2.text.toString().trim()
        if (name.isEmpty() ){
            Toast.makeText(this,"Please fill all fields",Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                val customerEntity=CustomerEntity(name = name)
                database.customerDao().insertCustomer(customerEntity)
            }
            Toast.makeText(this@AddContact,"Contact Saved",Toast.LENGTH_SHORT).show()
            finish()
        }
    }

}