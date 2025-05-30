package com.example.mobilevisualprogramming.blocks.renders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilevisualprogramming.R

class VarVisualBlock(
    val title: String,
    val modifier: Modifier = Modifier,
    val blockId: Int = -1,
    val content: @Composable ColumnScope.() -> Unit
) {
    private val blockBgColor @Composable get() = colorResource(id = R.color.block_bg_color)
    private val idColor @Composable get() = colorResource(id = R.color.id_color)

    @Composable
    fun Render() {
        Surface(
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
            shape = RoundedCornerShape(20.dp),
            modifier = modifier
                .width(350.dp)
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(color = blockBgColor)
                    .padding(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp)
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    if (blockId > 0) {
                        Text(
                            text = "$blockId",
                            fontSize = 50.sp,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.labelSmall,

                            color = idColor
                        )
                    }
                }
                content()
            }
        }
    }
}