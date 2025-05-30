package com.example.mobilevisualprogramming.blocks

import android.content.Context
import androidx.compose.runtime.Composable
import com.example.mobilevisualprogramming.main.VariableData
import androidx.compose.runtime.*

abstract class Block(open val variable: VariableData)
{
    var id by mutableIntStateOf(-1)

    @Composable
    abstract fun Render(context: Context)

}