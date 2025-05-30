package com.example.mobilevisualprogramming.blocks.operations

import androidx.compose.foundation.background
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
import com.example.mobilevisualprogramming.blocks.renders.OperatorVisualBlock
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import com.example.mobilevisualprogramming.main.VariableData

class PrintValueBlock(availableVariables: List<VariableData>) : OperationBlock(availableVariables)
{
    private var variableName by mutableStateOf("")
    private var temp by mutableStateOf("...временный вывод...")

    private fun validateVariableName(varName: String) {
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
            temp = value.toString()
            error = ""
        } catch (e: IllegalArgumentException) {
            error = e.message ?: "Ошибка при выводе значения"
            temp = "...временный вывод..."
        }
    }
    private val textFieldBgColor = Color(0xFF4B2267)

    @Composable
    override fun Render() {
        OperatorVisualBlock(
            title = " Печать:",
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
                        focusedContainerColor = textFieldBgColor,
                        unfocusedContainerColor = textFieldBgColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = " Вывод:",
                    color = Color.White
                )
                Box(
                    modifier = Modifier
                        .background(
                        color = textFieldBgColor,
                        shape = RoundedCornerShape(12.dp)
                        )
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(0.dp),
                    contentAlignment = Alignment.CenterStart
                ){
                    Text(text = temp, color = Color.White, modifier = Modifier.padding(start = 16.dp))
                }

                if (error.isNotEmpty()) {
                    Text(text = error, color = Color.Red)
                }
            }
        }.Render()
    }
}