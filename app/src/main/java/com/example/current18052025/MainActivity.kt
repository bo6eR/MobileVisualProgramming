package com.example.mobilevisualprogramming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import com.example.mobilevisualprogramming.main.VariableData
import com.example.mobilevisualprogramming.blocks.VarBlock
import com.example.mobilevisualprogramming.blocks.messages.AddVariableDialog
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlin.math.roundToInt


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var variableList by remember { mutableStateOf(listOf<VariableData>()) }
                var placedBlocks = remember { mutableStateListOf<VariableData>() }

                var draggingVar = remember { mutableStateOf<VariableData?>(null) }
                var dragOffset = remember { mutableStateOf(Offset.Zero) }

                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                var showAddDialog = remember { mutableStateOf(false) }

                AddVariableDialog(
                    show = showAddDialog.value,
                    onAdd = { variableList = variableList + it },
                    onDismiss = { showAddDialog.value = false }
                )

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(modifier = Modifier
                            .width(300.dp),
                            drawerContainerColor = Color.DarkGray

                        ) {
                            DrawerMenuContent(
                                variableList = variableList,
                                showAddDialog = showAddDialog,
                                draggingVar = draggingVar,
                                dragOffset = dragOffset,
                                placedBlocks = placedBlocks,
                                drawerState = drawerState,
                                scope = scope)
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
    placedBlocks: SnapshotStateList<VariableData>,
    drawerState: DrawerState,
    scope: CoroutineScope,
    draggingVar: MutableState<VariableData?>,
    dragOffset: MutableState<Offset>
){
    Box(modifier = Modifier.fillMaxSize()){

        placedBlocks.forEachIndexed  { index, variable ->
            Box(
                modifier = Modifier
                    .offset { IntOffset(variable.position.x.roundToInt(), variable.position.y.roundToInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val oldVar = placedBlocks[index]
                            val newPosition = oldVar.position + dragAmount
                            placedBlocks[index] = oldVar.copy(position = newPosition)
                        }
                    }
            ) {
                VarBlock(variable = variable)
            }
        }

        ButtonOfMenuOpening(scope, drawerState)

        draggingVar.value?.let { variable ->
            Box(
                modifier = Modifier
                    .offset { IntOffset(dragOffset.value.x.roundToInt(), dragOffset.value.y.roundToInt()) }
                    .zIndex(1f)
                    .pointerInput(Unit) {}
            ) {
                VarBlock(variable = variable)
            }
        }

    }
}

@Composable
fun BoxScope.ButtonOfMenuOpening(scope: CoroutineScope, drawerState: DrawerState){
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
    placedBlocks: SnapshotStateList<VariableData>,
    drawerState: DrawerState,
    scope: CoroutineScope
){
    //MENU a to ya zaputayus' chto gde pishu
    var widthOfVar by remember { mutableStateOf(0) }
    Column(modifier = Modifier
        .fillMaxSize()
        .onSizeChanged { size ->
            widthOfVar = size.width // ширина в пикселях
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
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
        val density = LocalDensity.current
        val widthDp = with(density) { (widthOfVar-150).toDp() }

        variableList.forEach { variable ->
            Text(
                text = variable.name,
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                modifier = Modifier
                    .padding(20.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                    .width(widthDp)
                    .pointerInput(variable) {
                        detectDragGestures(
                            onDragStart = {
                                draggingVar.value = variable
                                dragOffset.value = it
                                scope.launch {
                                    drawerState.close()
                                }
                            },
                            onDrag = { change, offset ->
                                change.consume()
                                dragOffset.value += offset
                            },
                            onDragEnd = {
                                placedBlocks.add(draggingVar.value!!.copy(position = dragOffset.value))
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


