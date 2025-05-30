package com.example.mobilevisualprogramming.blocks.messages

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.DialogProperties
import com.example.mobilevisualprogramming.R
import com.example.mobilevisualprogramming.main.VariableData

@Composable
fun AddVariableDialog(
    show: Boolean,
    onAdd: (VariableData) -> Unit,
    onDismiss: () -> Unit
) {
    var newVarName by remember { mutableStateOf("") }
    val addVariableText = androidx.compose.ui.res.stringResource(id = R.string.addVariableText)
    val nameOfVarText = androidx.compose.ui.res.stringResource(id = R.string.nameOfVarText)
    val addText = androidx.compose.ui.res.stringResource(id = R.string.addText)
    val cancelText = androidx.compose.ui.res.stringResource(id = R.string.cancelText)
    val emptyText = androidx.compose.ui.res.stringResource(id = R.string.emptyText)
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text=addVariableText) },
            text = {
                TextField(
                    value = newVarName,
                    onValueChange = { newVarName = it },
                    label = { Text(text = nameOfVarText) }
                )
            },
            confirmButton = {
                Button(onClick = {
                    val trimmed = newVarName.trim()
                    if (trimmed.isNotEmpty()) {
                        onAdd(VariableData(trimmed))
                        newVarName = emptyText
                        onDismiss()
                    }
                }) {
                    Text(addText)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    newVarName = emptyText
                    onDismiss()
                }) {
                    Text(text = cancelText)
                }
            },
            properties = DialogProperties(dismissOnClickOutside = true)
        )
    }
}
