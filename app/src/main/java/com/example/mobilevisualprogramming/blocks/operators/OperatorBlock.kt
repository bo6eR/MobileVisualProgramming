package com.example.mobilevisualprogramming.blocks.operators

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mobilevisualprogramming.blocks.render.OperatorVisualBlock
import java.util.*
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextField
import com.example.mobilevisualprogramming.blocks.OperationBlock


abstract class OperatorBlock(
    private val availableVariables: Map<String, Int>
) : OperationBlock(availableVariables) {
    var targetVarName by mutableStateOf("")
    var expression by mutableStateOf("")

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

    val blockBgColor = Color(0xFF6750A4)
    val textFieldBgColor = Color(0xFF4B2267)
    val placeholderColor = Color(0xFF6D6D6D)
    val idColor = Color(0xFFFBE200)

    @Composable
    override fun Render() {
        OperatorVisualBlock(
            title = getOperatorTitle(),
            modifier = Modifier.padding(8.dp),
            blockId = id
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
            ) {
                Text(
                    text = " Переменная:",
                    color = Color.White
                )
                TextField(
                    value = targetVarName,
                    onValueChange = { targetVarName = it },
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
                    text = " Выражение:",
                    color = Color.White
                )
                TextField(
                    value = expression,
                    onValueChange = { expression = it },
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