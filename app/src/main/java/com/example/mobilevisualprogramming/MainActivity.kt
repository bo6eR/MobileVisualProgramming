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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.mobilevisualprogramming.blocks.VarBlock

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var placedBlocks by remember { mutableStateOf(listOf<String>()) }
                var draggingVar by remember { mutableStateOf<String?>(null) }

                // 👉 Список переменных (можно добавлять)
                var variableList by remember { mutableStateOf(listOf("a", "b", "c")) }

                // Для диалога
                var showAddDialog by remember { mutableStateOf(false) }
                var newVarName by remember { mutableStateOf("") }

                if (showAddDialog) {
                    AlertDialog(
                        onDismissRequest = { showAddDialog = false },
                        title = { Text("Добавить переменную") },
                        text = {
                            TextField(
                                value = newVarName,
                                onValueChange = { newVarName = it },
                                label = { Text("Имя переменной") }
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (newVarName.isNotBlank()) {
                                        variableList = variableList + newVarName.trim()
                                    }
                                    newVarName = ""
                                    showAddDialog = false
                                }
                            ) {
                                Text("Добавить")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAddDialog = false }) {
                                Text("Отмена")
                            }
                        }
                    )
                }

                Row(modifier = Modifier.fillMaxSize()) {
                    // 👉 Variables List с кнопкой "+"
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
                                text = variable,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .background(Color.LightGray)
                                    .fillMaxWidth()
                                    .pointerInput(Unit) {
                                        detectDragGestures(
                                            onDragStart = { draggingVar = variable },
                                            onDrag = { _, _ -> },
                                            onDragEnd = { draggingVar = null },
                                            onDragCancel = { draggingVar = null }
                                        )
                                    }
                            )
                        }
                    }

                    // 🖼 Канвас
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(16.dp)
                            .pointerInput(draggingVar) {
                                detectDragGestures(
                                    onDrag = { _, _ -> },
                                    onDragEnd = {
                                        draggingVar?.let {
                                            placedBlocks = placedBlocks + it
                                            draggingVar = null
                                        }
                                    },
                                    onDragCancel = {
                                        draggingVar = null
                                    }
                                )
                            }
                    ) {
                        Text("Canvas", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        placedBlocks.forEach { name ->
                            VarBlock(name = name)
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}
