package com.example.disastermapperfrontend

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
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
    val sidebarColor = Color(0xFFF0F0F0)

    ModalDrawerSheet(
        drawerContainerColor = sidebarColor
    ) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Disaster Mapper",
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp)
        Divider()
        Column(
            modifier = Modifier.padding().fillMaxSize().padding(top = 20.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentPage == ApplicationPage.Home) Color.LightGray else sidebarColor
                ),
                shape = RectangleShape,
                onClick = {
                    changePage(ApplicationPage.Home)
                    closeSideBar()
                }
            ){
                Text(text = "Home", color = Color.Black)
            }
            Button(
                modifier = Modifier.fillMaxWidth(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentPage == ApplicationPage.Profile) Color.LightGray else sidebarColor
                ),
                shape = RectangleShape,
                onClick = {
                    changePage(ApplicationPage.Profile)
                    closeSideBar()
                }
            ){
                Text(text = "Profile", color = Color.Black)
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = handleLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Logout")
            }
        }
    }
}