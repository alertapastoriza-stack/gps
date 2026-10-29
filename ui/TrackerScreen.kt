package com.pe.tracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pe.tracker.ui.components.PhoneInput
import com.pe.tracker.ui.components.StatusCard
import com.pe.tracker.viewmodel.TrackerUiState
import android.content.Intent

@Composable
fun TrackerScreen(
    uiState: TrackerUiState,
    onStartTracking: (String) -> Unit,
    onStopPolling: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            TopAppBar(
                title = {
                    Text("📡 Phone Tracker", color = Color.White, fontSize = 20f)
                },
                actions = {
                    if (uiState.isPolling) {
                        TextButton(onClick = onStopPolling) {
                            Text("■ Detener", color = Color(0xFFEF4444))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))

            // Input
            PhoneInput(
                onTrack = onStartTracking,
                isLoading = uiState.isLoading
            )

            // Error
            uiState.error?.let { err ->
                Surface(
                    color = Color(0xFF7F1D1D),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚠️ $err", color = Color.White, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Resultado: Mapa + Status
            uiState.location?.let { loc ->
                Spacer(Modifier.height(16.dp))

                // MAPA (fijo, no scroll)
                Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                    MapScreen(location = loc) {
                        val intent = com.pe.tracker.util.GeoUtils.shareIntent(context, loc.lat, loc.lng)
                        context.startActivity(Intent.createChooser(intent, "Compartir"))
                    }
                }

                Spacer(Modifier.height(12.dp))

                // STATUS CARD
                StatusCard(
                    location = loc,
                    pollCount = uiState.pollCount,
                    lastUpdated = uiState.lastUpdated
                )

                Spacer(Modifier.height(16.dp))
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
