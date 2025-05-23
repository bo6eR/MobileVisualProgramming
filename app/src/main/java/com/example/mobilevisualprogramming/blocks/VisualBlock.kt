package com.example.mobilevisualprogramming.blocks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class VisualBlock(
    val title: String,
    val modifier: Modifier = Modifier,
    val blockId: Int = -1,
    val content: @Composable ColumnScope.() -> Unit
) {
    @Composable
    fun Render() {
        Surface(
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
            modifier = modifier
                .width(200.dp)
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        if (blockId > 0) Color(0xFFE0F7FA).copy(alpha = 0.7f)
                        else Color(0xFFE0F7FA)
                    )
                    .padding(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    if (blockId > 0) {
                        Text(
                            text = "#$blockId",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Blue,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp)
                        )
                    }
                }
                content()
            }
        }
    }
}