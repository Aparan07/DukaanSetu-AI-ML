package com.example.dukaansetu.ai

class StockUpdateManager(
    private val productDao: ProductDao
) {

    suspend fun addStock(
        product: String,
        quantity: Double
    ) {
        val existing = productDao.getProduct(product)

        val newQuantity =
            (existing?.quantity ?: 0.0) + quantity

        productDao.saveProduct(
            ProductEntity(
                product = product,
                quantity = newQuantity
            )
        )
    }

    suspend fun removeStock(
        product: String,
        quantity: Double
    ): Boolean {

        val existing = productDao.getProduct(product)
        val currentStock = existing?.quantity ?: 0.0

        if (currentStock < quantity) {
            return false
        }

        productDao.saveProduct(
            ProductEntity(
                product = product,
                quantity = currentStock - quantity
            )
        )

        return true
    }

    suspend fun getStock(product: String): Double {
        return productDao
            .getProduct(product)
            ?.quantity ?: 0.0
    }

    suspend fun getAllStock(): List<ProductEntity> {
        return productDao.getAllProducts()
    }
}