package com.example.dukaansetu.ai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val product: String,
    val quantity: Double,
    val timestamp: Long = System.currentTimeMillis()
)