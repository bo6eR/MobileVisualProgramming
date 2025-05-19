package com.example.mobilevisualprogramming.uiblocks

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.example.mobilevisualprogramming.blocks.PinData
import com.example.mobilevisualprogramming.blocks.PinType

@Composable
fun VisualBlockWithPins(
    title: String,
    pins: List<PinData>,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier) {
        Surface(
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
            modifier = Modifier
                .width(200.dp)
                .padding(4.dp)
        ) {
            Row {
                Column(Modifier.padding(4.dp)) {
                    pins.filter { it.type == PinType.EXEC_IN }.forEach { pin ->
                        PinTriangle(Color.Red, onPositionChanged = { pin.position = it })
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                Column(
                    modifier = Modifier
                        .background(Color(0xFFE0F7FA))
                        .padding(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    content()
                }

                Column(Modifier.padding(4.dp)) {
                    pins.filter { it.type == PinType.EXEC_OUT || it.type == PinType.DATA_OUT }.forEach { pin ->
                        if (pin.type == PinType.EXEC_OUT)
                            PinTriangle(Color.Green, onPositionChanged = { pin.position = it })
                        else
                            PinCircle(Color.Blue, onPositionChanged = { pin.position = it })
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
        ) {
            pins.filter { it.type == PinType.DATA_IN }.forEach { pin ->
                PinCircle(Color.Magenta, onPositionChanged = { pin.position = it })
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun PinCircle(color: Color, onPositionChanged: (Offset) -> Unit) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .background(color = color, shape = MaterialTheme.shapes.small)
            .onGloballyPositioned {
                val pos = it.localToWindow(Offset.Zero)
                onPositionChanged(pos)
            }
    )
}

@Composable
fun PinTriangle(color: Color, onPositionChanged: (Offset) -> Unit) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .onGloballyPositioned {
                val pos = it.localToWindow(Offset.Zero)
                onPositionChanged(pos)
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawPath(
                path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(size.width / 2f, 0f)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                },
                color = color
            )
        }
    }
}
