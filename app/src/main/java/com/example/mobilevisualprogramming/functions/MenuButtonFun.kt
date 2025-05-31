package com.example.mobilevisualprogramming.functions

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BoxScope.ButtonOfMenuOpening(scope: CoroutineScope, drawerState: DrawerState) {
    Button(
        onClick = {
            scope.launch {
                if (drawerState.isClosed) drawerState.open() else drawerState.close()
            }
        },
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 10.dp, bottom = 40.dp),
    ) {
        Text("Menu", fontSize = 20.sp)
    }
}