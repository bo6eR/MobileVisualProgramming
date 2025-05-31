package com.example.mobilevisualprogramming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mobilevisualprogramming.blocks.*
import com.example.mobilevisualprogramming.blocks.messages.*
import com.example.mobilevisualprogramming.blocks.operators.*
import com.example.mobilevisualprogramming.blocks.setters.SetVarBlock
import com.example.mobilevisualprogramming.blocks.operations.PrintValueBlock
import kotlinx.coroutines.launch
import com.example.mobilevisualprogramming.blocks.operations.IfBlock
import com.example.mobilevisualprogramming.functions.DrawerMenuContent
import com.example.mobilevisualprogramming.functions.MainPage
import com.example.mobilevisualprogramming.functions.updateBlockIdsByPosition
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
                    "печать" to { vars: List<VariableData> -> PrintValueBlock(vars)},
                    "условие" to { vars: List<VariableData> -> IfBlock(vars)}
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
                        onBlockPositionChanged = { nextBlockId = updateBlockIdsByPosition(placedBlocks) },
                        variableList.toMutableStateList()
                    )
                }
            }
        }
    }
}
