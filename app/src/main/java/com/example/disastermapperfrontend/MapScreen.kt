package com.example.disastermapperfrontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(
    viewModel: ChatViewModel = viewModel()
) {
    val floodLevel by viewModel.floodLevel.collectAsState()
    val centerLocation = GeoPoint(3.0784554644075564, 101.55352203251948 )
    var isFullScreen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
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

                        val marker = Marker(this)
                        marker.position = centerLocation
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = "Flood Risk"
                        overlays.add(marker)
                        updateMarkerColor(marker, floodLevel)
                    }
                },
                update = { mapView ->
                    mapView.onResume()
                    mapView.controller.setCenter(centerLocation)
                    mapView.controller.setZoom(if (isFullScreen) 18.0 else 20.0)

                    val marker = mapView.overlays.find {it is Marker } as? Marker
                    marker?.let {updateMarkerColor(it, floodLevel)}
                }
            )
        }
        ConnectionStatus(
            modifier = Modifier
                .align(Alignment.TopCenter)
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
                Text("Expand Map View")
            }
        }
    }
}

private fun updateMarkerColor(marker: Marker, floodLevel: Int) {
    val color = when (floodLevel) {
        0 -> android.graphics.Color.GREEN
        1 -> android.graphics.Color.YELLOW
        2 -> android.graphics.Color.rgb(255, 165, 0)
        3 -> android.graphics.Color.RED
        else -> android.graphics.Color.GRAY
    }
    marker.icon = marker.icon.mutate()
    marker.icon.setTint(color)
}
