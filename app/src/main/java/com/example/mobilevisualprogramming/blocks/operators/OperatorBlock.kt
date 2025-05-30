package com.example.mobilevisualprogramming.blocks.operators

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mobilevisualprogramming.blocks.renders.OperatorVisualBlock
import java.util.*
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.example.mobilevisualprogramming.blocks.OperationBlock
import com.example.mobilevisualprogramming.main.VariableData
import com.example.mobilevisualprogramming.R

abstract class OperatorBlock(
    override var availableVariables: List<VariableData>
) : OperationBlock(availableVariables) {
    var targetVarName by mutableStateOf("")
    private var expression by mutableStateOf("")

    private fun isBracketsValid(s: String): Boolean {
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

    protected fun evaluateExpression(context: Context): Int
    {
        println("1")
        val fullExpression = when (this) {
            is AdditionBlock -> "$targetVarName + ($expression)"
            is SubtractionBlock -> "$targetVarName - ($expression)"
            is MultiplicationBlock -> "$targetVarName * ($expression)"
            is DivisionBlock -> "$targetVarName / ($expression)"
            else -> throw IllegalArgumentException(context.getString(R.string.error_invalid_operator_type))
        }
        println("2")
        val replaced = Regex("[a-zA-Z_]\\w*").replace(fullExpression) {
            availableVariables.find { varData -> varData.name == it.value }?.value?.toString()
                ?: throw IllegalArgumentException(context.getString(R.string.error_variable_not_found, it.value))
        }
        println("3")
        if (Regex("[^0-9+\\-*/%().\\s]").containsMatchIn(replaced)) {
            throw IllegalArgumentException(context.getString(R.string.error_invalid_characters_in_expression))

        }
        println("4")
        return evaluateRPN(toRPN(replaced), context)
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

    private fun evaluateRPN(rpn: List<String>, context: Context): Int {
        val stack = Stack<Int>()
        for (token in rpn) {
            when {
                token.matches(Regex("\\d+")) -> stack.push(token.toInt())
                else -> {
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(applyOperation(a, b, token, context))
                }
            }
        }
        return stack.pop()
    }

    protected abstract fun applyOperation(a: Int, b: Int, op: String, context: Context): Int
    abstract fun execute(context: Context): Boolean

    private val textFieldBgColor @Composable get() = colorResource(id = R.color.text_field_bg_color)

    @Composable
    override fun Render(context: Context) {
        OperatorVisualBlock(
            title = getOperatorTitle(context),
            blockId = id
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.label_variable),
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
                    text = stringResource(R.string.label_expression),
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

    protected fun validateInputs(context: Context) {
        if (targetVarName.isBlank()) {
            throw IllegalArgumentException(context.getString(R.string.error_enter_variable_name))
        }
        if (expression.isBlank()) {
            throw IllegalArgumentException(context.getString(R.string.error_enter_expression))
        }
        if (!isBracketsValid(expression)) {
            throw IllegalArgumentException(context.getString(R.string.error_unbalanced_brackets))
        }
    }

    protected abstract fun getOperatorTitle(context: Context): String
}