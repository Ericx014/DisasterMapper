package com.example.disastermapperfrontend

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    context: Context,
    onChatClick: () -> Unit,
    currentPage: ApplicationPage,
    changePage: (ApplicationPage) -> Unit,
    handleLogout: () -> Unit,
    showChatState: MutableState<Boolean>,
) {
    val locationViewModel: LocationViewModel = viewModel()
    val isFullScreen by locationViewModel.isFullScreen.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun openDrawer() {
        scope.launch { drawerState.open() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideBarContent(
                currentPage = currentPage,
                changePage = changePage,
                handleLogout = handleLogout,
                closeSideBar = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Scaffold(
                topBar = {
                    HomeTopBar(
                        onChatClick = onChatClick,
                        openDrawer = { openDrawer() }
                    )
                },
                content = { innerPadding ->
                    HomeContent(
                        context = context,
                        currentPage = currentPage,
                        innerPadding = innerPadding,
                        showChatState = showChatState
                    )
                }
            )
            if (!isFullScreen){
                BottomBar(
                    currentPage = currentPage,
                    changePage = changePage,
                    onChatClick = onChatClick,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
fun BottomBar(
    onChatClick: () -> Unit,
    currentPage: ApplicationPage,
    changePage: (ApplicationPage) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        NavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            NavigationBarItem(
                selected = false,
                onClick = onChatClick,
                label = { Text("Chat") },
                icon = { Icon(Icons.Default.Chat, contentDescription = "Chat") }
            )
            NavigationBarItem(
                selected = currentPage == ApplicationPage.Home,
                onClick = { changePage(ApplicationPage.Home) },
                label = { Text("Home") },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
            )
            NavigationBarItem(
                selected = false,
                onClick = { "" },
                label = { Text("Settings") },
                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") }
            )
        }
    }
}