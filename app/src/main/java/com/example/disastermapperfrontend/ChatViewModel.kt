package com.example.disastermapperfrontend

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ChatViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://disastermapperchat-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _currentUsername = MutableStateFlow("")
    val currentUsername: StateFlow<String> = _currentUsername

    private val _currentUserEmail = MutableStateFlow("")
    val currentUserEmail: StateFlow<String> = _currentUserEmail

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _floodLevel = MutableStateFlow(0)
    val floodLevel: StateFlow<Int> = _floodLevel

    init {
        loadMessages()
        loadCurrentUsername()
        loadCurrentEmail()
        startConnectionCheck()
    }

    private fun loadMessages() {
        database.child("messages")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(Message::class.java)
                    message?.let {
                        _messages.value += it
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatViewModel", "Database error: ${error.message}", error.toException())
                }
        })
        Log.i("ChatViewModel", "Messages loaded")
    }

    private fun loadCurrentUsername() {
        auth.currentUser?.let { user ->
            database.child("users").child(user.uid).child("username").get()
                .addOnSuccessListener { snapshot ->
                    _currentUsername.value = snapshot.value as? String ?: ""
                    Log.i("ChatViewModel", "Username loaded: ${_currentUsername.value}")
                }
        }
    }

    private fun loadCurrentEmail() {
        auth.currentUser?.let { user ->
            _currentUserEmail.value = user.email ?: ""
            Log.i("ChatViewModel", "User email loaded: ${_currentUserEmail.value}")
        }
    }

    private fun startConnectionCheck() {
        viewModelScope.launch {
            while (true) {
                checkConnection()
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    private suspend fun checkConnection() {
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

                    val responseData = JSONObject(response)
                    _isConnected.value = responseData.getBoolean("video_receiving")
                    _floodLevel.value = responseData.getInt("flood_level")
                } else {
                    throw Exception("HTTP error code: $responseCode")
                }
            }
        } catch (e: Exception) {
            _isConnected.value = false
            _floodLevel.value = 0
            Log.e("ConnectionStatus", "Error: ${e.javaClass.simpleName} - ${e.message}", e)
        }
    }
    fun sendMessage(content: String) {
        val user = auth.currentUser
        if (user == null) {
            Log.e("ChatViewModel", "User is not authenticated, cannot send message")
            return
        }

        user.let {
            val messageId = database.child("messages").push().key ?: return
            val message = Message(
                id = messageId,
                sender = user.uid,
                content = content,
                timestamp = System.currentTimeMillis(),
                senderUsername = currentUsername.value
            )
            database.child("messages").child(messageId).setValue(message)
                .addOnSuccessListener {
                    Log.i("ChatViewModel", "${currentUsername.value} sent a message")
                }
                .addOnFailureListener { e ->
                    Log.e("ChatViewModel", "Failed to send message", e)
                }
        }
    }

    fun logIn(
        email: String,
        password: String,
        onLoginSuccess: () -> Unit,
        onLoginFail: (String) -> Unit
    ){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.i("Login", "Login successful for email: $email")
                onLoginSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("Login", "Login failed", exception)
                onLoginFail("Login failed")
            }
    }

    fun signUp(
        email: String,
        password: String,
        username: String,
        onSignUpSuccess: () -> Unit,
        onSignUpFail: (String) -> Unit
    ){
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
                            Log.i("SignUp", "Username set for UID: ${user.uid}")
                            onSignUpSuccess()
                        }
                        .addOnFailureListener { error ->
                            Log.e("SignUp", "Failed to set username: ${error.localizedMessage}")
                            onSignUpFail(error.localizedMessage ?: "Sign up error")
                        }
                } ?: run {
                    Log.e("SignUp", "User creation failed: User cannot be null")
                    onSignUpFail("User cannot be null")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("SignUp", "Sign up failed", exception)
                onSignUpFail(exception.localizedMessage ?: "Unknown error occurred")
            }
    }

}