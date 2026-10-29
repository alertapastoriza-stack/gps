package com.pe.tracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pe.tracker.util.PhoneParser

@Composable
fun PhoneInput(
    onTrack: (String) -> Unit,
    isLoading: Boolean
) {
    var phone by rememberSaveable { mutableStateOf("") }
    var showHelp by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "📱 Ingresar número a rastrear",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("+51 999 888 777") },
            placeholder = { Text("Código país + número") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White.copy(alpha = 0.7f),
                focusedBorderColor = Color(0xFF3B82F6),
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
            ),
            enabled = !isLoading
        )

        if (showHelp) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E293B)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("💡 Tips:", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text("• Incluye código de país: +51 987654321", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    Text("• El objetivo NO necesita hacer nada", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    Text("• Precisión: zona/torre celular (±500m-2km)", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Button(
            onClick = { if (PhoneParser.isValid(phone)) onTrack(phone) },
            enabled = PhoneParser.isValid(phone) && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("🔍 RASTREAR AHORA", fontWeight = FontWeight.Bold, fontSize = 16f)
            }
        }

        TextButton(onClick = { showHelp = !showHelp }) {
            Text(if (showHelp) "Ocultar ayuda" else "¿Cómo funciona?", color = Color.Gray)
        }
    }
}
