package com.example.mobilevisualprogramming.blocks.operators

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mobilevisualprogramming.main.VariableData

class AdditionBlock(
    variable: VariableData,
    availableVariables: Map<String, Int>
) : OperatorBlock(variable, availableVariables) {

    override fun applyOperation(a: Int, b: Int, op: String): Int {
        return when (op) {
            "+" -> a + b
            else -> throw IllegalArgumentException("Неподдерживаемая операция: $op")
        }
    }

    override fun execute() {
        error = ""
        try {
            if (!isBracketsValid(expression)) {
                throw IllegalArgumentException("Несбалансированные скобки")
            }
            val result = evaluateExpression("${variable.value} + ($expression)")
            variable.value = result
        } catch (e: Exception) {
            error = "Ошибка: ${e.message}"
        }
    }

    @Composable
    override fun RenderContent() {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("Сложение (+) для: ${variable.name}")
            TextField(
                value = expression,
                onValueChange = { expression = it },
                placeholder = { Text("Введите выражение") }
            )
            if (error.isNotEmpty()) {
                Text(text = error, color = Color.Red)
            }
        }
    }
}