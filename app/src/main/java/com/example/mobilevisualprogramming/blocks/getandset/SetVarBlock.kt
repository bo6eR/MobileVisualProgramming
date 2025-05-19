package com.example.mobilevisualprogramming.blocks.getandset

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mobilevisualprogramming.blocks.VarBlock
import com.example.mobilevisualprogramming.blocks.PinData
import com.example.mobilevisualprogramming.blocks.PinType
import com.example.mobilevisualprogramming.main.VariableData

class SetVarBlock(variable: VariableData) : VarBlock(variable) {
    init {
        pins.add(PinData(PinType.EXEC_IN))   // Вход exec
        pins.add(PinData(PinType.EXEC_OUT))  // Выход exec
        pins.add(PinData(PinType.DATA_IN))   // Вход value
    }

    @Composable
    override fun RenderContent() {
        Text("Set: ${variable.name} <- ${variable.value}")
    }
}
