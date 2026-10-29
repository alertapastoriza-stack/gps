package com.pe.tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pe.tracker.data.NetworkClient
import com.pe.tracker.data.TrackerRepository
import com.pe.tracker.ui.TrackerScreen
import com.pe.tracker.viewmodel.TrackerUiState
import com.pe.tracker.viewmodel.TrackerViewModel

class MainActivity : ComponentActivity() {

    private val repository by lazy {
        TrackerRepository(
            api = NetworkClient.buildApi(),
            geocodeApi = NetworkClient.buildGeocodeApi()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = androidx.compose.ui.graphics.Color(0xFF3B82F6),
                    background = androidx.compose.ui.graphics.Color(0xFF0F172A),
                    surface = androidx.compose.ui.graphics.Color(0xFF1E293B)
                )
            ) {
                val viewModel: TrackerViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            return TrackerViewModel(repository) as T
                        }
                    }
                )

                val uiState by viewModel.uiState.collectAsState()

                TrackerScreen(
                    uiState = uiState,
                    onStartTracking = { viewModel.startTracking(it) },
                    onStopPolling = { viewModel.stopPolling() }
                )
            }
        }
    }
}

// Import
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
