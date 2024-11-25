package com.example.disastermapperfrontend

import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.osmdroid.util.GeoPoint

@Composable
fun ConnectionStatus(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel(),
    currentLocation: GeoPoint? = null
) {
    val isConnected by viewModel.isConnected.collectAsState()
    val detectionViewModel : DetectionViewModel = viewModel()
    val floodStatus by detectionViewModel.floodStatus.collectAsState()

    Surface(
        modifier = modifier
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
//        shadowElevation = 8.dp,
//        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        color = Color.White.copy(alpha = 0.8f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
//                text = if (isConnected) "Connection: Valid" else "Connection: Invalid",
//                color = if (isConnected) Color.Green else Color.Red,
                text = "Connection: Valid",
                style = MaterialTheme.typography.bodySmall,
            )
            currentLocation?.let { location ->
                Text(
                    text = "Lat: ${location.latitude.round(4)}\nLong: ${location.longitude.round(4)}",
                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                )
            }
//            Row(
//            ){
//                Text(
//                    text = "Flood status: ",
//                    style = MaterialTheme.typography.bodySmall
//                )
//                Text(
//                    text = {floodStatus},
//                    style = MaterialTheme.typography.bodySmall
//                )
//            }
        }
    }
}