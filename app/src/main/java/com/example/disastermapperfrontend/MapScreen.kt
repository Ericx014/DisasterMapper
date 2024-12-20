package com.example.disastermapperfrontend

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
@Composable
fun MapScreen(
    context: Context,
    viewModel: ChatViewModel = viewModel(),
    currentPage: ApplicationPage,
    changePage: (ApplicationPage) -> Unit,
    locationViewModel: LocationViewModel = viewModel()
) {
    val floodLevel by viewModel.floodLevel.collectAsState()
    val centerLocation = GeoPoint(3.0784554644075564, 101.55352203251948)
    val isFullScreen by locationViewModel.isFullScreen.collectAsState()

    val detectionViewModel: DetectionViewModel = viewModel()
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val floodStatus by detectionViewModel.floodStatus.collectAsState()

    val countDown = 7
    LaunchedEffect(floodStatus) {
//        if (floodStatus == "Flooding") {
            showDialog = true
            coroutineScope.launch {
                delay((countDown * 1000).toLong())
                showDialog = false
            }
//        }
    }

    var currentLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).addOnSuccessListener { location ->
                    location?.let {
                        currentLocation = GeoPoint(it.latitude, it.longitude)
                        locationViewModel.updateLocation(currentLocation ?: GeoPoint(0.0, 0.0))
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = { context ->
                    MapView(context).apply {
                        Configuration.getInstance().userAgentValue = context.packageName
                        setTileSource(TileSourceFactory.MAPNIK)
                        controller.setZoom(20.0)
                        controller.setCenter(centerLocation)

                        val centerMarker = Marker(this)
                        centerMarker.position = centerLocation
                        centerMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        centerMarker.title = "Flood Risk"
                        centerMarker.icon = centerMarker.icon.mutate()
                        updateMarkerColor(centerMarker, floodStatus)
                        overlays.add(centerMarker)

                        currentLocation?.let { location ->
                            val currentLocationMarker = Marker(this)
                            currentLocationMarker.position = location
                            currentLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            currentLocationMarker.title = "Current Location"
                            overlays.add(currentLocationMarker)
                        }
                    }
                },
                update = { mapView ->
                    mapView.onResume()
                    mapView.controller.setCenter(centerLocation)
                    mapView.controller.setZoom(if (isFullScreen) 16.0 else 19.0)

                    // Determine the circle's fill color based on flood status
                    val fillColor = when (floodStatus) {
                        "Flooding" -> 0x30FF0000
                        "No Flooding" -> 0x3000FF00
                        else -> 0x300000FF
                    }

                    val strokeColor = when (floodStatus) {
                        "Flooding" -> 0x80FF0000.toInt()
                        "No Flooding" -> 0x8000FF00.toInt()
                        else -> 0x800000FF.toInt()
                    }

                    val circleOverlay = mapView.overlays.firstOrNull { it is Polygon && it.points.isNotEmpty() } as? Polygon
                    val radius = if (isFullScreen) 400.0 else 160.0

                    if (circleOverlay == null) {
                        // Create new circle if not present
                        val newCircle = Polygon().apply {
                            points = createCirclePoints(centerLocation, radius)
                            this.fillColor = fillColor
                            this.strokeColor = strokeColor
                            strokeWidth = 2f
                        }
                        mapView.overlays.add(newCircle)
                    } else {
                        // Update existing circle
                        circleOverlay.points = createCirclePoints(centerLocation, radius)
                        circleOverlay.fillColor = fillColor
                        circleOverlay.strokeColor = strokeColor
                    }

                    // Update the center marker
                    val centerMarker = mapView.overlays.firstOrNull { it is Marker && it.title == "Flood Risk" } as? Marker
                    centerMarker?.let {
                        it.icon = it.icon.mutate()
                        updateMarkerColor(it, floodStatus)
                    }

                    // Handle current location marker
                    currentLocation?.let { location ->
                        val currentLocationMarker = mapView.overlays.firstOrNull {
                            it is Marker && it.title == "Current Location"
                        } as? Marker

                        if (currentLocationMarker != null) {
                            currentLocationMarker.position = location
                        } else {
                            val newMarker = Marker(mapView).apply {
                                position = location
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = "Current Location"
                            }
                            mapView.overlays.add(newMarker)
                        }
                    }

                    mapView.invalidate()
                }
            )
        }
        ConnectionStatus(
            currentLocation = currentLocation,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(bottom = 16.dp)
        )
        if (showDialog && floodStatus == "Flooding") {
            FloodAlertModal(
                isVisible = true,
                onDismiss = { showDialog = false },
                severity = FloodSeverity.HIGH,
                countdownTime = countDown,
                changePage = changePage,
                currentPage = currentPage
            )
        } else if (showDialog && floodStatus == "No Flooding") {
            NonFloodingNoticeModal(
                isVisible = true,
                onDismiss = { showDialog = false },
                countdownTime = countDown,
                changePage = changePage,
                currentPage = currentPage
            )
        }
        if (isFullScreen) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .zIndex(1f),
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                IconButton(
                    onClick = { locationViewModel.exitFullScreen() },
                    modifier = Modifier
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        if (!isFullScreen) {
            Button(
                onClick = { locationViewModel.enterFullScreen() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 110.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF598392)
                )
            ) {
                Text("Expand Map")
            }
        }
    }
}

// Helper function to create circle points
private fun createCirclePoints(center: GeoPoint, radiusInMeters: Double): ArrayList<GeoPoint> {
    val points = ArrayList<GeoPoint>()
    val earthRadius = 6371000.0 // Earth's radius in meters
    val numPoints = 60 // Number of points to create the circle

    for (i in 0 until numPoints) {
        val angle = Math.PI * 2 * i / numPoints

        // Calculate the lat/lon for each point around the circle
        val latRadius = radiusInMeters / earthRadius
        val lonRadius = radiusInMeters / (earthRadius * Math.cos(Math.PI * center.latitude / 180))

        val lat = center.latitude + (latRadius * Math.sin(angle) * 180 / Math.PI)
        val lon = center.longitude + (lonRadius * Math.cos(angle) * 180 / Math.PI)

        points.add(GeoPoint(lat, lon))
    }

    return points
}

private fun updateMarkerColor(marker: Marker, floodStatus: String) {
    val color = when (floodStatus) {
        "No Flooding" -> android.graphics.Color.GREEN
        "Flooding" -> android.graphics.Color.RED
        else -> android.graphics.Color.GRAY
    }
    marker.icon = marker.icon.mutate()
    marker.icon.setTint(color)
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}
