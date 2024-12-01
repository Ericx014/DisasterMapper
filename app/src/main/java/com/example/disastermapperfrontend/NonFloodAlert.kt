package com.example.disastermapperfrontend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NonFloodingNoticeModal(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    countdownTime: Int = 10,
    currentPage: ApplicationPage,
    changePage: (ApplicationPage) -> Unit,
) {
    val context = LocalContext.current
    var remainingTime by remember { mutableStateOf(countdownTime) }
    val coroutineScope = rememberCoroutineScope()

    // Cleanup vibration when dialog is dismissed or composable is disposed
    DisposableEffect(isVisible) {
        onDispose { }
    }

    // Trigger countdown when dialog becomes visible
    LaunchedEffect(isVisible) {
        if (isVisible) {
            // Start countdown
            coroutineScope.launch {
                for (time in countdownTime downTo 1) {
                    remainingTime = time
                    delay(1000L) // Wait 1 second
                }
                remainingTime = 0
            }
        }
    }

    if (isVisible) {
        Dialog(
            onDismissRequest = {
                onDismiss()
            }
        ) {
            Card(
                modifier = Modifier
                    .widthIn(min = 300.dp, max = 450.dp)
                    .padding(0.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Success Icon
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "No Flooding",
                        tint = Color(0xFF34A853),
                        modifier = Modifier.size(48.dp)
                    )

                    // Notice Title
                    Text(
                        text = "No Flooding Detected",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Status Badge
                    Surface(
                        color = Color(0xFF34A853).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Safe",
                            color = Color(0xFF34A853),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    // Notice Message
                    Text(
                        text = "No flooding has been detected in the area. The current conditions are safe, and no immediate action is required.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.Justify
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Dismiss", color = Color.Black)
                        }

                        Button(
                            onClick = { changePage(ApplicationPage.History) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF34A853)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Details (${remainingTime}s)")
                        }
                    }
                }
            }
        }
    }
}