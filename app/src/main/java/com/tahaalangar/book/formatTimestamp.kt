package com.tahaalangar.book

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) // Format date
    return format.format(date)
}

fun formatTime(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Format time
    return format.format(date)
}

fun main() {
    val testTimestamp = 1723746600000L // August 15, 2024, 10:23 PM
    println("Formatted Date: ${formatTimestamp(testTimestamp)}")
    println("Formatted Time: ${formatTime(testTimestamp)}")
}

