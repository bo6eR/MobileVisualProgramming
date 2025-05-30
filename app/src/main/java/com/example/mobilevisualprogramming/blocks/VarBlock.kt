package com.example.mobilevisualprogramming.blocks

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mobilevisualprogramming.R
import com.example.mobilevisualprogramming.blocks.renders.VarVisualBlock
import com.example.mobilevisualprogramming.main.VariableData

open class VarBlock(override val variable: VariableData) : Block(variable) {
    @Composable
    open fun RenderContent() {
        Text(text = stringResource(R.string.value_display, variable.value.toString()), style = MaterialTheme.typography.bodyMedium)
    }

    @Composable
    override fun Render(context: Context) {
        VarVisualBlock(
            title = stringResource(R.string.variable_display, variable.name),
            modifier = Modifier.padding(8.dp),
            blockId = id
        ) {
            RenderContent()
        }.Render()
    }
}