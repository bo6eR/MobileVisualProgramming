package com.example.mobilevisualprogramming.blocks.operators

import com.example.mobilevisualprogramming.main.VariableData

class MultiplicationBlock(
    availableVariables: List<VariableData>
) : OperatorBlock(availableVariables) {

    override fun applyOperation(a: Int, b: Int, op: String): Int = when (op) {
        "*" -> a * b
        else -> throw IllegalArgumentException("Unsupported operation: $op")
    }

    override fun getOperatorTitle(): String = "Умножение (*)"
}