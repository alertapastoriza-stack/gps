package com.pe.tracker.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object GeoUtils {

    /** Distancia Haversine en metros */
    fun distanceMeters(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    /** Genera URL de Google Maps para compartir */
    fun googleMapsUrl(lat: Double, lng: Double, label: String = "Ubicación"): String {
        return "https://www.google.com/maps/search/?api=1&query=$lat,$lng&query_place_id=&label=${Uri.encode(label)}"
    }

    /** Intent para abrir Google Maps en el dispositivo */
    fun shareIntent(context: Context, lat: Double, lng: Double): Intent {
        val url = googleMapsUrl(lat, lng)
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Ubicación rastreada")
            putExtra(Intent.EXTRA_TEXT, "📍 Ubicación: $lat, $lng\n$lat, $lng\nAbrir en Maps: $url")
        }
    }

    /** Formatea coordenadas para display */
    fun formatCoords(lat: Double, lng: Double): String {
        val latDir = if (lat >= 0) "N" else "S"
        val lngDir = if (lng >= 0) "E" else "O"
        return "${abs(lat).toString().take(7)}° $latDir, ${abs(lng).toString().take(7)}° $lngDir"
    }
}
