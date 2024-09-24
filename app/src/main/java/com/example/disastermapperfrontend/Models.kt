package com.example.disastermapperfrontend

data class Message(
    val id: String = "",
    val sender: String = "",
    val content: String = "",
    val timestamp: Long = 0,
    val senderUsername: String = ""
)

data class User(
    val id: String = "",
    val username: String = "",
    val email: String = ""
)