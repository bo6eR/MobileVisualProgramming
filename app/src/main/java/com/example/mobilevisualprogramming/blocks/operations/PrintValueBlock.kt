package com.example.mobilevisualprogramming.blocks.operations

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mobilevisualprogramming.blocks.OperationBlock
import com.example.mobilevisualprogramming.blocks.render.OperatorVisualBlock
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.mobilevisualprogramming.main.VariableData

class PrintValueBlock(availableVariables: List<VariableData>) : OperationBlock(availableVariables)
{
    var variableName by mutableStateOf("")

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

    protected fun validateVariableName(varName: String) {
        if (varName.isBlank()) {
            throw IllegalArgumentException("Введите название переменной")
        }
        if (!availableVariables.any { it.name == varName }) {
            throw IllegalArgumentException("Переменная '$varName' не найдена")
        }
    }

    fun execute() {
        try {
            validateVariableName(variableName)
            val value = availableVariables.find { it.name == variableName }?.value
                ?: throw IllegalArgumentException("Переменная не найдена")
            println("Значение переменной $variableName: $value")
            error = ""
        } catch (e: IllegalArgumentException) {
            error = e.message ?: "Ошибка при выводе значения"
        }
    }

    @Composable
    override fun Render() {
        OperatorVisualBlock(
            title = "Print Value",
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
                    value = variableName,
                    onValueChange = { variableName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(0.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF4B2267),
                        unfocusedContainerColor = Color(0xFF4B2267),
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
}