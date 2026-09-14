package com.example.dukaansetu.ai

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SaleDao {

    @Insert
    suspend fun insertSale(sale: SaleEntity)

    @Query("SELECT * FROM sales ORDER BY timestamp DESC")
    suspend fun getAllSales(): List<SaleEntity>

    @Query("SELECT * FROM sales WHERE product = :product")
    suspend fun getSalesForProduct(product: String): List<SaleEntity>

    @Query("DELETE FROM sales")
    suspend fun clearSales()
}