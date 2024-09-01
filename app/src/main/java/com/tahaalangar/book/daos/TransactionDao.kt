package com.tahaalangar.book.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tahaalangar.book.entities.TransactionEntity

@Dao
interface TransactionDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getTransactionsByCustomerId(customerId: Int): LiveData<List<TransactionEntity>>

    @Query("SELECT * FROM transactions")
    fun getAllTransactions(): LiveData<List<TransactionEntity>>

    @Query("DELETE FROM transactions WHERE customerId = :customerId")
    suspend fun deleteTransactionsByCustomerId(customerId: Int)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

}