package com.example.mobilevisualprogramming.blocks.operators

import com.example.mobilevisualprogramming.main.VariableData

class DivisionBlock(
    availableVariables: List<VariableData>
) : OperatorBlock(availableVariables) {

    override fun applyOperation(a: Int, b: Int, op: String): Int = when (op) {
        "/" -> {
            if (b == 0) throw ArithmeticException("Деление на ноль")
            a / b
        }
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

    override fun getOperatorTitle(): String = "Деление (/)"
}