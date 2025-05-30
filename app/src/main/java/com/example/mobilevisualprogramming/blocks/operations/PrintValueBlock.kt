package com.example.mobilevisualprogramming.blocks.operations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mobilevisualprogramming.blocks.OperationBlock
import com.example.mobilevisualprogramming.blocks.renders.OperatorVisualBlock
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import com.example.mobilevisualprogramming.main.VariableData
import androidx.compose.ui.res.stringResource
import android.content.Context
import com.example.mobilevisualprogramming.R

class PrintValueBlock(availableVariables: List<VariableData>) : OperationBlock(availableVariables) {
    private var variableName by mutableStateOf("")
    private var temp by mutableStateOf("")

    private val textFieldBgColor = Color(0xFF4B2267)

    private fun validateVariableName(varName: String, context: Context) {
        if (varName.isBlank()) {
            throw IllegalArgumentException(context.getString(R.string.print_value_error_empty_name))
        }
        if (!availableVariables.any { it.name == varName }) {
            throw IllegalArgumentException(context.getString(R.string.print_value_error_var_not_found, varName))
        }
    }

    fun execute(context: Context) {
        try {
            validateVariableName(variableName, context)
            val value = availableVariables.find { it.name == variableName }?.value
                ?: throw IllegalArgumentException(context.getString(R.string.print_value_error_var_not_found_simple))
            temp = value.toString()
            error = ""
        } catch (e: IllegalArgumentException) {
            error = e.message ?: context.getString(R.string.print_value_error_output)
            temp = context.getString(R.string.print_value_temp_output)
        }
    }

    @Composable
    override fun Render(context: Context) {
        OperatorVisualBlock(
            title = stringResource(R.string.print_value_title),
            blockId = id
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.print_value_variable_label),
                    color = Color.White
                )
                TextField(
                    value = variableName,
                    onValueChange = { variableName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(0.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = textFieldBgColor,
                        unfocusedContainerColor = textFieldBgColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.print_value_output_label),
                    color = Color.White
                )
                Box(
                    modifier = Modifier
                        .background(
                            color = textFieldBgColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(0.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = temp, color = Color.White, modifier = Modifier.padding(start = 16.dp))
                }

                if (error.isNotEmpty()) {
                    Text(text = error, color = Color.Red)
                }
            }
        }.Render()
    }
}
