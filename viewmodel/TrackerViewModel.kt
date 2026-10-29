package com.pe.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pe.tracker.data.LocationResult
import com.pe.tracker.data.TrackerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TrackerUiState(
    val isLoading: Boolean = false,
    val isPolling: Boolean = false,
    val error: String? = null,
    val location: LocationResult? = null,
    val phoneNumber: String = "",
    val pollCount: Int = 0,
    val lastUpdated: Long? = null,
    val history: List<LocationResult> = emptyList()
)

class TrackerViewModel(
    private val repository: TrackerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackerUiState())
    val uiState: StateFlow<TrackerUiState> = _uiState.asStateFlow()

    private var pollJob: Job? = null

    // ─── Acciones ────────────────────────────────────────────────────────

    fun startTracking(phoneNumber: String) {
        _uiState.value = TrackerUiState(
            isLoading = true,
            phoneNumber = phoneNumber
        )

        viewModelScope.launch {
            try {
                val location = repository.trackPhone(phoneNumber)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    location = location,
                    lastUpdated = System.currentTimeMillis(),
                    history = listOf(location)
                )
                // Iniciar polling automático
                startPolling(phoneNumber)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    fun startPolling(phoneNumber: String) {
        stopPolling()
        _uiState.value = _uiState.value.copy(isPolling = true)

        pollJob = viewModelScope.launch {
            var count = 0
            while (true) {
                delay(60_000L) // Poll cada 60 segundos
                count++
                try {
                    val newLoc = repository.pollLocation(phoneNumber)
                    val prev = _uiState.value.location

                    // Detectar movimiento (cambio > 100m)
                    val moved = if (prev != null && newLoc != null) {
                        com.pe.tracker.util.GeoUtils.distanceMeters(
                            prev.lat, prev.lng, newLoc.lat, newLoc.lng
                        ) > 100.0
                    } else false

                    _uiState.value = _uiState.value.copy(
                        location = newLoc,
                        lastUpdated = System.currentTimeMillis(),
                        pollCount = count,
                        history = if (moved) {
                            (_uiState.value.history + newLoc).takeLast(100)
                        } else {
                            _uiState.value.history
                        }
                    )
                } catch (_: Exception) {
                    // Silencioso — no interrumpir UI por un timeout
                }
            }
        }
    }

    fun stopPolling() {
        pollJob?.cancel()
        pollJob = null
        _uiState.value = _uiState.value.copy(isPolling = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }
}
