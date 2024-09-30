package com.example.disastermapperfrontend

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ConnectionStatus(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel(),
) {
    val isConnected by viewModel.isConnected.collectAsState()

    Surface(
        modifier = modifier
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp) ,
        shadowElevation = 3.dp
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = if (isConnected) "Connection: Valid" else "Connection: Invalid",
            color = if (isConnected) Color.Green else Color.Red
        )
    }
}