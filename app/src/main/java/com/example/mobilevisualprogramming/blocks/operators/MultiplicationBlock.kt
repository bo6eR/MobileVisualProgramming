package com.example.mobilevisualprogramming.blocks.operators

class MultiplicationBlock(
    availableVariables: Map<String, Int>
) : OperatorBlock(availableVariables) {

    override fun applyOperation(a: Int, b: Int, op: String): Int = when (op) {
        "*" -> a * b
        else -> throw IllegalArgumentException("Unsupported operation: $op")
    }

    override fun execute(): Boolean {
        error = ""
        return try {
            validateInputs()
            val result = evaluateExpression()
            // Логика сохранения результата
            true
        } catch (e: Exception) {
            error = "Ошибка: ${e.message}"
            false
        }
    }

    override fun getOperatorTitle(): String = "Умножение (*)"
}