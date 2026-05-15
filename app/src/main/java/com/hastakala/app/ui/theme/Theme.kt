package com.hastakala.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─── Brand Colors (Modern Indigo & Amber Palette) ──────────────────────────────
val ArtisanOlive   = Color(0xFF3949AB) // Indigo (Primary)
val ArtisanClay    = Color(0xFFFFB300) // Amber (Secondary)
val ArtisanCream   = Color(0xFFF5F7FA) // Off-White/Cool Grey (Background)
val ArtisanInk     = Color(0xFF1A1A1A) // Deep Carbon (Text)
val ArtisanSand    = Color(0xFF7986CB) // Light Indigo (Accent)
val ArtisanLight   = Color(0xFFE8EAF6) // Indigo Mist (Surface Variant)

// ─── Dark Mode Specific Colors ────────────────────────────────────────────────
val ArtisanCreamDark = Color(0xFF121212)
val ArtisanInkDark   = Color(0xFFE0E0E0)
val ArtisanLightDark = Color(0xFF1E1E1E)

val ColorSuccess   = Color(0xFF43A047)
val ColorWarning   = Color(0xFFFB8C00)
val ColorDanger    = Color(0xFFE53935)

val White          = Color(0xFFFFFFFF)
val BackgroundGray = Color(0xFFEEEEEE)

// ─── Color Schemes ─────────────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary         = ArtisanOlive,
    onPrimary       = White,
    primaryContainer = ArtisanLight,
    onPrimaryContainer = ArtisanOlive,
    secondary       = ArtisanClay,
    onSecondary     = White,
    background      = ArtisanCream,
    onBackground    = ArtisanInk,
    surface         = White,
    onSurface       = ArtisanInk,
    surfaceVariant  = Color(0xFFECEFF1),
    error           = ColorDanger,
)

private val DarkColorScheme = darkColorScheme(
    primary         = ArtisanOlive,
    onPrimary       = White,
    primaryContainer = ArtisanLightDark,
    onPrimaryContainer = ArtisanOlive,
    secondary       = ArtisanClay,
    onSecondary     = White,
    background      = ArtisanCreamDark,
    onBackground    = ArtisanInkDark,
    surface         = ArtisanLightDark,
    onSurface       = ArtisanInkDark,
    surfaceVariant  = Color(0xFF2C2C2C),
    error           = ColorDanger,
)

// ─── Typography ───────────────────────────────────────────────────────────────
val HastaKalaTypography = Typography(
    displayLarge  = TextStyle(fontSize = 36.sp, fontWeight = FontWeight.Bold,   letterSpacing = (-0.5).sp),
    displayMedium = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold),
    headlineLarge = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.SemiBold),
    headlineMedium= TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
    titleLarge    = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
    titleMedium   = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.1.sp),
    bodyLarge     = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
    bodyMedium    = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
    bodySmall     = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
    labelLarge    = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold,    letterSpacing = 0.5.sp),
    labelMedium   = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold,    letterSpacing = 0.5.sp),
    labelSmall    = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold,    letterSpacing = 0.5.sp),
)

// ─── Theme ────────────────────────────────────────────────────────────────────
/**
 * Root Material 3 theme for Hasta Kala.
 *
 * @param darkTheme When `true`, uses [DarkColorScheme]; when `false`, [LightColorScheme].
 *   Typically driven by [com.hastakala.app.data.AppThemeMode] plus [androidx.compose.foundation.isSystemInDarkTheme]
 *   in [com.hastakala.app.MainActivity] so a settings toggle updates the whole tree instantly.
 */
@Composable
fun HastaKalaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = HastaKalaTypography,
        content     = content
    )
}
