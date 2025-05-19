package com.example.mobilevisualprogramming.uiblocks

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun ConnectionLine(start: Offset, end: Offset) {
    Canvas(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
        val control1 = Offset(start.x + 100f, start.y)
        val control2 = Offset(end.x - 100f, end.y)
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(start.x, start.y)
            cubicTo(control1.x, control1.y, control2.x, control2.y, end.x, end.y)
        }
        drawPath(
            path = path,
            color = Color.Black,
            style = Stroke(width = 4f, pathEffect = PathEffect.cornerPathEffect(10f))
        )
    }
}
