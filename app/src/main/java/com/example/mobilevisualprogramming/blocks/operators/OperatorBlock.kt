package com.example.mobilevisualprogramming.blocks.operators

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mobilevisualprogramming.blocks.Block
import com.example.mobilevisualprogramming.blocks.VisualBlock
import com.example.mobilevisualprogramming.main.VariableData
import java.util.*

abstract class OperatorBlock(
    private val availableVariables: Map<String, Int>
) : Block(variable = VariableData("")) {
    var targetVarName by mutableStateOf("") // Название переменной для изменения
    var expression by mutableStateOf("")    // Выражение для операции
    var error by mutableStateOf("")         // Сообщение об ошибке

    // Проверка скобок
    protected fun isBracketsValid(s: String): Boolean {
        var balance = 0
        for (char in s) {
            when (char) {
                '(' -> balance++
                ')' -> {
                    balance--
                    if (balance < 0) return false
                }
            }
        }
        return balance == 0
    }

    // Вычисление выражения
    protected fun evaluateExpression(): Int {
        // Объединяем targetVarName и выражение в одну формулу
        val fullExpression = when (this) {
            is AdditionBlock -> "$targetVarName + ($expression)"
            is SubtractionBlock -> "$targetVarName - ($expression)"
            is MultiplicationBlock -> "$targetVarName * ($expression)"
            is DivisionBlock -> "$targetVarName / ($expression)"
            else -> throw IllegalStateException("Unknown operator type")
        }

        // Заменяем переменные на значения
        val replaced = Regex("[a-zA-Z_]\\w*").replace(fullExpression) {
            availableVariables[it.value]?.toString()
                ?: throw IllegalArgumentException("Переменная '${it.value}' не найдена")
        }

        // Проверка на недопустимые символы
        if (Regex("[^0-9+\\-*/%().\\s]").containsMatchIn(replaced)) {
            throw IllegalArgumentException("Недопустимые символы в выражении")
        }

        // Конвертируем в ОПН и вычисляем
        return evaluateRPN(toRPN(replaced))
    }

    private fun toRPN(expr: String): List<String> {
        val output = mutableListOf<String>()
        val stack = Stack<String>()
        val tokens = expr.replace(" ", "").split(Regex("(?<=[+\\-*/%()])|(?=[+\\-*/%()])"))

        for (token in tokens) {
            when {
                token.matches(Regex("\\d+")) -> output.add(token)
                token in listOf("+", "-", "*", "/", "%") -> {
                    while (!stack.isEmpty() && stack.peek() != "(" &&
                        precedence(stack.peek()) >= precedence(token)) {
                        output.add(stack.pop())
                    }
                    stack.push(token)
                }
                token == "(" -> stack.push(token)
                token == ")" -> {
                    while (stack.peek() != "(") {
                        output.add(stack.pop())
                    }
                    stack.pop()
                }
            }
        }
        while (!stack.isEmpty()) {
            output.add(stack.pop())
        }
        return output
    }

    private fun precedence(op: String): Int = when (op) {
        "+", "-" -> 1
        "*", "/", "%" -> 2
        else -> 0
    }

    private fun evaluateRPN(rpn: List<String>): Int {
        val stack = Stack<Int>()
        for (token in rpn) {
            when {
                token.matches(Regex("\\d+")) -> stack.push(token.toInt())
                else -> {
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(applyOperation(a, b, token))
                }
            }
        }
        return stack.pop()
    }

    protected abstract fun applyOperation(a: Int, b: Int, op: String): Int
    abstract fun execute(): Boolean

    @Composable
    override fun Render() {
        VisualBlock(
            title = getOperatorTitle(),
            modifier = Modifier.padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Целевая переменная:")
                TextField(
                    value = targetVarName,
                    onValueChange = { targetVarName = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Выражение:")
                TextField(
                    value = expression,
                    onValueChange = { expression = it },
                    modifier = Modifier.fillMaxWidth()
                )

                if (error.isNotEmpty()) {
                    Text(text = error, color = Color.Red)
                }
            }
        }.Render()
    }

    protected fun validateInputs() {
        if (targetVarName.isBlank()) {
            throw IllegalArgumentException("Введите название переменной")
        }
        if (expression.isBlank()) {
            throw IllegalArgumentException("Введите выражение")
        }
        if (!isBracketsValid(expression)) {
            throw IllegalArgumentException("Несбалансированные скобки")
        }
    }

    protected abstract fun getOperatorTitle(): String
}