package com.example.mobilevisualprogramming.blocks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobilevisualprogramming.blocks.render.VarVisualBlock
import com.example.mobilevisualprogramming.main.VariableData

open class VarBlock(override val variable: VariableData) : Block(variable) {
    @Composable
    open fun RenderContent() {
        Text("Значение: ${variable.value}", style = MaterialTheme.typography.bodyMedium)
    }

    @Composable
    override fun Render() {
        VarVisualBlock(
            title = "Переменная: ${variable.name}",
            modifier = Modifier.padding(8.dp),
            blockId = id
        ) {
            RenderContent()
        }.Render()
    }
}