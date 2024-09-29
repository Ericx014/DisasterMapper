package com.example.disastermapperfrontend

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.IOException
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
            FirebaseApp.initializeApp(this)
            MainScreen()
        }
    }
}

@Composable
fun ConnectionStatus(modifier: Modifier = Modifier) {
    var isConnected by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            try {
                withContext(Dispatchers.IO) {
                    val url = URL("http://192.168.0.102:8000")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connectTimeout = 5000 // 5 seconds
                    connection.readTimeout = 5000 // 5 seconds

                    val responseCode = connection.responseCode
                    Log.i("ConnectionStatus", "Response Code: $responseCode")

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = connection.inputStream.bufferedReader().use { it.readText() }
                        Log.i("ConnectionStatus", "Response: $response")
                        isConnected = response.trim().toBoolean()
                    } else {
                        throw IOException("HTTP error code: $responseCode")
                    }
                }
                errorMessage = null
            } catch (e: Exception) {
                isConnected = false
                Log.e("ConnectionStatus", "Error: ${e.javaClass.simpleName} - ${e.message}", e)
                errorMessage = "${e.javaClass.simpleName}: ${e.message}"
            }
            delay(1000)
        }
    }
    Surface(
        modifier = Modifier
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp) ,
        shadowElevation = 6.dp
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = if (isConnected) "Connection: Valid" else "Connection: Invalid",
            color = if (isConnected) Color.Green else Color.Red
        )
    }
}


@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    var isFullScreen by remember { mutableStateOf(false) }

    val mapViewModifier = if (isFullScreen) {
        Modifier.fillMaxSize()
    } else {
        Modifier
            .fillMaxWidth(0.5f)
            .padding(horizontal = 16.dp)
            .aspectRatio(1f)
    }

    val centerLocation = GeoPoint(3.0784554644075564, 101.55352203251948 ) // Replace with your shop's coordinates

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            modifier = mapViewModifier
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
                .clip(RoundedCornerShape(16.dp))
//            shadowElevation = 10.dp
        ) {
            AndroidView(
                factory = { context ->
                    MapView(context).apply {
                        Configuration.getInstance().userAgentValue = context.packageName
                        setTileSource(TileSourceFactory.MAPNIK)
                        controller.setZoom(20.0)
                        controller.setCenter(centerLocation)

                        val marker = Marker(this)
                        marker.position = centerLocation
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = "Shop Location"
                        overlays.add(marker)
                    }
                },
                update = { mapView ->
                    mapView.onResume()
                    mapView.controller.setCenter(centerLocation)
                    mapView.controller.setZoom(if (isFullScreen) 18.0 else 20.0)
                }
            )
        }
        ConnectionStatus(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
        if (isFullScreen) {
            IconButton(
                onClick = { isFullScreen = false },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .zIndex(1f)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (!isFullScreen) {
            Button(
                onClick = { isFullScreen = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Text("Expand to Full Screen")
            }
        }
    }
}

@Composable
fun MainScreen() {
    var isLoggedIn by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }
    var showChat by remember { mutableStateOf(false) }

    if (isLoggedIn) {
        if (showChat) {
            ChatScreen(
                onBackToHome = { showChat = false }
            )
        } else {
            HomeScreen(
                onChatClick = { showChat = true },
                handleLogout = { isLoggedIn = false }
            )
        }
    } else {
        AuthScreen(
            handleLogin = { isLoggedIn = true }
        )
    }
}

@Composable
fun SideBarContent(viewModel: ChatViewModel = viewModel(), handleLogout: () -> Unit){
    val currentUsername by viewModel.currentUsername.collectAsState()

    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Disaster Mapper",
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp)
        Divider()
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = currentUsername,
                fontSize = 20.sp
            )
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeTopBar(onChatClick: () -> Unit, openDrawer: () -> Unit) {
    TopAppBar(
        title = { Text("Home") },
        navigationIcon = {
            IconButton(onClick = { openDrawer() }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        actions = {
            IconButton(onClick = onChatClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "Chat"
                )
            }
        }
    )
}

@Composable
fun HomeScreen(onChatClick: () -> Unit, handleLogout: () -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    fun openDrawer() {
        scope.launch { drawerState.open() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { SideBarContent(handleLogout = handleLogout) }
    ) {
        Scaffold(
            topBar = {
                HomeTopBar(
                    onChatClick = onChatClick,
                    openDrawer = {openDrawer()})
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
//                    .verticalScroll(rememberScrollState())
            ) {
                    MapScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel(),
    onBackToHome: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    val currentUsername by viewModel.currentUsername.collectAsState()
    var newMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat") },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Home")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "Welcome, $currentUsername!",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                reverseLayout = true
            ) {
                items(messages.reversed()) { message ->
                    MessageBubble(message, isCurrentUser = message.sender == FirebaseAuth.getInstance().currentUser?.uid)
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
        }
    }
}

@Composable
fun MessageBubble(message: Message, isCurrentUser: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 1.dp,
            color = if (isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            content = {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(8.dp),
                    color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                )
            }
        )
        Text(
            text = message.senderUsername,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        )
    }
}

@Composable
fun AuthScreen(
    handleLogin: () -> Unit,
) {
    var isLogin by rememberSaveable  { mutableStateOf(true) }
    var email by rememberSaveable  { mutableStateOf("") }
    var password by rememberSaveable  { mutableStateOf("") }
    var username by rememberSaveable  { mutableStateOf("") }
    var errorMessage by rememberSaveable  { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLogin) "Login" else "Sign Up",
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

        if (!isLogin) {
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
                if (isLogin) {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            Log.i("AuthScreen", "Login successful for email: $email, user: $username")
                            handleLogin()
                            email = ""
                            password = ""
                        }
                        .addOnFailureListener { errorMessage = it.localizedMessage }
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener { result ->
                            result.user?.let { user ->
                                Log.i("AuthScreen", "User created with UID: ${user.uid}")
                                FirebaseDatabase.getInstance("https://disastermapperchat-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
                                    .child("users")
                                    .child(user.uid)
                                    .child("username")
                                    .setValue(username)
                                    .addOnSuccessListener {
                                        Log.i("AuthScreen", "Username set for UID: ${user.uid}")
                                        email = ""
                                        password = ""
                                        username = ""
                                        isLogin = true
                                    }
                                    .addOnFailureListener { error ->
                                        Log.e("AuthScreen", "Failed to set username: ${error.localizedMessage}")
                                        errorMessage = error.localizedMessage
                                    }
                            } ?: run {
                                Log.e("AuthScreen", "User creation failed: User is null")
                                errorMessage = "User creation failed"
                            }
                        }
                        .addOnFailureListener { exception ->
                            errorMessage = if (exception is FirebaseAuthException && exception.errorCode == "ERROR_EMAIL_ALREADY_IN_USE") {
                                "Email address is already in use."
                            } else {
                                exception.localizedMessage
                            }
                            Log.e("AuthScreen", "Sign up failed: $errorMessage")
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLogin) "Login" else "Sign Up")
        }

        TextButton(
            onClick = { isLogin = !isLogin },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLogin) "Need an account? Sign Up" else "Already have an account? Login")
        }

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}