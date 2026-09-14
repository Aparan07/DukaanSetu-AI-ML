package com.example.dukaansetu.ai

data class BillItem(
    val product: String,
    val quantity: Double? = null,
    val price: Double? = null
)

class BillParser {

    fun parse(ocrText: String): List<BillItem> {

        val items = mutableListOf<BillItem>()

        val lines = ocrText
            .lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        for (line in lines) {

            val lowerLine = line.lowercase()

            val product = when {

                lowerLine.contains("sugar") ||
                        lowerLine.contains("chini") -> "sugar"

                lowerLine.contains("rice") ||
                        lowerLine.contains("chawal") -> "rice"

                lowerLine.contains("atta") ||
                        lowerLine.contains("flour") -> "atta"

                lowerLine.contains("milk") ||
                        lowerLine.contains("doodh") -> "milk"

                lowerLine.contains("oil") ||
                        lowerLine.contains("tel") -> "oil"

                else -> null
            }

            if (product != null) {

                val cleanLine = line
                    .replace(",", "")
                    .replace("₹", "")
                    .replace("rs.", "", ignoreCase = true)
                    .replace("rs", "", ignoreCase = true)
                    .replace("rupees", "", ignoreCase = true)

                val numbers = Regex(
                    """\d+(?:\.\d+)?"""
                )
                    .findAll(cleanLine)
                    .mapNotNull {
                        it.value.toDoubleOrNull()
                    }
                    .toList()

                val quantity = numbers.firstOrNull()
                val price = numbers.getOrNull(1)

                items.add(
                    BillItem(
                        product = product,
                        quantity = quantity,
                        price = price
                    )
                )
            }
        }

        return items
    }
}