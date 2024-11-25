package com.example.disastermapperfrontend

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

val MainColor = Color(0xFF598392)
val MainColorLight = Color(0xFF7A9DAA)
val MainColorDark = Color(0xFF3D6A7A)
val BackgroundColor = Color(0xFFF5F7F8)
val SurfaceColor = Color.White
val TextColor = Color(0xFF2D3A3F)
val ErrorColor = Color(0xFFB44141)
@Composable
fun AuthScreen(
    viewModel: ChatViewModel = viewModel(),
    handleLogin: () -> Unit
) {
    var showLogin by rememberSaveable  { mutableStateOf(true) }
    var email by rememberSaveable  { mutableStateOf("") }
    var password by rememberSaveable  { mutableStateOf("") }
    var username by rememberSaveable  { mutableStateOf("") }
    var name by rememberSaveable  { mutableStateOf("") }
    var position by rememberSaveable  { mutableStateOf("") }
    var branch by rememberSaveable  { mutableStateOf("") }
    var id by rememberSaveable  { mutableStateOf("") }

    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .widthIn(min = 300.dp, max = 1000.dp)
                .background(SurfaceColor, RoundedCornerShape(16.dp))
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (showLogin) "Login" else "Sign Up",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp),
                color = TextColor
            )
            if (!showLogin) {
                AuthTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "Username",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                AuthTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Full Name",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AuthTextField(
                        value = position,
                        onValueChange = { position = it },
                        label = "Position",
                        modifier = Modifier.weight(1f)
                    )
                    AuthTextField(
                        value = branch,
                        onValueChange = { branch = it },
                        label = "Branch",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                AuthTextField(
                    value = id,
                    onValueChange = { id = it },
                    label = "ID",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            AuthTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )
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
                            email = email,
                            password = password,
                            username = username,
                            branch = branch,
                            position = position,
                            name = name,
                            id = id,
                            onSignUpSuccess = {
                                showLogin = true
                            },
                            onSignUpFail = { error ->
                                errorMessage = error
                            })
                        email = ""
                        password = ""
                        username = ""
                        name = ""
                        position = ""
                        branch = ""
                        id = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainColor,
                    contentColor = Color.White
                )
            ) {
                Text(if (showLogin) "Login" else "Sign Up")
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { showLogin = !showLogin },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MainColor
                )
            ) {
                Text(if (showLogin) "Need an account? Sign Up" else "Already have an account? Login")
            }

            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = ErrorColor,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextColor) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SurfaceColor,
            unfocusedContainerColor = SurfaceColor,
            focusedBorderColor = MainColor,
            unfocusedBorderColor = MainColorLight,
            focusedLabelColor = MainColor,
            unfocusedLabelColor = MainColorLight,
            cursorColor = MainColor,
            focusedTextColor = TextColor,
            unfocusedTextColor = TextColor
        )
    )
}