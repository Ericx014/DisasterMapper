package com.example.disastermapperfrontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun FloodAlertModal(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    severity: FloodSeverity = FloodSeverity.HIGH,
    countdownTime: Int,
    currentPage: ApplicationPage,
    changePage: (ApplicationPage) -> Unit,
) {
    val context = LocalContext.current
    var remainingTime by remember { mutableStateOf(countdownTime) }
    val coroutineScope = rememberCoroutineScope()

    // Get the vibration service
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    // Cleanup vibration when dialog is dismissed or composable is disposed
    DisposableEffect(isVisible) {
        onDispose {
            vibrator.cancel()
        }
    }

    // Trigger repeating vibration and countdown when dialog becomes visible
    LaunchedEffect(isVisible) {
        if (isVisible) {
            // Start vibration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationPattern = longArrayOf(0, 500, 100, 500)
                val vibrationAmplitudes = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE)
                val effect = VibrationEffect.createWaveform(vibrationPattern, vibrationAmplitudes, 0) // 0 means repeat
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 500, 100, 500), 0) // 0 means repeat
            }

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
                vibrator.cancel() // Cancel vibration when dialog is dismissed
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
                    // Alert Icon
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Flood Warning",
                        tint = severity.color,
                        modifier = Modifier.size(48.dp)
                    )

                    // Alert Title
                    Text(
                        text = "Flood Alert",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Severity Badge
                    Surface(
                        color = severity.color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = severity.label,
                            color = severity.color,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    // Alert Message
                    Text(
                        text = "Flooding has been detected in the area. Please follow the protocols and be in position for dispatch.",
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
                                vibrator.cancel() // Cancel vibration when dismissed
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
                                containerColor = severity.color
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            // Show countdown in Details button
                            Text("Details (${remainingTime}s)")
                        }
                    }
                }
            }
        }
    }
}


enum class FloodSeverity(
    val color: Color,
    val label: String
) {
    LOW(Color(0xFF2196F3), "Low Risk"),
    MEDIUM(Color(0xFFFFA000), "Medium Risk"),
    HIGH(Color(0xFFD32F2F), "High Risk")
}