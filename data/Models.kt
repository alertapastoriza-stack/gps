package com.pe.tracker.data

import com.google.gson.annotations.SerializedName
import java.time.Instant

// ─── HLR Lookup Response (hlrlookup.com) ────────────────────────────────
data class HlrResponse(
    @SerializedName("valid") val valid: Boolean,
    @SerializedName("country") val country: String?,
    @SerializedName("country_code") val countryCode: String?,
    @SerializedName("carrier") val carrier: String?,
    @SerializedName("mcc") val mcc: String?,
    @SerializedName("mnc") val mnc: String?,
    @SerializedName("type") val type: String?  // "mobile", "landline"
)

// ─── Cell Tower / Geo Approximation ─────────────────────────────────────
data class LocationResult(
    val lat: Double,
    val lng: Double,
    val accuracyMeters: Int,
    val city: String?,
    val region: String?,
    val country: String?,
    val carrier: String?,
    val source: String  // "cell_tower", "hlr_region", "ip"
)

// ─── Tracking Session (local) ───────────────────────────────────────────
data class TrackingSession(
    val id: Long = 0,
    val phoneNumber: String,
    val carrier: String?,
    val country: String?,
    val lastLat: Double?,
    val lastLng: Double?,
    val lastAccuracy: Int?,
    val lastCity: String?,
    val lastUpdated: Instant? = Instant.now(),
    val isActive: Boolean = true,
    val pollCount: Int = 0
)

// ─── Position History Entry ─────────────────────────────────────────────
data class PositionEntry(
    val id: Long = 0,
    val sessionId: Long,
    val lat: Double,
    val lng: Double,
    val accuracyMeters: Int,
    val city: String?,
    val timestamp: Instant = Instant.now()
)
