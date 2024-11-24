package com.example.disastermapperfrontend

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
@OptIn(ExperimentalMaterial3Api::class)
fun HomeTopBar(onChatClick: () -> Unit, openDrawer: () -> Unit) {
    TopAppBar(
        title = { Text("Disaster Mapper") },
//        navigationIcon = {
//            IconButton(onClick = { openDrawer() }) {
//                Icon(
//                    imageVector = Icons.Filled.Menu,
//                    contentDescription = "Menu"
//                )
//            }
//        },
        actions = {
            IconButton(onClick = onChatClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "Chat"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color(0xFF01161e),  // Set title text color to white
            actionIconContentColor = Color(0xFF01161e),
            navigationIconContentColor = Color(0xFF01161e)
        )
    )
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
                ),
            containerColor = Color.White,
            contentColor = Color.Black,
        ) {
            NavigationBarItem(
                selected = false,
                onClick = onChatClick,
                label = { Text("Chat") },
                icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat") },
            )
            NavigationBarItem(
                selected = currentPage == ApplicationPage.Home,
                onClick = { changePage(ApplicationPage.Home) },
                label = { Text("Home") },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.Black,
                    indicatorColor = Color(0xFF598392),
                    unselectedIconColor = Color.Black,
                    unselectedTextColor = Color.Black
                )
            )
            NavigationBarItem(
                selected = currentPage == ApplicationPage.Profile,
                onClick = { changePage(ApplicationPage.Profile)  },
                label = { Text("Profile") },
                icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.Black,
                    indicatorColor = Color(0xFF598392),
                    unselectedIconColor = Color.Black,
                    unselectedTextColor = Color.Black
                )
            )
        }
    }
}