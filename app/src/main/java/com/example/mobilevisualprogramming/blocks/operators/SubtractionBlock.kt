package com.example.mobilevisualprogramming.blocks.operators

import com.example.mobilevisualprogramming.main.VariableData

class SubtractionBlock(
    availableVariables: List<VariableData>
) : OperatorBlock(availableVariables) {

    override fun applyOperation(a: Int, b: Int, op: String): Int = when (op) {
        "+" -> a + b
        "*" -> a * b
        "/" -> a / b
        "-" -> a - b
        else -> throw IllegalArgumentException("Неверная операция: $op")
    }

    override fun execute(): Boolean {
        error = ""
        return try {
            validateInputs()
            val result = evaluateExpression()
            availableVariables.find { it.name == targetVarName }?.value = result
            true
        } catch (e: Exception) {
            error = "Ошибка: ${e.message}"
            false
        }
    }

    override fun getOperatorTitle(): String = "Вычитание (-)"
}