package com.example.dukaansetu.ai

class IntentParser {

    fun parse(text: String): IntentResult {

        val input = text.lowercase().trim()

        // ---------------- SALE ----------------

        if (
            input.contains("biki") ||
            input.contains("bik gaya") ||
            input.contains("bik gya") ||
            input.contains("becha") ||
            input.contains("bech diya") ||
            input.contains("sold") ||
            input.contains("sale") ||
            input.contains("diya")
        ) {

            val quantity = Regex(
                """(\d+(?:\.\d+)?)\s*(kg|kilo|kilogram|litre|liter|ltr|pcs|piece|packet)?"""
            )
                .find(input)
                ?.groupValues
                ?.get(1)
                ?.toDoubleOrNull()

            val product = findProduct(input)

            return IntentResult(
                intent = "SALE",
                product = product,
                quantity = quantity
            )
        }

        // ---------------- PAYMENT ----------------

        if (
            input.contains("paise mil gaye") ||
            input.contains("paise de diye") ||
            input.contains("payment") ||
            input.contains("paid") ||
            input.contains("jama") ||
            input.contains("jama kar diye") ||
            input.contains("mil gaye") ||
            input.contains("de diye") ||
            input.contains("payment received")
        ) {

            val amount = extractAmount(input)
            val customer = findCustomer(input)

            return IntentResult(
                intent = "PAYMENT",
                customer = customer,
                amount = amount
            )
        }

        // ---------------- UDHAAR ----------------

        if (
            input.contains("udhaar") ||
            input.contains("udhar") ||
            input.contains("credit") ||
            input.contains("baaki") ||
            input.contains("khata") ||
            input.contains("credit pe")
        ) {

            val amount = extractAmount(input)
            val customer = findCustomer(input)

            return IntentResult(
                intent = "ADD_CREDIT",
                customer = customer,
                amount = amount
            )
        }

        // ---------------- UNKNOWN ----------------

        return IntentResult(
            intent = "UNKNOWN"
        )
    }

    // Product detection
    private fun findProduct(input: String): String? {

        return when {

            input.contains("sugar") ||
                    input.contains("chini") -> "sugar"

            input.contains("rice") ||
                    input.contains("chawal") -> "rice"

            input.contains("atta") ||
                    input.contains("flour") -> "atta"

            input.contains("milk") ||
                    input.contains("doodh") -> "milk"

            else -> null
        }
    }

    // Customer detection
    private fun findCustomer(input: String): String? {

        return when {

            input.contains("ramesh") -> "Ramesh"

            input.contains("suresh") -> "Suresh"

            input.contains("mohan") -> "Mohan"

            else -> null
        }
    }

    // Amount extraction
    private fun extractAmount(input: String): Double? {

        return Regex(
            """(?:₹|rs\.?|rupees?)?\s*(\d+(?:\.\d+)?)"""
        )
            .find(input)
            ?.groupValues
            ?.get(1)
            ?.toDoubleOrNull()
    }
}