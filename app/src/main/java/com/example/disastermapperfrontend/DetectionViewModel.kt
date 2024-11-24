package com.example.disastermapperfrontend

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DetectionViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://disastermapperchat-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

    private val _floodStatus = MutableStateFlow("")
    val floodStatus: StateFlow<String> = _floodStatus

    init {
        loadFloodStatus()
        listenForFloodStatusChanges()
    }

    private fun loadFloodStatus(){
        auth.currentUser?.let { user ->
            database.child("flood_status").get()
                .addOnSuccessListener { snapshot ->
                    val flood_status = snapshot.value as? String
                    _floodStatus.value = flood_status ?: ""

                    Log.i("DetectionViewModel", "Loaded flood status: ${snapshot.value}")
                }
                .addOnFailureListener { exception ->
                    Log.e("DetectionViewModel", "Failed to load flood status", exception)
                }
        }
    }
    private fun listenForFloodStatusChanges() {
        database.child("flood_status").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val flood_status = snapshot.value as? String
                _floodStatus.value = flood_status ?: ""
                Log.i("DetectionViewModel", "Flood status updated: $flood_status")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DetectionViewModel", "Failed to listen for flood status updates", error.toException())
            }
        })
    }
}