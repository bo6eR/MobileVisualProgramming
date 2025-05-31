package com.example.mobilevisualprogramming.functions

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.mobilevisualprogramming.blocks.Block

fun updateBlockIdsByPosition(
    placedBlocks: SnapshotStateList<Block>
): Int {
    val sortedBlocks = placedBlocks.sortedBy { it.variable.position.x }
    sortedBlocks.forEachIndexed { index, block ->
        block.id = index + 1
    }
    return placedBlocks.size + 1
}