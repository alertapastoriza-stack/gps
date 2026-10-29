package com.pe.tracker.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LiveIndicator(isActive: Boolean) {
    if (isActive) {
        val transition = rememberInfiniteTransition(label = "pulse")
        val scale by transition.animateFloat(
            initialValue = 1f,
            targetValue = 1.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )
        val alpha by transition.animateFloat(
            initialValue = 1f,
            targetValue = 0.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )

        Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
            // Outer pulse
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .scale(scale)
                    .background(Color(0xFF22C55E).copy(alpha = alpha * 0.4f), CircleShape)
            )
            // Inner dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFF22C55E), CircleShape)
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(Color(0xFF3B82F6), CircleShape)
        )
    }
}
