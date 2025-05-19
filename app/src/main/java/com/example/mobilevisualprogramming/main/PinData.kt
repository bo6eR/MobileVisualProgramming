package com.example.mobilevisualprogramming.main

import androidx.compose.ui.geometry.Offset

enum class PinType { EXEC_IN, EXEC_OUT, DATA_IN, DATA_OUT }

data class PinData(
    val type: PinType,
    var position: Offset = Offset.Zero
)