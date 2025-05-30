package com.example.mobilevisualprogramming.blocks.operations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import com.example.mobilevisualprogramming.blocks.Block
import com.example.mobilevisualprogramming.blocks.OperationBlock
import com.example.mobilevisualprogramming.blocks.setters.SetVarBlock
import com.example.mobilevisualprogramming.blocks.operators.OperatorBlock
import com.example.mobilevisualprogramming.blocks.renders.OperatorVisualBlock
import com.example.mobilevisualprogramming.main.VariableData
import kotlin.math.min
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import android.content.Context
import androidx.compose.ui.res.colorResource
import com.example.mobilevisualprogramming.R

class IfBlock(availableVariables: List<VariableData>) : OperationBlock(availableVariables) {
    private var condition by mutableStateOf("")
    var step by mutableStateOf("1")
    private var lastExecutionResult by mutableStateOf("...")

    private val comparisonOperators = listOf(
        ">",
        "<",
        ">=",
        "<=",
        "==",
        "!="
    )
    private val logicalOperators = listOf(
        "&&",
        "||"
    )

    fun execute(context: Context, placedBlocks: List<Block>, currentBlockId: Int) {
        try
        {
            validateInputs(context)
            val conditionResult = evaluateCondition(context)
            lastExecutionResult = if (conditionResult)
                context.getString(R.string.if_block_condition_true)
            else
                context.getString(R.string.if_block_condition_false)

            if (conditionResult)
            {
                val steps = step.toInt()
                val currentIndex = placedBlocks.indexOfFirst { it.id == currentBlockId }
                if (currentIndex != -1)
                {
                    val endIndex = min(currentIndex + steps + 1, placedBlocks.size)
                    for (i in currentIndex + 1 until endIndex)
                    {
                        when (val block = placedBlocks[i]) {
                            is PrintValueBlock -> block.execute(context)
                            is SetVarBlock -> block.execute(context)
                            is OperatorBlock -> block.execute(context)
                            is IfBlock -> block.execute(context, placedBlocks, block.id)
                        }
                    }
                }
            }
            error = ""
        } catch (e: Exception) {
            error = e.message ?: context.getString(R.string.if_block_error_execution)
            lastExecutionResult = context.getString(R.string.if_block_error, error)
        }
    }

    private fun validateInputs(context: Context) {
        if (condition.isBlank()) {
            throw IllegalArgumentException(context.getString(R.string.if_block_error_enter_condition))
        }
        if (step.isBlank()) {
            throw IllegalArgumentException(context.getString(R.string.if_block_error_enter_step))
        }
        if (!step.matches(Regex("\\d+"))) {
            throw IllegalArgumentException(context.getString(R.string.if_block_error_step_positive))
        }
        if (step.toInt() <= 0) {
            throw IllegalArgumentException(context.getString(R.string.if_block_error_step_gt_zero))
        }
    }

    private fun evaluateCondition(context: Context): Boolean {
        availableVariables.forEach { variable ->
            if (condition.contains(variable.name)) {
                if (!condition.matches(Regex(".*\\b${variable.name}\\b.*"))) {
                    throw IllegalArgumentException(
                        context.getString(R.string.if_block_error_var_usage, variable.name)
                    )
                }
            }
        }

        var processedCondition = condition
        availableVariables.forEach { variable ->
            processedCondition = processedCondition.replace(variable.name, variable.value.toString())
        }

        if (!containsValidOperators(processedCondition)) {
            throw IllegalArgumentException(
                context.getString(
                    R.string.if_block_error_invalid_condition,
                    comparisonOperators.joinToString()
                )
            )
        }
        return try {
            val result = evaluateBooleanExpression(context, processedCondition)
            result
        } catch (e: Exception) {
            throw IllegalArgumentException(
                context.getString(R.string.if_block_error_eval_condition, e.message ?: "")
            )
        }
    }

    private fun containsValidOperators(condition: String): Boolean {
        val operatorPattern = comparisonOperators.joinToString("|") { Regex.escape(it) } +
                "|" + logicalOperators.joinToString("|") { Regex.escape(it) }
        return condition.matches(Regex(".*($operatorPattern).*"))
    }

    private fun evaluateBooleanExpression(context: Context, expression: String): Boolean {
        if (expression.contains("||")) {
            return expression.split("||").any { evaluateBooleanExpression(context, it.trim()) }
        }
        if (expression.contains("&&")) {
            return expression.split("&&").all { evaluateBooleanExpression(context, it.trim()) }
        }

        comparisonOperators.forEach { op ->
            if (expression.contains(op)) {
                val parts = expression.split(op)
                if (parts.size == 2) {
                    val left = parts[0].trim().toDoubleOrNull() ?: throw IllegalArgumentException(
                        context.getString(R.string.if_block_error_invalid_number, parts[0])
                    )
                    val right = parts[1].trim().toDoubleOrNull() ?: throw IllegalArgumentException(
                        context.getString(R.string.if_block_error_invalid_number, parts[1])
                    )

                    return when (op) {
                        ">" -> left > right
                        "<" -> left < right
                        ">=" -> left >= right
                        "<=" -> left <= right
                        "==" -> left == right
                        "!=" -> left != right
                        else -> throw IllegalArgumentException(
                            context.getString(R.string.if_block_error_unknown_operator, op)
                        )
                    }
                }
            }
        }

        throw IllegalArgumentException(
            context.getString(R.string.if_block_error_invalid_expression, expression)
        )
    }

    private val textFieldBgColor @Composable get() = colorResource(id = R.color.text_field_bg_color)

    @Composable
    override fun Render(context: Context) {
        OperatorVisualBlock(
            title = stringResource(R.string.if_block_title),
            blockId = id
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.if_block_condition_label),
                    color = Color.White
                )
                TextField(
                    value = condition,
                    onValueChange = { condition = it },
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
                    text = stringResource(R.string.if_block_step_label),
                    color = Color.White
                )
                TextField(
                    value = step,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("\\d*"))) {
                            step = newValue
                        }
                    },
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
                Text(text = stringResource(R.string.if_block_result_label), color = Color.White)
                Text(text = lastExecutionResult, color = Color.White, modifier = Modifier.padding(start = 16.dp))
            }
        }.Render()
    }
}
