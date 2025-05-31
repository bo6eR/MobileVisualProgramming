package com.example.mobilevisualprogramming.blocks.setters

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.mobilevisualprogramming.blocks.VarBlock
import com.example.mobilevisualprogramming.main.VariableData
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import java.util.*

val textFieldBgColor = Color(0xFF4B2267)

class SetVarBlock(
    override val variable: VariableData,
    private var availableVariables: List<VariableData>
) : VarBlock(variable) {
    private var expression by mutableStateOf("")
    private var error by mutableStateOf("")

    override fun updateAvailableVariables(newVariables: List<VariableData>) {
        super.updateAvailableVariables(newVariables)
        availableVariables = newVariables
    }

    private fun evaluateExpression(expr: String, vars: List<VariableData>): Int {
        val replaced = Regex("[a-zA-Z_]\\w*").replace(expr) {
            val name = it.value
            vars.find { it.name == name }?.toString()
                ?: throw IllegalArgumentException("Переменная \"$name\" не объявлена")
        }

        if (Regex("[^0-9+\\-*/%().\\s]").containsMatchIn(replaced)) {
            throw IllegalArgumentException("Недопустимые символы в выражении")
        }

        val rpn = toRPN(replaced)
        return evalRPN(rpn)
    }

    private fun toRPN(expr: String): List<String> {
        val output = mutableListOf<String>()
        val stack = Stack<String>()
        val tokens = expr.replace(" ", "").split(Regex("(?<=[+\\-*/%()])|(?=[+\\-*/%()])"))

        for (token in tokens) {
            when {
                token.matches(Regex("\\d+")) -> output.add(token)
                token in listOf("+", "-", "*", "/", "%") -> {
                    while (!stack.isEmpty() && stack.peek() != "(" && (precedence(stack.peek()) >= precedence(
                            token
                        ))
                    ) {
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

    private fun precedence(op: String): Int {
        return when (op) {
            "+", "-" -> 1
            "*", "/", "%" -> 2
            else -> 0
        }
    }

    private fun evalRPN(rpn: List<String>): Int {
        val stack = Stack<Int>()
        for (token in rpn) {
            when {
                token.matches(Regex("\\d+")) -> stack.push(token.toInt())
                else -> {
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(
                        when (token) {
                            "+" -> a + b
                            "-" -> a - b
                            "*" -> a * b
                            "/" -> a / b
                            "%" -> a % b
                            else -> throw IllegalArgumentException("Неизвестный оператор: $token")
                        }
                    )
                }
            }
        }
        return stack.pop()
    }

    override fun execute() {
        error = ""
        try {
            if (!isBracketsValid(expression)) {
                throw IllegalArgumentException("Несбалансированные скобки")
            }
            val result = evaluateExpression(expression, availableVariables)
            variable.value = result
            availableVariables.find { it.name == variable.name }?.value = result
        } catch (e: Exception) {
            error = "Ошибка: ${e.message}"
        }
    }

    @Composable
    override fun RenderContent() {
        Column(modifier = Modifier) {
            Row(
                modifier = Modifier
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${variable.name} = ",
                    color = Color.White

                )
                TextField(
                    value = expression,
                    onValueChange = { expression = it },
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


            }
            if (error.isNotEmpty()) {
                Text(text = error, color = Color.Red)
            }
        }

    }
}