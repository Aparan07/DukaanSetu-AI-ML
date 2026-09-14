package com.example.dukaansetu.ai

class SmartInsights {

    fun checkStock(product: String, currentStock: Double): String {

        return if (currentStock <= 5) {
            "$product stock is low. Restock recommended."
        } else {
            "$product stock level is healthy."
        }
    }

    fun compareSales(product: String, currentSales: Double, previousSales: Double): String {

        return when {
            currentSales > previousSales ->
                "$product sales are increasing."

            currentSales < previousSales ->
                "$product sales are decreasing."

            else ->
                "$product sales are stable."
        }
    }
}