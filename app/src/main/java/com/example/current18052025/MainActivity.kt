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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var variableList by remember { mutableStateOf(listOf<VariableData>()) }
                var placedBlocks = remember { mutableStateListOf<VariableData>() }

                var draggingVar by remember { mutableStateOf<VariableData?>(null) }
                var dragOffset by remember { mutableStateOf(Offset.Zero) }

                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                var showAddDialog by remember { mutableStateOf(false) }

                AddVariableDialog(
                    show = showAddDialog,
                    onAdd = { variableList = variableList + it },
                    onDismiss = { showAddDialog = false }
                )

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(modifier = Modifier
                            .width(300.dp),
                            drawerContainerColor = Color.DarkGray

                        ) {

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
                                        onClick = { showAddDialog = true },
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
                                                        draggingVar = variable
                                                        dragOffset = it
                                                        scope.launch {
                                                            drawerState.close()
                                                        }
                                                    },
                                                    onDrag = { change, offset ->
                                                        change.consume()
                                                        dragOffset += offset
                                                    },
                                                    onDragEnd = {
                                                        placedBlocks.add(draggingVar!!.copy(position = dragOffset))
                                                        draggingVar = null
                                                        dragOffset = Offset.Zero
                                                    },
                                                    onDragCancel = {
                                                        draggingVar = null
                                                        dragOffset = Offset.Zero
                                                    }
                                                )
                                            }
                                    )
                                }


                            }




                        }
                    }
                ) {

                    //MAIN_PAGE a to ya zaputayus' chto gde pishu
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
                        draggingVar?.let { variable ->
                            Box(
                                modifier = Modifier
                                    .offset { IntOffset(dragOffset.x.toInt(), dragOffset.y.toInt()) }
                                    .zIndex(1f)
                                    .pointerInput(Unit) {}
                            ) {
                                VarBlock(variable = variable)
                            }
                        }

                    }


                }





            }
        }
    }
}


