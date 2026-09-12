
package com.example.dukaansetu.ai

data class IntentResult(
    val intent: String,
    val product: String? = null,
    val quantity: Double? = null,
    val customer: String? = null,
    val amount: Double? = null
)