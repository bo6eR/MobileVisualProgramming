package com.example.mobilevisualprogramming.blocks.messages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun SetGetChoiceMessage(
    show: MutableState<Boolean>,
    onChoice: (isGet: Boolean) -> Unit
) {
    if (show.value) {
        Dialog(onDismissRequest = { show.value = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = androidx.compose.ui.Modifier.padding(20.dp)
                ) {
                    Text("Выбери действие:")
                    Spacer(androidx.compose.ui.Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = {
                            onChoice(true)
                            show.value = false
                        }) {
                            Text("Get")
                        }
                        Button(onClick = {
                            onChoice(false)
                            show.value = false
                        }) {
                            Text("Set")
                        }
                    }
                }
            }
        }
    }
}
