package com.example.disastermapperfrontend

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloodHistoryPage(
    viewModel: DetectionViewModel = viewModel()
) {
    val floodHistory by viewModel.floodHistory.collectAsState()
    val locationViewModel: LocationViewModel = viewModel()
    val currentLocation = locationViewModel.currentLocation.collectAsState()

    Scaffold(
        containerColor = Color(0x12598392)
    ) { paddingValues ->
        if (floodHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No flood history available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 105.dp)
            ) {
//                item {
//                    Text(
//                        text = "Flood History",
//                        style = MaterialTheme.typography.headlineLarge,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(bottom = 16.dp)
//                    )
//                }
                items(floodHistory) { entry ->
                    FloodHistoryItem(entry)
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FloodHistoryItem(
    entry: Map<String, Any>,
    currentLocation: GeoPoint? = null
) {
    val status = entry["status"] as? String ?: "Unknown"
    val timestamp = entry["timestamp"] as? String ?: ""

    val parsedDateTime = LocalDateTime.parse(timestamp)
    val formattedDate = parsedDateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = when (status) {
                "Flooding" -> MaterialTheme.colorScheme.errorContainer
                "No Flooding" -> Color(0xFFE6F4EA)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (status) {
                "Flooding" -> {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Flooding",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                "No Flooding" -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "No Flooding",
                        tint = Color(0xFF34A853),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }

            Column {
                Text(
                    text = status,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (status) {
                        "Flooding" -> MaterialTheme.colorScheme.error
                        "No Flooding" -> Color(0xFF34A853)
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Location: Management and Science University",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Text(
                    text = "Coordinates: ${String.format("3.0785, 100.5535")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}