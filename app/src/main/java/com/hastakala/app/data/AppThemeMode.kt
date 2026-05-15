package com.hastakala.app.data

/**
 * How the app chooses light vs dark [androidx.compose.material3.MaterialTheme] colors.
 *
 * - [SYSTEM]: Follow the device night mode (updates when the user changes system theme).
 * - [LIGHT] / [DARK]: User override; persisted until changed again.
 */
enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}
