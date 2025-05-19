package com.example.mobilevisualprogramming.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

class VariableData(
    val name: String,
    var value: Int = 0,
    position: Offset = Offset.Zero
) {
    var position by mutableStateOf(position)
}