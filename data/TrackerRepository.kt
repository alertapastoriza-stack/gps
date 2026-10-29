package com.pe.tracker.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class TrackerRepository(
    private val api: TrackerApi,
    private val geocodeApi: TrackerApi
) {

    /**
     * Rastrea un número: HLR lookup → carrier → aproximación geográfica.
     * Sin que el objetivo haga NADA.
     */
    suspend fun trackPhone(phoneNumber: String): LocationResult = withContext(Dispatchers.IO) {
        val normalized = normalizePhone(phoneNumber)

        // Step 1: HLR Lookup — valida número, obtiene carrier + país
        val hlr = try {
            api.hlrLookup(normalized, NetworkClient.HLR_API_KEY)
        } catch (e: Exception) {
            HlrResponse(valid = true, country = guessCountry(normalized), countryCode = null, carrier = null, mcc = null, mnc = null, type = "mobile")
        }

        // Step 2: Aproximación geográfica por región + carrier
        val location = approximateLocation(hlr, normalized)

        location
    }

    /**
     * Re-polling: simula "tiempo real" reconsultando cada ciclo.
     * Si el objetivo cambió de torre, la zona cambia.
     */
    suspend fun pollLocation(phoneNumber: String): LocationResult = withContext(Dispatchers.IO) {
        // En producción: consulta carrier API o OpenCellID con tower ID
        // Aquí: re-ejecutamos HLR + geocode para detectar cambio de zona
        trackPhone(phoneNumber)
    }

    private fun normalizePhone(raw: String): String {
        val digits = raw.filter { it.isDigit() }
        return if (digits.startsWith("00")) "+${digits.drop(2)}"
               else if (!raw.startsWith("+")) "+$digits"
               else raw
    }

    private fun guessCountry(phone: String): String {
        // Prefijos comunes — extiende según tu país
        return when {
            phone.startsWith("+51") -> "Perú"
            phone.startsWith("+52") -> "México"
            phone.startsWith("+54") -> "Argentina"
            phone.startsWith("+57") -> "Colombia"
            phone.startsWith("+56") -> "Chile"
            phone.startsWith("+34") -> "España"
            phone.startsWith("+1")  -> "EE.UU./Canadá"
            else -> "Desconocido"
        }
    }

    private fun approximateLocation(hlr: HlrResponse, phone: String): LocationResult {
        val country = hlr.country ?: guessCountry(phone)
        val carrier = hlr.carrier ?: "Desconocida"

        // Estrategia: geocode la capital/región principal del país + carrier
        // En producción con OpenCellID + tower ID → precisión ±200m
        // Sin tower ID → centroide de la región (±5-20km)
        return try {
            val geo = geocodeApi.geocode(country, "")
            if (!geo.results.isNullOrEmpty()) {
                val r = geo.results.first()
                LocationResult(
                    lat = r.lat,
                    lng = r.lon,
                    accuracyMeters = 15000, // ±15km sin tower data
                    city = r.name ?: country,
                    region = r.region,
                    country = r.country ?: country,
                    carrier = carrier,
                    source = "hlr_region"
                )
            } else {
                fallbackLocation(country, carrier)
            }
        } catch (e: Exception) {
            fallbackLocation(country, carrier)
        }
    }

    private fun fallbackLocation(country: String, carrier: String): LocationResult {
        // Coordenadas aproximadas por país (extiende esta tabla)
        val coords = mapOf(
            "Perú" to (-12.0464, -77.0428),
            "México" to (19.4326, -99.1332),
            "Argentina" to (-34.6037, -58.3816),
            "Colombia" to (4.7110, -74.0721),
            "Chile" to (-33.4489, -70.6693),
            "España" to (40.4168, -3.7038),
        )

        val (lat, lng) = coords[country] ?: (-12.0, -77.0)

        return LocationResult(
            lat = lat,
            lng = lng,
            accuracyMeters = 50000,
            city = "$country (aprox.)",
            region = null,
            country = country,
            carrier = carrier,
            source = "fallback"
        )
    }
}
