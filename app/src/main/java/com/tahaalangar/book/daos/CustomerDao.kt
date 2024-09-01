package com.tahaalangar.book.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tahaalangar.book.entities.CustomerEntity

@Dao
interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity): Long

    @Query("SELECT * FROM customers WHERE customerId = :customerId")
    suspend fun getCustomerById(customerId: Int): CustomerEntity?

    @Query("SELECT * FROM customers")
    fun getAllCustomers(): LiveData<List<CustomerEntity>>

    @Query("DELETE FROM customers WHERE customerId = :customerId")
    suspend fun deleteCustomerById(customerId: Int)

}