package com.example.mobilevisualprogramming.functions

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilevisualprogramming.blocks.Block
import com.example.mobilevisualprogramming.blocks.OperationBlock
import com.example.mobilevisualprogramming.main.VariableData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerMenuContent(
    nextBlockId: Int,
    placedBlocks: SnapshotStateList<Block>,
    operatorsList: List<Pair<String, (List<VariableData>) -> OperationBlock>>,
    variableList: List<VariableData>,
    showAddDialog: MutableState<Boolean>,
    onVarDropped: (VariableData) -> Unit,
    onVarRemoved: (VariableData) -> Unit,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    var widthOfVar by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .onSizeChanged { size -> widthOfVar = size.width },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Переменные:",
                color = Color.White,
                fontSize = 30.sp
            )
            Button(
                onClick = { showAddDialog.value = true },
                modifier = Modifier.size(35.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("+", color = Color.White, fontSize = 25.sp)
            }
        }

        Spacer(Modifier.height(8.dp))

        variableList.forEach { variable ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = variable.name,
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                        .pointerInput(variable) {
                            detectTapGestures(
                                onTap = {
                                    onVarDropped(variable)
                                }
                            )
                        }
                        .padding(vertical = 8.dp)
                )

                IconButton(
                    onClick = { onVarRemoved(variable) },
                    modifier = Modifier
                        .size(40.dp)
                        .padding(start = 8.dp)
                ) {
                    Text("×", color = Color.Red, fontSize = 30.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OperatorsMenuContent(
            operatorsList = operatorsList,
            onOperatorSelected = { creator ->
                val operator = creator(variableList)
                operator.id = nextBlockId + 1
                scope.launch { drawerState.close() }

                placedBlocks.add(operator)
                updateBlockIdsByPosition(placedBlocks = placedBlocks)
            }
        )
    }
}