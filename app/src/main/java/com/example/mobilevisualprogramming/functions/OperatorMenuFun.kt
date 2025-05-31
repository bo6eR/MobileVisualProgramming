package com.example.mobilevisualprogramming.functions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilevisualprogramming.blocks.OperationBlock
import com.example.mobilevisualprogramming.main.VariableData

@Composable
fun OperatorsMenuContent(
    operatorsList: List<Pair<String, (List<VariableData>) -> OperationBlock>>,
    onOperatorSelected: ((List<VariableData>) -> OperationBlock) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Операторы:",
            color = Color.White,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        operatorsList.forEach { (symbol, creator) ->
            Button(
                onClick = { onOperatorSelected(creator) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(50.dp)
            ) {
                Text(symbol, fontSize = 24.sp)
            }
        }
    }
}