package com.tahaalangar.book.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tahaalangar.book.daos.CustomerDao
import com.tahaalangar.book.daos.TransactionDao
import com.tahaalangar.book.entities.CustomerEntity
import com.tahaalangar.book.entities.TransactionEntity


@Database(entities = [CustomerEntity::class,TransactionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "books_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}