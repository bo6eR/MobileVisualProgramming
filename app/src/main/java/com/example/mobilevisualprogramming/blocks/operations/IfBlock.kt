package com.example.mobilevisualprogramming.blocks.operations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import com.example.mobilevisualprogramming.blocks.Block
import com.example.mobilevisualprogramming.blocks.OperationBlock
import com.example.mobilevisualprogramming.blocks.setters.SetVarBlock
import com.example.mobilevisualprogramming.blocks.operators.OperatorBlock
import com.example.mobilevisualprogramming.blocks.renders.OperatorVisualBlock
import com.example.mobilevisualprogramming.main.VariableData
import kotlin.math.min
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text


class IfBlock(availableVariables: List<VariableData>) : OperationBlock(availableVariables) {
    private var condition by mutableStateOf("")
    var step by mutableStateOf("1")
    private var lastExecutionResult by mutableStateOf("...")

    private val comparisonOperators = listOf(">", "<", ">=", "<=", "==", "!=")
    private val logicalOperators = listOf("&&", "||")

    fun execute(placedBlocks: List<Block>, currentBlockId: Int) {
        try
        {
            validateInputs()
            val conditionResult = evaluateCondition()
            lastExecutionResult = if (conditionResult) "Условие истинно" else "Условие ложно"

            if (conditionResult)
            {
                val steps = step.toInt()
                val currentIndex = placedBlocks.indexOfFirst { it.id == currentBlockId }
                if (currentIndex != -1)
                {
                    val endIndex = min(currentIndex + steps + 1, placedBlocks.size)
                    for (i in currentIndex + 1 until endIndex)
                    {
                        when (val block = placedBlocks[i]) {
                            is PrintValueBlock -> block.execute()
                            is SetVarBlock -> block.execute()
                            is OperatorBlock -> block.execute()
                            is IfBlock -> block.execute(placedBlocks, block.id)
                        }
                    }
                }
            }
            error = ""
        } catch (e: Exception) {
            error = e.message ?: "Ошибка выполнения условия"
            lastExecutionResult = "Ошибка: $error"
        }
    }

    private fun validateInputs() {
        if (condition.isBlank()) {
            throw IllegalArgumentException("Введите условие")
        }
        if (step.isBlank()) {
            throw IllegalArgumentException("Введите шаг")
        }
        if (!step.matches(Regex("\\d+"))) {
            throw IllegalArgumentException("Шаг должен быть положительным числом")
        }
        if (step.toInt() <= 0) {
            throw IllegalArgumentException("Шаг должен быть больше 0")
        }
    }

    private fun evaluateCondition(): Boolean {
        availableVariables.forEach { variable ->
            if (condition.contains(variable.name)) {
                if (!condition.matches(Regex(".*\\b${variable.name}\\b.*"))) {
                    throw IllegalArgumentException("Неправильное использование переменной ${variable.name}")
                }
            }
        }

        var processedCondition = condition
        availableVariables.forEach { variable ->
            processedCondition = processedCondition.replace(variable.name, variable.value.toString())
        }

        if (!containsValidOperators(processedCondition)) {
            throw IllegalArgumentException("Некорректное условие. Используйте операторы: ${comparisonOperators.joinToString()}")
        }
        return try {
            val result = evaluateBooleanExpression(processedCondition)
            result
        } catch (e: Exception) {
            throw IllegalArgumentException("Невозможно вычислить условие: ${e.message}")
        }
    }

    private fun containsValidOperators(condition: String): Boolean {
        val operatorPattern = comparisonOperators.joinToString("|") { Regex.escape(it) } +
                "|" + logicalOperators.joinToString("|") { Regex.escape(it) }
        return condition.matches(Regex(".*($operatorPattern).*"))
    }

    private fun evaluateBooleanExpression(expression: String): Boolean {
        if (expression.contains("||")) {
            return expression.split("||").any { evaluateBooleanExpression(it.trim()) }
        }
        if (expression.contains("&&")) {
            return expression.split("&&").all { evaluateBooleanExpression(it.trim()) }
        }

        comparisonOperators.forEach { op ->
            if (expression.contains(op)) {
                val parts = expression.split(op)
                if (parts.size == 2) {
                    val left = parts[0].trim().toDoubleOrNull() ?: throw IllegalArgumentException("Некорректное число: ${parts[0]}")
                    val right = parts[1].trim().toDoubleOrNull() ?: throw IllegalArgumentException("Некорректное число: ${parts[1]}")

                    return when (op) {
                        ">" -> left > right
                        "<" -> left < right
                        ">=" -> left >= right
                        "<=" -> left <= right
                        "==" -> left == right
                        "!=" -> left != right
                        else -> throw IllegalArgumentException("Неизвестный оператор: $op")
                    }
                }
            }
        }

        throw IllegalArgumentException("Некорректное выражение: $expression")
    }

    private val textFieldBgColor = Color(0xFF4B2267)

    @Composable
    override fun Render() {
        OperatorVisualBlock(
            title = " Условие If:",
            blockId = id
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
            ) {
                Text(
                    text = " Условие:",
                    color = Color.White
                )
                TextField(
                    value = condition,
                    onValueChange = { condition = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(0.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = textFieldBgColor,
                        unfocusedContainerColor = textFieldBgColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = " Шаг:",
                    color = Color.White
                )
                TextField(
                    value = step,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("\\d*"))) {
                            step = newValue
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(0.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = textFieldBgColor,
                        unfocusedContainerColor = textFieldBgColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = " Результат:", color = Color.White)
                Text(text = lastExecutionResult, color = Color.White, modifier = Modifier.padding(start = 16.dp))
            }
        }.Render()
    }
}