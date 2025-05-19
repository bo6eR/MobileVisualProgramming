package com.example.mobilevisualprogramming.blocks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.mobilevisualprogramming.main.VariableData
import com.example.mobilevisualprogramming.uiblocks.VisualBlockWithPins

enum class PinType { EXEC_IN, EXEC_OUT, DATA_IN, DATA_OUT }

data class PinData(
    val type: PinType,
    var position: Offset = Offset.Zero
)

open class VarBlock(val variable: VariableData) {
    var pins = mutableListOf<PinData>()

    init {

    }

    @Composable
    open fun RenderContent() {
        Text("Значение: ${variable.value}", style = MaterialTheme.typography.bodyMedium)
    }

    @Composable
    fun Render() {
        VisualBlockWithPins(
            title = "Variable: ${variable.name}",
            pins = pins,
            modifier = Modifier
                .padding(8.dp)
        ) {
            RenderContent()
        }
    }
}
