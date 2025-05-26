package com.example.mobilevisualprogramming.blocks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.mobilevisualprogramming.main.VariableData


abstract class OperationBlock(private val availableVariables: Map<String, Int>) : Block(variable = VariableData(""))
{
    var error by mutableStateOf("")
}