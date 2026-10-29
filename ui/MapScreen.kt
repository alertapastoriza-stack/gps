package com.pe.tracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.SupportZoomControls
import com.pe.tracker.data.LocationResult
import com.pe.tracker.util.GeoUtils

@Composable
fun MapScreen(location: LocationResult, onShare: () -> Unit) {
    val context = LocalContext.current
    val targetLatLng = LatLng(location.lat, location.lng)

    Column(modifier = Modifier.fillMaxSize()) {
        // Mapa (80% de la pantalla)
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            cameraPositionState = remember {
                MarkerState().let {
                    null // se setea abajo
                }
            },
            uiSettings = com.google.maps.android.compose.UiSettingsOptions(
                zoomControlsEnabled = true,
                compassEnabled = true
            )
        ) {
            Marker(
                state = MarkerState(LatLng(location.lat, location.lng)),
                title = "🎯 Objetivo",
                snippet = "${location.city} — ${location.carrier}"
            )

            // Círculo de precisión (visual)
            // Nota: en maps-compose se usa Circle
            com.google.maps.android.compose.Circle(
                center = LatLng(location.lat, location.lng),
                radius = location.accuracyMeters.toDouble(),
                fillColor = Color(0x33FF4444),
                strokeColor = Color(0xAAFF4444),
                strokeWidth = 2f
            )

            SupportZoomControls()
        }

        // Barra inferior con acciones
        Surface(
            color = Color(0xFF1E293B),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = GeoUtils.formatCoords(location.lat, location.lng),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Text(
                        text = "±${location.accuracyMeters}m — ${location.city}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                Button(
                    onClick = {
                        val intent = GeoUtils.shareIntent(context, location.lat, location.lng)
                        context.startActivity(Intent.createChooser(intent, "Compartir ubicación"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E))
                ) {
                    Text("📤 Compartir en Maps")
                }
            }
        }
    }
}

// Import faltante
import android.content.Intent
