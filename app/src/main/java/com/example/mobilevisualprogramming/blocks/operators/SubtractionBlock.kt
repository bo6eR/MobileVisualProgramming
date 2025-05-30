package com.example.mobilevisualprogramming.blocks.operators

import android.content.Context
import com.example.mobilevisualprogramming.R
import com.example.mobilevisualprogramming.main.VariableData

class SubtractionBlock(
    availableVariables: List<VariableData>
) : OperatorBlock(availableVariables) {

    override fun applyOperation(a: Int, b: Int, op: String, context: Context): Int = when (op) {
        "+" -> a + b
        "*" -> a * b
        "/" -> a / b
        "-" -> a - b
        else -> throw IllegalArgumentException(context.getString(R.string.invalid_operation, op))
    }

    override fun execute(context: Context): Boolean {
        error = ""
        return try {
            validateInputs(context)
            val result = evaluateExpression(context)
            availableVariables.find { it.name == targetVarName }?.value = result
            true
        } catch (e: Exception) {
            error = context.getString(R.string.if_block_error, e.message)
            false
        }
    }

    override fun getOperatorTitle(context: Context): String = context.getString(R.string.vychitanie)
}