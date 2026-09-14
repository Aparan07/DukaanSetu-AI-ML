package com.example.dukaansetu.ai

class SaleManager(
    private val saleDao: SaleDao
) {

    suspend fun recordSale(
        product: String,
        quantity: Double
    ) {
        saleDao.insertSale(
            SaleEntity(
                product = product,
                quantity = quantity
            )
        )
    }

    suspend fun getAllSales(): List<SaleEntity> {
        return saleDao.getAllSales()
    }

    suspend fun getSalesForProduct(
        product: String
    ): List<SaleEntity> {
        return saleDao.getSalesForProduct(product)
    }
}