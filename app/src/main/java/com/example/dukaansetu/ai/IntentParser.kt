package com.example.dukaansetu.ai

class IntentParser {

    fun parse(text: String): IntentResult {

        val input = text.lowercase()

        // Sale detection
        if (input.contains("biki") ||
            input.contains("becha") ||
            input.contains("sold")
        ) {
            return IntentResult(
                intent = "SALE"
            )
        }

        // Udhaar detection
        if (input.contains("udhaar") ||
            input.contains("credit")
        ) {
            return IntentResult(
                intent = "ADD_CREDIT"
            )
        }

        // Payment detection
        if (input.contains("paise mil gaye") ||
            input.contains("payment") ||
            input.contains("paid")
        ) {
            return IntentResult(
                intent = "PAYMENT"
            )
        }

        // Unknown command
        return IntentResult(
            intent = "UNKNOWN"
        )
    }
}