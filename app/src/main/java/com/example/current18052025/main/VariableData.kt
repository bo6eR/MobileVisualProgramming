package com.example.mobilevisualprogramming.main

import androidx.compose.ui.geometry.Offset

data class VariableData(
    val name: String,
    val value: Int = 0,
    var position: Offset = Offset.Zero
)