package com.example.mobilevisualprogramming.blocks.getandset

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mobilevisualprogramming.blocks.VarBlock
import com.example.mobilevisualprogramming.blocks.PinData
import com.example.mobilevisualprogramming.blocks.PinType
import com.example.mobilevisualprogramming.main.VariableData

class GetVarBlock(variable: VariableData) : VarBlock(variable) {
    init {
        pins.add(PinData(PinType.DATA_OUT))
    }

    @Composable
    override fun RenderContent() {
        Text("Get: ${variable.name} = ${variable.value}")
    }
}
