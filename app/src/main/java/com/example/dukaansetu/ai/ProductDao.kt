package com.example.dukaansetu.ai

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ProductDao {

    @Upsert
    suspend fun saveProduct(product: ProductEntity)

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE product = :product")
    suspend fun getProduct(product: String): ProductEntity?

    @Query("DELETE FROM products")
    suspend fun clearProducts()
}