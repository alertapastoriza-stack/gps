package com.pe.tracker.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// ─── API Interface ──────────────────────────────────────────────────────
interface TrackerApi {

    @GET("v1/hlr")
    suspend fun hlrLookup(
        @Query("number") number: String,
        @Query("api_key") apiKey: String
    ): HlrResponse

    // OpenCellID — cell tower lookup (free tier)
    @GET("v0/enrichment/cell")
    suspend fun cellTowerLookup(
        @Query("mcc") mcc: String,
        @Query("mnc") mnc: String,
        @Query("cell_id") cellId: String,
        @Query("key") key: String
    ): CellEnrichment

    // Fallback: geocode by region/city for approximate center
    @GET("v1/geocode")
    suspend fun geocode(
        @Query("q") query: String,
        @Query("api_key") apiKey: String
    ): GeocodeResponse
}

data class CellEnrichment(
    val lat: Double?,
    val lng: Double?,
    val radius: Int?,
    val city: String?,
    val region: String?,
    val country: String?
)

data class GeocodeResponse(
    val results: List<GeocodeResult>?
)

data class GeocodeResult(
    val lat: Double,
    val lon: Double,
    val name: String?,
    val country: String?,
    val region: String?
)

// ─── Client Builder ─────────────────────────────────────────────────────
object NetworkClient {

    private const val HLR_BASE = "https://api.hlrlookup.com/"
    private const val OPENCELLID_BASE = "https://api.opencellid.org/"
    private const val NOMINATIM_BASE = "https://nominatim.openstreetmap.org/"

    // API Keys — pon las tuyas o usa free tier
    private const val HLR_API_KEY = "TU_HLR_API_KEY"
    private const val OPENCELLID_KEY = "TU_OPENCELLID_KEY"

    fun buildApi(): TrackerApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(HLR_BASE)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackerApi::class.java)
    }

    fun buildGeocodeApi(): TrackerApi {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(NOMINATIM_BASE)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackerApi::class.java)
    }
}
