package com.example.dukaansetu.ai

class AIManager {

    private val intentParser = IntentParser()

    fun processInput(text: String): IntentResult {
        return intentParser.parse(text)
    }
}