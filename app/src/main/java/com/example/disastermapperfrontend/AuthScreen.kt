package com.example.disastermapperfrontend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AuthScreen(
    viewModel: ChatViewModel = viewModel(),
    handleLogin: () -> Unit
) {
    var showLogin by rememberSaveable  { mutableStateOf(true) }
    var email by rememberSaveable  { mutableStateOf("") }
    var password by rememberSaveable  { mutableStateOf("") }
    var username by rememberSaveable  { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (showLogin) "Login" else "Sign Up",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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

        if (!showLogin) {
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (showLogin) {
                    viewModel.logIn(
                        email,
                        password,
                        onLoginSuccess = { handleLogin() },
                        onLoginFail = { error ->
                            errorMessage = error
                        })
                    email = ""
                    password = ""
                } else {
                    viewModel.signUp(
                        email,
                        password,
                        username,
                        onSignUpSuccess = {
                            showLogin = true
                        },
                        onSignUpFail = { error ->
                            errorMessage = error
                        })
                    email = ""
                    password = ""
                    username = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (showLogin) "Login" else "Sign Up")
        }

        TextButton(
            onClick = { showLogin = !showLogin },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (showLogin) "Need an account? Sign Up" else "Already have an account? Login")
        }

    }
}