package com.hastakala.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hastakala.app.data.Language
import com.hastakala.app.ui.theme.*

@Composable
fun SplashScreen(lang: Language) {
    val scale by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        // Decorative circles
        Box(
            modifier = Modifier.size(300.dp).clip(CircleShape)
                .background(Color.White.copy(0.05f))
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-60).dp)
        )
        Box(
            modifier = Modifier.size(200.dp).clip(CircleShape)
                .background(Color.White.copy(0.05f))
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 60.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(Color.White.copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = when (lang) {
                        Language.KN -> "ಹಸ್ತ-ಕಲಾ"
                        Language.HI -> "हस्त-कला"
                        else -> "Hasta-Kala"
                    },
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Light,
                        letterSpacing = 2.sp
                    )
                )
                Text(
                    text = when (lang) {
                        Language.KN -> "ಮಳಿಗೆ"
                        Language.HI -> "शಪ್ಪ"
                        else -> "Shop"
                    },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(0.7f),
                        letterSpacing = 4.sp
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            CircularProgressIndicator(
                color = Color.White.copy(0.5f),
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        }
    }
}
