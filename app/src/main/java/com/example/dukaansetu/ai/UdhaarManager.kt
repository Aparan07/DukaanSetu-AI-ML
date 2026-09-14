package com.example.dukaansetu.ai

class UdhaarManager(
    private val udhaarDao: UdhaarDao
) {

    suspend fun addCredit(
        customer: String,
        amount: Double
    ) {
        val existing = udhaarDao.getUdhaar(customer)

        val newBalance =
            (existing?.balance ?: 0.0) + amount

        udhaarDao.saveUdhaar(
            UdhaarEntity(
                customer = customer,
                balance = newBalance
            )
        )
    }

    suspend fun recordPayment(
        customer: String,
        amount: Double
    ): Boolean {

        val existing = udhaarDao.getUdhaar(customer)
        val currentBalance = existing?.balance ?: 0.0

        if (amount > currentBalance) {
            return false
        }

        udhaarDao.saveUdhaar(
            UdhaarEntity(
                customer = customer,
                balance = currentBalance - amount
            )
        )

        return true
    }

    suspend fun getBalance(
        customer: String
    ): Double {
        return udhaarDao
            .getUdhaar(customer)
            ?.balance ?: 0.0
    }

    suspend fun getAllBalances(): List<UdhaarEntity> {
        return udhaarDao.getAllUdhaar()
    }
}