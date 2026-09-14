package com.example.dukaansetu.ai

class DemandForecaster {

    fun predictNextDemand(sales: List<Double>): Double {

        if (sales.isEmpty()) {
            return 0.0
        }

        // Simple average-based demand prediction
        val averageSales = sales.average()

        return averageSales
    }
}