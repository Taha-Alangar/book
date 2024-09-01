package com.tahaalangar.book.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["customerId"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index("customerId")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Int = 0,
    val customerId: Int,   // Foreign key to reference the customer

    val amount: Double,
    val reason:String,
    val type: String,      // "Give" or "Get"
    val timestamp: Long    // Store the transaction time for history tracking
)
