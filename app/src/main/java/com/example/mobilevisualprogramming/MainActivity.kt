package com.example.mobilevisualprogramming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilevisualprogramming.blocks.*
import com.example.mobilevisualprogramming.blocks.messages.*
import com.example.mobilevisualprogramming.blocks.operators.*
import com.example.mobilevisualprogramming.blocks.getandset.SetVarBlock
import com.example.mobilevisualprogramming.blocks.operations.PrintValueBlock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.ui.res.painterResource
import com.example.mobilevisualprogramming.main.VariableData

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var variableList by remember { mutableStateOf(listOf<VariableData>()) }
                val placedBlocks = remember { mutableStateListOf<Block>() }
                var nextBlockId by remember { mutableIntStateOf(1) }

                val variablesDrawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val showAddDialog = remember { mutableStateOf(false) }

                val operatorsList = listOf(
                    "+" to { vars: List<VariableData> -> AdditionBlock(vars) },
                    "-" to { vars: List<VariableData> -> SubtractionBlock(vars) },
                    "*" to { vars: List<VariableData> -> MultiplicationBlock(vars) },
                    "/" to { vars: List<VariableData> -> DivisionBlock(vars) },
                    "Print" to { vars: List<VariableData> -> PrintValueBlock(vars)}
                )

                fun updateVariablesInBlocks(newVariables: List<VariableData>) {
                    placedBlocks.forEach { block ->
                        when (block) {
                            is OperationBlock -> block.updateAvailableVariables(newVariables)
                            is SetVarBlock -> block.updateAvailableVariables(newVariables)
                        }
                    }
                }

                fun removeVariable(variable: VariableData) {
                    val newList = variableList.toMutableList().apply { remove(variable) }
                    variableList = newList
                    updateVariablesInBlocks(newList)
                }

                AddVariableDialog(
                    show = showAddDialog.value,
                    onAdd = {
                        val newList = variableList + it
                        variableList = newList
                        updateVariablesInBlocks(newList)
                    },
                    onDismiss = { showAddDialog.value = false }
                )

                ModalNavigationDrawer(
                    drawerState = variablesDrawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier.width(300.dp),
                            drawerContainerColor = Color.DarkGray
                        ) {
                            DrawerMenuContent(
                                nextBlockId = nextBlockId,
                                placedBlocks = placedBlocks,
                                operatorsList = operatorsList,
                                variableList = variableList,
                                showAddDialog = showAddDialog,
                                onVarDropped = { variable ->
                                    val newVariable = VariableData(
                                        name = variable.name,
                                        value = variable.value,
                                        position = variable.position
                                    )
                                    val block = SetVarBlock(newVariable, variableList)
                                    block.id = nextBlockId+1

                                    placedBlocks.add(block)
                                    scope.launch { variablesDrawerState.close() }
                                    nextBlockId = updateBlockIdsByPosition(placedBlocks)
                                },
                                onVarRemoved = { variable ->
                                    removeVariable(variable)
                                },
                                drawerState = variablesDrawerState,
                                scope = scope
                            )
                        }
                    }
                ) {
                    MainPage(
                        placedBlocks = placedBlocks,
                        variablesDrawerState = variablesDrawerState,
                        scope = scope,
                        onBlockPositionChanged = { nextBlockId = updateBlockIdsByPosition(placedBlocks) }
                    )
                }
            }
        }
    }
}

@Composable
fun MainPage(
    placedBlocks: List<Block>,
    variablesDrawerState: DrawerState,
    scope: CoroutineScope,
    onBlockPositionChanged: () -> Unit
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
            ) {
                block.Render()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ButtonOfMenuOpening(scope, variablesDrawerState)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp, end = 16.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        FloatingActionButton(
            onClick = {
                placedBlocks
                    .sortedBy { it.id }
                    .forEach { block ->
                        when (block) {
                            is PrintValueBlock-> block.execute()
                            is SetVarBlock -> block.execute()
                            is OperatorBlock -> block.execute()
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
            text = "Operators",
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

fun updateBlockIdsByPosition(
    placedBlocks: SnapshotStateList<Block>
) : Int
{
    val sortedBlocks = placedBlocks.sortedBy { it.variable.position.x }
    sortedBlocks.forEachIndexed { index, block ->
        block.id = index + 1
    }
    return placedBlocks.size + 1
}

@Composable
fun BoxScope.ButtonOfMenuOpening(scope: CoroutineScope, drawerState: DrawerState) {
    Button(
        onClick = {
            scope.launch {
                if (drawerState.isClosed) drawerState.open() else drawerState.close()
            }
        },
        modifier = Modifier.align(Alignment.BottomEnd).padding(end = 10.dp, bottom = 40.dp),
    ) {
        Text("Menu", fontSize = 20.sp)
    }
}

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
                text = "Variables",
                color = Color.White,
                fontSize = 30.sp)
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
                operator.id = nextBlockId+1
                scope.launch { drawerState.close() }

                placedBlocks.add(operator)
                updateBlockIdsByPosition(placedBlocks = placedBlocks)
            }
        )
    }
}