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
import com.example.mobilevisualprogramming.blocks.*
import com.example.mobilevisualprogramming.main.VariableData
import com.example.mobilevisualprogramming.blocks.messages.AddVariableDialog
import com.example.mobilevisualprogramming.blocks.messages.SetGetChoiceMessage
import com.example.mobilevisualprogramming.blocks.getandset.GetVarBlock
import com.example.mobilevisualprogramming.blocks.getandset.SetVarBlock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var variableList by remember { mutableStateOf(listOf<VariableData>()) }
                val placedBlocks = remember { mutableStateListOf<VarBlock>() }

                val draggingVar = remember { mutableStateOf<VariableData?>(null) }
                val dragOffset = remember { mutableStateOf(Offset.Zero) }

                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val showAddDialog = remember { mutableStateOf(false) }
                val showSetGetDialog = remember { mutableStateOf(false) }
                val chosenVariableTemp = remember { mutableStateOf<VariableData?>(null) }

                // Добавление переменной
                AddVariableDialog(
                    show = showAddDialog.value,
                    onAdd = { variableList = variableList + it },
                    onDismiss = { showAddDialog.value = false }
                )

                // Диалог выбора Get/Set
                SetGetChoiceMessage(
                    show = showSetGetDialog,
                    onChoice = { isGet ->
                        chosenVariableTemp.value?.let { variable ->
                            val newVariable = VariableData(
                                name = variable.name,
                                value = variable.value,
                                position = dragOffset.value
                            )
                            val block = if (isGet) GetVarBlock(newVariable)
                            else SetVarBlock(newVariable)
                            placedBlocks.add(block)
                        }
                        showSetGetDialog.value = false
                        chosenVariableTemp.value = null
                    }
                )

                ModalNavigationDrawer(
                    drawerState = drawerState,
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
                                drawerState = drawerState,
                                scope = scope
                            )
                        }
                    }
                ) {
                    MainPage(
                        placedBlocks = placedBlocks,
                        drawerState = drawerState,
                        scope = scope,
                        draggingVar = draggingVar,
                        dragOffset = dragOffset
                    )
                }
            }
        }
    }
}

@Composable
fun MainPage(
    placedBlocks: List<VarBlock>,
    drawerState: DrawerState,
    scope: CoroutineScope,
    draggingVar: MutableState<VariableData?>,
    dragOffset: MutableState<Offset>
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
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            block.variable.position += dragAmount / scale
                        }
                    }
            ) {
                block.Render()
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
    Box(modifier = Modifier.fillMaxSize()){
        ButtonOfMenuOpening(scope, drawerState)
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
