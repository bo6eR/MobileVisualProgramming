package com.example.mobilevisualprogramming.blocks

import androidx.compose.runtime.Composable
import com.example.mobilevisualprogramming.main.VariableData
import androidx.compose.runtime.*

abstract class Block(open val variable: VariableData)
{
    var id by mutableIntStateOf(-1)

    @Composable
    abstract fun Render()

    open fun execute() {}
    open fun execute(placedBlocks: List<Block>, currentBlockId: Int) {}

    open fun updateAvailableVariables(newVariables: List<VariableData>) {}

    protected fun isBracketsValid(s: String): Boolean {
        var balance = 0
        for (char in s) {
            when (char) {
                '(' -> balance++
                ')' ->
                {
                    balance--
                    if (balance < 0) return false
                }
            }
        }
        return balance == 0
    }

}