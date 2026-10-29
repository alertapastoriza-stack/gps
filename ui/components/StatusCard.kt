package com.pe.tracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pe.tracker.data.LocationResult
import com.pe.tracker.util.GeoUtils
import java.time.Instant
import java.time.Duration

@Composable
fun StatusCard(location: LocationResult, pollCount: Int, lastUpdated: Long?) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E293B),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                LiveIndicator(isActive = pollCount > 0)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (pollCount > 0) "RASTREO ACTIVO" else "UBICACIÓN OBTENIDA",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (pollCount > 0) Color(0xFF22C55E) else Color(0xFF3B82F6)
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Poll #${pollCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(16.dp))

            // Coordenadas
            Row {
                InfoChip("LAT", GeoUtils.formatCoords(location.lat, location.lng).split(",")[0].trim())
                Spacer(Modifier.width(8.dp))
                InfoChip("LNG", GeoUtils.formatCoords(location.lat, location.lng).split(",")[1].trim())
            }

            Spacer(Modifier.height(12.dp))

            // Detalles
            DetailRow("📡 Operadora", location.carrier ?: "N/D")
            DetailRow("🌍 País", location.country ?: "N/D")
            DetailRow("🏙️ Zona", location.city ?: "N/D")
            DetailRow("🎯 Precisión", "±${location.accuracyMeters}m")
            DetailRow("📡 Fuente", location.source)

            if (lastUpdated != null) {
                Spacer(Modifier.height(8.dp))
                val ago = Duration.between(Instant.ofEpochMilli(lastUpdated), Instant.now()).seconds
                Text(
                    text = "Última actualización: hace ${ago}s",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun InfoChip(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF334155),
        modifier = Modifier.weight(1f)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}

@Composable
fun DetailRow(icon: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$icon ", style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
    }
}
