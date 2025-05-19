package com.example.mobilevisualprogramming.uiblocks

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ExecPin(
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    isInput: Boolean = true
) {
    Canvas(modifier.size(size)) {
        val path = Path().apply {
            val centerY = size.toPx() / 2
            val width = size.toPx()
            val height = size.toPx()
            if (isInput) {
                // Входящий — направлен вправо
                moveTo(0f, centerY)
                lineTo(width, 0f)
                lineTo(width, height)
            } else {
                // Исходящий — направлен влево
                moveTo(width, centerY)
                lineTo(0f, 0f)
                lineTo(0f, height)
            }
            close()
        }
        drawPath(path = path, color = color)
    }
}

@Composable
fun ValuePinCircle(
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 10.dp
) {
    Canvas(modifier.size(size)) {
        drawCircle(color = color, radius = size.toPx() / 2)
    }
}
