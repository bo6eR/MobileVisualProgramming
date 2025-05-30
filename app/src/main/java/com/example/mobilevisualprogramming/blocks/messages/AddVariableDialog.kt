package com.example.mobilevisualprogramming.blocks.messages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    var errorText by remember { mutableStateOf("") }
    val errorEnterVariableName = androidx.compose.ui.res.stringResource(id = R.string.error_enter_variable_name)
    val invalidVariableName = androidx.compose.ui.res.stringResource(id = R.string.invalid_variable_name)

    val addVariableText = androidx.compose.ui.res.stringResource(id = R.string.addVariableText)
    val nameOfVarText = androidx.compose.ui.res.stringResource(id = R.string.nameOfVarText)
    val addText = androidx.compose.ui.res.stringResource(id = R.string.addText)
    val cancelText = androidx.compose.ui.res.stringResource(id = R.string.cancelText)
    val emptyText = androidx.compose.ui.res.stringResource(id = R.string.emptyText)

    val variableNamePattern = Regex("^[a-zA-Z_][a-zA-Z0-9_]*$")
    fun isValidVariableName(name: String): Boolean {
        return variableNamePattern.matches(name)
    }

    if (show) {
        AlertDialog(
            onDismissRequest = {
                errorText = ""
                onDismiss()
            },
            title = { Text(text=addVariableText) },
            text = {
                Column{
                    TextField(
                        value = newVarName,
                        onValueChange = { newVarName = it
                                            if (errorText.isNotEmpty()) {
                                                errorText = ""
                                            }
                        },
                        label = { Text(text = nameOfVarText) },
                        isError = errorText.isNotEmpty()
                    )
                    if (errorText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = errorText,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }


            },
            confirmButton = {
                Button(onClick = {
                    var trimmed = newVarName.trim()
                    when {
                        trimmed.isEmpty() -> errorText = errorEnterVariableName
                        !isValidVariableName(trimmed) -> errorText = invalidVariableName
                        else -> {
                            onAdd(VariableData(trimmed))
                            newVarName = emptyText
                            errorText = ""
                            onDismiss()
                        }
                    }
                }) {
                    Text(text = addText)
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
