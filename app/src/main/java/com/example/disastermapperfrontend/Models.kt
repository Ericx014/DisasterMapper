package com.example.disastermapperfrontend

data class Message(
    val id: String = "",
    val sender: String = "",
    val content: String = "",
    val timestamp: Long = 0
)

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = ""
)