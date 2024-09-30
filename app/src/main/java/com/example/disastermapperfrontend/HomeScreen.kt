package com.example.disastermapperfrontend

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onChatClick: () -> Unit,
    currentPage: ApplicationPage,
    changePage: (ApplicationPage) -> Unit,
    handleLogout: () -> Unit
) {
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
        Scaffold(
            topBar = {
                HomeTopBar(
                    onChatClick = onChatClick,
                    openDrawer = {openDrawer()})
            }
        ) { innerPadding ->
            HomeContent(
                currentPage = currentPage,
                innerPadding = innerPadding
            )
        }
    }
}