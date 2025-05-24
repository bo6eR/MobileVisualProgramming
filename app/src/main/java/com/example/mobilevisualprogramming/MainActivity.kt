package com.example.mobilevisualprogramming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.zIndex
import com.example.mobilevisualprogramming.main.VariableData
import com.example.mobilevisualprogramming.blocks.*
import com.example.mobilevisualprogramming.blocks.messages.*
import com.example.mobilevisualprogramming.blocks.operators.*
import com.example.mobilevisualprogramming.blocks.getandset.GetVarBlock
import com.example.mobilevisualprogramming.blocks.getandset.SetVarBlock
import com.example.mobilevisualprogramming.blocks.operations.PrintValueBlock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var variableList by remember { mutableStateOf(listOf<VariableData>()) }
                val placedBlocks = remember { mutableStateListOf<Block>() }
                var nextBlockId by remember { mutableStateOf(1) }

                val draggingVar = remember { mutableStateOf<VariableData?>(null) }
                val dragOffset = remember { mutableStateOf(Offset.Zero) }

                val variablesDrawerState = rememberDrawerState(DrawerValue.Closed)
                val operatorsDrawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val showAddDialog = remember { mutableStateOf(false) }
                val showSetGetDialog = remember { mutableStateOf(false) }
                val chosenVariableTemp = remember { mutableStateOf<VariableData?>(null) }

                // Функция для обновления ID блоков по их X-позиции
                fun updateBlockIdsByPosition()
                {
                    // Сортируем блоки по X-координате
                    val sortedBlocks = placedBlocks.sortedBy { it.variable.position.x }
                    // Назначаем новые ID по порядку
                    sortedBlocks.forEachIndexed { index, block ->
                        block.id = index + 1
                    }
                    // Обновляем nextBlockId
                    nextBlockId = placedBlocks.size + 1
                }

                val operatorsList = listOf(
                    "+" to { vars: List<VariableData> -> AdditionBlock(vars) },
                    "-" to { vars: List<VariableData> -> SubtractionBlock(vars) },
                    "*" to { vars: List<VariableData> -> MultiplicationBlock(vars) },
                    "/" to { vars: List<VariableData> -> DivisionBlock(vars) },
                )

                AddVariableDialog(show = showAddDialog.value, onAdd = {
                    variableList = variableList + it
                    placedBlocks.forEach { block ->
                        if (block is OperatorBlock) {
                            block.availableVariables = variableList
                        }
                    } }, onDismiss = { showAddDialog.value = false })

                SetGetChoiceMessage(show = showSetGetDialog,
                    onChoice = { isGet ->
                        chosenVariableTemp.value?.let { variable ->
                            val newVariable = VariableData(
                                name = variable.name,
                                value = variable.value,
                                position = dragOffset.value
                            )
                            val block = if (isGet) GetVarBlock(newVariable)
                            else SetVarBlock(newVariable, variableList)
                            block.id = nextBlockId++
                            placedBlocks.add(block)
                            updateBlockIdsByPosition()
                        }
                        showSetGetDialog.value = false
                        chosenVariableTemp.value = null
                    }
                )

                ModalNavigationDrawer(
                    drawerState = variablesDrawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier.width(300.dp),
                            drawerContainerColor = Color.DarkGray
                        ) {
                            DrawerMenuContent(
                                variableList = variableList,
                                showAddDialog = showAddDialog,
                                draggingVar = draggingVar,
                                dragOffset = dragOffset,
                                onVarDropped = { variable ->
                                    chosenVariableTemp.value = variable
                                    showSetGetDialog.value = true
                                },
                                drawerState = variablesDrawerState,
                                scope = scope
                            )
                        }
                    }
                ) {
                    ModalNavigationDrawer(
                        drawerState = operatorsDrawerState,
                        drawerContent = {
                            ModalDrawerSheet(
                                modifier = Modifier.width(250.dp),
                                drawerContainerColor = Color(0xFFE0E0E0)
                            ) {
                                OperatorsMenuContent(
                                    operatorsList = operatorsList,
                                    onOperatorSelected = { creator ->
                                        val operator = creator(variableList)
                                        operator.id = nextBlockId++
                                        placedBlocks.add(operator)
                                        updateBlockIdsByPosition()
                                    }
                                )
                            }
                        },
                        gesturesEnabled = operatorsDrawerState.isOpen
                    ) {
                        MainPage(
                            placedBlocks = placedBlocks,
                            variablesDrawerState = variablesDrawerState,
                            operatorsDrawerState = operatorsDrawerState,
                            scope = scope,
                            draggingVar = draggingVar,
                            dragOffset = dragOffset,
                            onBlockPositionChanged = { updateBlockIdsByPosition() } // Обновляем ID при изменении позиции
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainPage(
    placedBlocks: List<Block>,
    variablesDrawerState: DrawerState,
    operatorsDrawerState: DrawerState,
    scope: CoroutineScope,
    draggingVar: MutableState<VariableData?>,
    dragOffset: MutableState<Offset>,
    onBlockPositionChanged: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = Modifier
        .fillMaxSize()
        .transformable(
            state = rememberTransformableState { zoomChange, panChange, _ ->
                scale = (scale * zoomChange).coerceIn(0.5f, 3f)
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
                    .offset {
                        IntOffset(
                            block.variable.position.x.roundToInt(),
                            block.variable.position.y.roundToInt()
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                block.variable.position += dragAmount / scale
                            },
                            onDragEnd = {
                                onBlockPositionChanged()
                            }
                        )
                    }
            ) {
                when (block) {
                    is OperatorBlock -> block.Render()
                    else -> block.Render()
                }
            }
        }

        draggingVar.value?.let { variable ->
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            dragOffset.value.x.roundToInt(),
                            dragOffset.value.y.roundToInt()
                        )
                    }
                    .zIndex(1f)
            ) {
                Text(
                    text = variable.name,
                    modifier = Modifier
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ButtonOfMenuOpening(scope, variablesDrawerState)
        ButtonOfOperatorsOpening(scope, operatorsDrawerState)

        // Добавляем кнопку Start
        Button(
            onClick = {
                // Сортируем блоки по ID и выполняем их
                val sortedBlocks = placedBlocks.sortedBy { it.id }
                sortedBlocks.forEach { block ->
                    when (block) {
                        is OperatorBlock -> block.execute()
                        is SetVarBlock -> block.execute()
                        is PrintValueBlock -> block.execute()
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text("Start", fontSize = 20.sp)
        }
    }
}

@Composable
fun OperatorsMenuContent(
    operatorsList: List<Pair<String, (List<VariableData>) -> OperatorBlock>>,
    onOperatorSelected: ((List<VariableData>) -> OperatorBlock) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Операторы",
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
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

@Composable
fun BoxScope.ButtonOfOperatorsOpening(scope: CoroutineScope, operatorsDrawerState: DrawerState) {
    Button(
        onClick = {
            scope.launch {
                if (operatorsDrawerState.isClosed) operatorsDrawerState.open()
                else operatorsDrawerState.close()
            }
        },
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = 10.dp, bottom = 40.dp),
    ) {
        Text("Operators", fontSize = 20.sp)
    }
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
        Text("Open/close menu", fontSize = 20.sp)
    }
}

@Composable
fun DrawerMenuContent(
    variableList: List<VariableData>,
    showAddDialog: MutableState<Boolean>,
    draggingVar: MutableState<VariableData?>,
    dragOffset: MutableState<Offset>,
    onVarDropped: (VariableData) -> Unit,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    var widthOfVar by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size -> widthOfVar = size.width },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Variables", color = Color.White, fontSize = 30.sp)
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
            Text(
                text = variable.name,
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                modifier = Modifier
                    .padding(20.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                    .width((widthOfVar - 150).dp)
                    .pointerInput(variable) {
                        detectDragGestures(
                            onDragStart = {
                                draggingVar.value = variable
                                dragOffset.value = it
                                scope.launch { drawerState.close() }
                            },
                            onDrag = { change, offset ->
                                change.consume()
                                dragOffset.value += offset
                            },
                            onDragEnd = {
                                onVarDropped(variable)
                                draggingVar.value = null
                                dragOffset.value = Offset.Zero
                            },
                            onDragCancel = {
                                draggingVar.value = null
                                dragOffset.value = Offset.Zero
                            }
                        )
                    }
            )
        }
    }
}