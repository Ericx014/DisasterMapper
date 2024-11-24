package com.example.disastermapperfrontend

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SideBarContent(
    viewModel: ChatViewModel = viewModel(),
    currentPage: ApplicationPage,
    changePage: (ApplicationPage) -> Unit,
    handleLogout: () -> Unit,
    closeSideBar: () -> Unit
){
    val currentUsername by viewModel.currentUsername.collectAsState()
    val currentUserEmail by viewModel.currentUserEmail.collectAsState()
    val currentUserFullName by viewModel.currentUserFullName.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()
    val currentUserPosition by viewModel.currentUserPosition.collectAsState()
    val currentUserBranch by viewModel.currentUserBranch.collectAsState()
    val sidebarColor = Color(0xFFFFFFFF)

    ModalDrawerSheet(
        drawerContainerColor = sidebarColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Virtual ID",
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Divider(
                modifier = Modifier.padding(bottom = 50.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )

            // ID Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Profile Picture
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.default_profile_picture),
                            contentDescription = "Profile picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // User Information
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        InfoRow("USERNAME", currentUsername.uppercase())
                        InfoRow("NAME", currentUserFullName.uppercase())
                        InfoRow("ID", currentUserId.uppercase())
                        InfoRow("EMAIL", currentUserEmail.uppercase())
                        InfoRow("POSITION", currentUserPosition.uppercase())
                        InfoRow("BRANCH", currentUserBranch.uppercase())
                        InfoRow("STATUS", "ACTIVE")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = handleLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Logout",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    isStatus: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            modifier = Modifier
                .weight(0.4f)
                .padding(end = 8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )

        Box(
            modifier = Modifier.weight(0.6f),
            contentAlignment = Alignment.CenterStart
        ) {
            if (isStatus) {
                Box(
                    modifier = Modifier
                        .background(
                            color = if (value == "ACTIVE") Color(0xFF4CAF50) else Color(0xFFE57373),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = value,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Text(
                    text = value,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
