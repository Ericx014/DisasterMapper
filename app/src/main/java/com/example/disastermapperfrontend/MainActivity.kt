package com.example.disastermapperfrontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isLoggedIn by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }
            var showRegistration by remember { mutableStateOf(false) }

            if (isLoggedIn) {
                ChatApp(onLogout = { isLoggedIn = false })
            } else {
                if (showRegistration) {
                    RegistrationScreen(onRegistrationSuccess = { isLoggedIn = true })
                } else {
                    LoginScreen(
                        onLoginSuccess = { isLoggedIn = true },
                        onRegisterClick = { showRegistration = true }
                    )
                }
            }
        }
    }
}

@Composable
fun ChatApp(viewModel: ChatViewModel = viewModel(), onLogout: () -> Unit) {
    val messages by viewModel.messages.collectAsState()
    var newMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                MessageBubble(message)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = newMessage,
                onValueChange = { newMessage = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (newMessage.isNotBlank()) {
                        viewModel.sendMessage(newMessage)
                        newMessage = ""
                    }
                }
            ) {
                Text("Send")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                logout(onLogout)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Log Out")
        }
    }
}

fun logout(onLogout: () -> Unit) {
    FirebaseAuth.getInstance().signOut()
    onLogout()
}

@Composable
fun MessageBubble(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (message.sender == FirebaseAuth.getInstance().currentUser?.uid) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 1.dp,
            color = if (message.sender == FirebaseAuth.getInstance().currentUser?.uid) MaterialTheme.colorScheme .primary else MaterialTheme.colorScheme .secondary
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme .onPrimary
            )
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onRegisterClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { onLoginSuccess() }
                    .addOnFailureListener { errorMessage = it.localizedMessage }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun RegistrationScreen(onRegistrationSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { onRegistrationSuccess() }
                    .addOnFailureListener { errorMessage = it.localizedMessage }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

