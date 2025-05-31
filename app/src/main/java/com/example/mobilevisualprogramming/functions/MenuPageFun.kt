package com.example.mobilevisualprogramming.functions

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.mobilevisualprogramming.R
import com.example.mobilevisualprogramming.blocks.Block
import com.example.mobilevisualprogramming.blocks.operations.IfBlock
import com.example.mobilevisualprogramming.blocks.operations.PrintValueBlock
import com.example.mobilevisualprogramming.blocks.operators.OperatorBlock
import com.example.mobilevisualprogramming.blocks.setters.SetVarBlock
import com.example.mobilevisualprogramming.main.VariableData
import kotlinx.coroutines.CoroutineScope
import kotlin.math.roundToInt

@Composable
fun MainPage(
    placedBlocks: SnapshotStateList<Block>,
    variablesDrawerState: DrawerState,
    scope: CoroutineScope,
    onBlockPositionChanged: () -> Unit,
    variableList: SnapshotStateList<VariableData>
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = Modifier
        .fillMaxSize()
        .transformable(
            state = rememberTransformableState { zoomChange, panChange, _ ->
                scale = (scale * zoomChange).coerceIn(0.2f, 2f)
                offset += panChange
            }
        )
        .graphicsLayer(
            scaleX = scale,
            scaleY = scale,
            translationX = offset.x,
            translationY = offset.y
        )
    ) {
        placedBlocks.forEach { block ->
            Box(
                modifier = Modifier
                    .absoluteOffset {
                        IntOffset(
                            block.variable.position.x.roundToInt(),
                            block.variable.position.y.roundToInt()
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                block.variable.position += dragAmount
                            },
                            onDragEnd = {
                                onBlockPositionChanged()
                            }
                        )
                    }
                    .pointerInput(Unit){
                        detectTapGestures(
                            onDoubleTap = {
                                if (placedBlocks.size == 1){
                                    placedBlocks.clear()
                                }
                                else{
                                    placedBlocks.removeIf { it.id == block.id }
                                }
                                onBlockPositionChanged()
                            }
                        )
                    }
            ) {
                block.Render()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) { ButtonOfMenuOpening(scope, variablesDrawerState) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp, end = 16.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        FloatingActionButton(
            onClick = {
                var currentIndex = 0
                val sortedBlocks = placedBlocks.sortedBy { it.id }
                while (currentIndex < sortedBlocks.size) {
                    when (val block = sortedBlocks[currentIndex])
                    {
                        is PrintValueBlock -> block.execute()
                        is SetVarBlock -> block.execute()
                        is OperatorBlock -> block.execute()
                        is IfBlock ->
                        {
                            block.execute(sortedBlocks, block.id)
                            val steps = block.step.toIntOrNull() ?: 0
                            currentIndex += steps
                        }
                    }
                    currentIndex++
                }
                variableList.forEach {
                    it.value = 0
                }
                placedBlocks.forEach { block->
                    when (block)
                    {
                        is PrintValueBlock -> block.updateAvailableVariables(variableList)
                        is SetVarBlock -> block.updateAvailableVariables(variableList)
                        is OperatorBlock -> block.updateAvailableVariables(variableList)
                        is IfBlock -> block.updateAvailableVariables(variableList)
                    }
                }
            },
            modifier = Modifier,
            containerColor = Color(0xFF4CAF50),
            contentColor = Color.White
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_play),
                contentDescription = "Start execution"
            )
        }
    }
}