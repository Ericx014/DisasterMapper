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

data class FloodStatusEntry(
    val status: String = "",
    val timestamp: String = "",
    val id: String = ""
)

class DetectionViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://disastermapperchat-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

    private val _floodStatus = MutableStateFlow("")
    val floodStatus: StateFlow<String> = _floodStatus

    private val _floodHistory = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val floodHistory: StateFlow<List<Map<String, Any>>> = _floodHistory

    init {
//        loadFloodStatus()
        listenForFloodStatusChanges()
        loadFloodHistory()
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
        database.child("flood_status").orderByKey().limitToLast(1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lastChildSnapshot = snapshot.children.lastOrNull()
                val floodData = lastChildSnapshot?.value as? Map<String, Any>
                val status = floodData?.get("status") as? String ?: ""
                _floodStatus.value = status
                Log.i("DetectionViewModel", "Flood status updated: $status")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DetectionViewModel", "Failed to listen for flood status updates", error.toException())
            }
        })
    }

    private fun loadFloodHistory() {
        auth.currentUser?.let { user ->
            database.child("flood_status").get()
                .addOnSuccessListener { snapshot ->
                    val floodList = mutableListOf<Map<String, Any>>()

                    snapshot.children.forEach { childSnapshot ->
                        val floodEntry = childSnapshot.value as? Map<String, Any>
                        if (floodEntry != null) {
                            floodList.add(floodEntry)
                        }
                    }

                    _floodHistory.value = floodList
                    Log.i("DetectionViewModel", "Loaded flood data: $floodList")
                }
                .addOnFailureListener { exception ->
                    Log.e("DetectionViewModel", "Failed to load flood data", exception)
                }
        }
    }
}