package com.example.mobilevisualprogramming.blocks

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun VarBlock(name: String) {
    VisualBlock(title = "Variable: $name") {
        Text("Значение: 0", style = MaterialTheme.typography.bodyMedium)
    }
}
