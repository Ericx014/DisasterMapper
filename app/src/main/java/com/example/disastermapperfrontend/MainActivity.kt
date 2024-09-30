package com.example.disastermapperfrontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
            FirebaseApp.initializeApp(this)
            MainScreen()
        }
    }
}

enum class ApplicationPage {
    Home, Profile
}

@Composable
fun MainScreen() {
    var isLoggedIn by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }
    var showChat by remember { mutableStateOf(false) }
    var currentPage by remember {mutableStateOf(ApplicationPage.Home)}

    if (isLoggedIn) {
        if (showChat) {
            ChatScreen(
                onBackToHome = { showChat = false }
            )
        } else {
            HomeScreen(
                onChatClick = { showChat = true },
                currentPage = currentPage,
                changePage = {newPage -> currentPage = newPage },
                handleLogout = { isLoggedIn = false }
            )
        }
    } else {
        AuthScreen(
            handleLogin = { isLoggedIn = true }
        )
    }
}
