package com.example.dukaansetu.ai

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface UdhaarDao {

    @Upsert
    suspend fun saveUdhaar(udhaar: UdhaarEntity)

    @Query("SELECT * FROM udhaar")
    suspend fun getAllUdhaar(): List<UdhaarEntity>

    @Query("SELECT * FROM udhaar WHERE customer = :customer")
    suspend fun getUdhaar(customer: String): UdhaarEntity?

    @Query("DELETE FROM udhaar")
    suspend fun clearUdhaar()
}