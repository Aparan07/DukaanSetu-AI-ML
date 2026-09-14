package com.example.dukaansetu.ai

data class InventoryInsight(
    val product: String,
    val currentStock: Double,
    val predictedDemand: Double,
    val recommendedRestock: Double,
    val salesTrend: String,
    val message: String
)

class InventoryInsightsManager {

    private val forecaster = DemandForecaster()
    private val insights = SmartInsights()

    fun generateInsight(
        product: String,
        currentStock: Double,
        salesHistory: List<Double>
    ): InventoryInsight {

        // Predict future demand
        val predictedDemand =
            forecaster.predictNextDemand(salesHistory)

        // Check sales trend
        val salesTrend = if (salesHistory.size >= 2) {

            val currentSales = salesHistory.takeLast(1).first()
            val previousSales = salesHistory
                .dropLast(1)
                .lastOrNull() ?: currentSales

            when {
                currentSales > previousSales -> "Increasing"
                currentSales < previousSales -> "Decreasing"
                else -> "Stable"
            }

        } else {
            "Not enough data"
        }

        // Calculate recommended restock quantity
        val recommendedRestock =
            if (currentStock < predictedDemand) {
                predictedDemand - currentStock
            } else {
                0.0
            }

        // Generate recommendation
        val message = when {

            currentStock <= 5 ->
                "$product stock is critically low. Restock recommended."

            currentStock < predictedDemand ->
                "$product may run low soon. Consider restocking."

            salesTrend == "Increasing" ->
                "$product sales are increasing. Keep extra stock."

            salesTrend == "Decreasing" ->
                "$product sales are decreasing. Avoid overstocking."

            else ->
                "$product stock level is healthy."
        }

        return InventoryInsight(
            product = product,
            currentStock = currentStock,
            predictedDemand = predictedDemand,
            recommendedRestock = recommendedRestock,
            salesTrend = salesTrend,
            message = message
        )
    }
}