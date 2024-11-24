package com.example.disastermapperfrontend

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
            FirebaseApp.initializeApp(this)
            val context = LocalContext.current
            MainScreen(context)
        }
    }
}

enum class ApplicationPage {
    Home, Profile
}

@Composable
fun MainScreen(context: Context) {
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
                context = context,
                onChatClick = {
                    showChat = true
//                    currentPage = ApplicationPage.Chat
                },
                currentPage = currentPage,
                changePage = {newPage -> currentPage = newPage },
                handleLogout = { isLoggedIn = false },
                showChatState = remember { mutableStateOf(showChat) }
            )
        }
    } else {
        Box(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
        ) {
            AuthScreen(
                handleLogin = { isLoggedIn = true }
            )
        }
    }
}
