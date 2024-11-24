package com.example.disastermapperfrontend

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.osmdroid.util.GeoPoint

class LocationViewModel : ViewModel() {

    // A flow to hold current location, initially null
    private val _currentLocation = MutableStateFlow<GeoPoint?>(null)
    val currentLocation: StateFlow<GeoPoint?> = _currentLocation

    private val _isFullScreen = MutableStateFlow<Boolean>(false);
    val isFullScreen: StateFlow<Boolean> = _isFullScreen

    // Function to update the current location
    fun updateLocation(location: GeoPoint) {
        _currentLocation.value = location
    }

    fun exitFullScreen() {
        _isFullScreen.value = false;
    }

    fun enterFullScreen() {
        _isFullScreen.value = true;
    }

}
