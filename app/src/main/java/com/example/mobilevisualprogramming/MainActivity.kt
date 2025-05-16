package com.example.mobilevisualprogramming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import com.example.mobilevisualprogramming.main.VariableData
import com.example.mobilevisualprogramming.blocks.VarBlock
import com.example.mobilevisualprogramming.blocks.messages.AddVariableDialog

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var variableList by remember { mutableStateOf(listOf<VariableData>()) }
                var placedBlocks by remember { mutableStateOf(listOf<VariableData>()) }

                var draggingVar by remember { mutableStateOf<VariableData?>(null) }
                var dragOffset by remember { mutableStateOf(Offset.Zero) }

                var showAddDialog by remember { mutableStateOf(false) }
                var newVarName by remember { mutableStateOf("") }

                AddVariableDialog(show = showAddDialog, onAdd = { newVar -> variableList = variableList + newVar }, onDismiss = { showAddDialog = false })

                Box(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        // 👉 Variables List
                        Column(
                            modifier = Modifier
                                .width(160.dp)
                                .fillMaxHeight()
                                .background(Color(0xFFEEEEEE))
                                .padding(8.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Variables", style = MaterialTheme.typography.titleMedium)
                                Button(
                                    onClick = { showAddDialog = true },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text("+")
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            variableList.forEach { variable ->
                                Text(
                                    text = variable.name,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .background(Color.LightGray)
                                        .fillMaxWidth()
                                        .pointerInput(variable) {
                                            detectDragGestures(
                                                onDragStart = {
                                                    draggingVar = variable
                                                    dragOffset = it
                                                },
                                                onDrag = { change, offset ->
                                                    change.consume()
                                                    dragOffset += offset
                                                },
                                                onDragEnd = {
                                                    placedBlocks = placedBlocks + draggingVar!!.copy()
                                                    draggingVar = null
                                                    dragOffset = Offset.Zero
                                                },
                                                onDragCancel = {
                                                    draggingVar = null
                                                    dragOffset = Offset.Zero
                                                }
                                            )
                                        }
                                )
                            }
                        }

                        // 🖼 Canvas
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                                .padding(16.dp)
                        ) {
                            Text("Canvas", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            placedBlocks.forEach { variable ->
                                VarBlock(variable = variable)
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }

                    // 🧲 Визуальное отображение перетаскиваемого блока
                    draggingVar?.let { variable ->
                        Box(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        dragOffset.x.toInt(),
                                        dragOffset.y.toInt()
                                    )
                                }
                                .zIndex(1f)
                        ) {
                            VarBlock(variable = variable)
                        }
                    }
                }
            }
        }
    }
}
