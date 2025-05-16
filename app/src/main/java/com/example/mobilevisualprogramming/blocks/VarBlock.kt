package com.example.mobilevisualprogramming.blocks

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mobilevisualprogramming.main.VariableData

@Composable
fun VarBlock(variable: VariableData) {
    VisualBlock(title = "Variable: ${variable.name}") {
        Text("Значение: ${variable.value}", style = MaterialTheme.typography.bodyMedium)
    }
}
