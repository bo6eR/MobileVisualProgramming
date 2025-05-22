package com.example.mobilevisualprogramming.blocks

import androidx.compose.runtime.Composable
import com.example.mobilevisualprogramming.main.VariableData

abstract class Block(open val variable: VariableData) {

    @Composable
    abstract fun Render()

}