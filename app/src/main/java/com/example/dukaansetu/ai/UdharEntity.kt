package com.example.dukaansetu.ai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "udhaar")
data class UdhaarEntity(
    @PrimaryKey
    val customer: String,
    val balance: Double
)