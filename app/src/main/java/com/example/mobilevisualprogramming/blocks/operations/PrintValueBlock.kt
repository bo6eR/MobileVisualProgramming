package com.example.mobilevisualprogramming.blocks.operations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mobilevisualprogramming.blocks.Block
import com.example.mobilevisualprogramming.blocks.VisualBlock
import com.example.mobilevisualprogramming.main.VariableData

class PrintValueBlock(
    private val availableVariables: List<VariableData>
) : Block(variable = VariableData("PrintBlock")) {

    var variableName by mutableStateOf("")  // Имя переменной для вывода
    var error by mutableStateOf("")        // Сообщение об ошибке

    fun execute() {
        error = ""
        try {
            if (variableName.isBlank()) {
                throw IllegalArgumentException("Введите название переменной")
            }

            val variable = availableVariables.find { it.name == variableName }
                ?: throw IllegalArgumentException("Переменная '$variableName' не найдена")

            println("Значение переменной ${variable.name}: ${variable.value}")
        } catch (e: Exception) {
            error = "Ошибка: ${e.message}"
        }
    }

    @Composable
    override fun Render() {
        VisualBlock(
            title = "Вывод значения",
            modifier = Modifier.padding(8.dp),
            blockId = id
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Имя переменной для вывода:")
                TextField(
                    value = variableName,
                    onValueChange = { variableName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Введите имя переменной") }
                )

                if (error.isNotEmpty()) {
                    Text(text = error, color = Color.Red)
                }
            }
        }.Render()
    }
}