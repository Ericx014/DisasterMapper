package com.example.disastermapperfrontend
import retrofit2.http.GET

interface ApiService {
    @GET("/")
    suspend fun getSingleValue(): OpencvResponse
}
