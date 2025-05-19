package com.example.mobilevisualprogramming.blocks.messages

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.DialogProperties
import com.example.mobilevisualprogramming.main.VariableData

@Composable
fun AddVariableDialog(
    show: Boolean,
    onAdd: (VariableData) -> Unit,
    onDismiss: () -> Unit
) {
    var newVarName by remember { mutableStateOf("") }

    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Добавить переменную") },
            text = {
                TextField(
                    value = newVarName,
                    onValueChange = { newVarName = it },
                    label = { Text("Имя переменной") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    val trimmed = newVarName.trim()
                    if (trimmed.isNotEmpty()) {
                        onAdd(VariableData(trimmed))
                        newVarName = ""
                        onDismiss()
                    }
                }) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    newVarName = ""
                    onDismiss()
                }) {
                    Text("Отмена")
                }
            },
            properties = DialogProperties(dismissOnClickOutside = true)
        )
    }
}
