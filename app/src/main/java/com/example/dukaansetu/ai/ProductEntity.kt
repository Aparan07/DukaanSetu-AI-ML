package com.example.dukaansetu.ai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val product: String,
    val quantity: Double
)