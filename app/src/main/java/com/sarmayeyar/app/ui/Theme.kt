package com.sarmayeyar.app.ui

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF1565C0),
    onPrimary = Color.White,

    secondary = Color(0xFF00897B),
    onSecondary = Color.White,

    tertiary = Color(0xFFFFA000),
    onTertiary = Color.Black,

    background = Color(0xFFF7F8FA),
    onBackground = Color(0xFF1B1B1B),

    surface = Color.White,
    onSurface = Color(0xFF1B1B1B),

    surfaceVariant = Color(0xFFE9EDF2),
    onSurfaceVariant = Color(0xFF4A4F55)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF003258),

    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF003731),

    tertiary = Color(0xFFFFCC80),
    onTertiary = Color(0xFF422B00),

    background = Color(0xFF101214),
    onBackground = Color(0xFFE6E8EA),

    surface = Color(0xFF181A1D),
    onSurface = Color(0xFFE6E8EA),

    surfaceVariant = Color(0xFF292D32),
    onSurfaceVariant = Color(0xFFC3C7CC)
)

private const val PREFS_NAME = "sarmayeyar_theme"
private const val DARK_MODE_KEY = "dark_mode"

fun loadDarkMode(context: Context): Boolean {
    return context
        .getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        .getBoolean(
            DARK_MODE_KEY,
            false
        )
}

fun saveDarkMode(
    context: Context,
    enabled: Boolean
) {
    context
        .getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        .edit()
        .putBoolean(
            DARK_MODE_KEY,
            enabled
        )
        .apply()
}

@Composable
fun SarmayeYarTheme(
    darkMode: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme =
            if (darkMode) {
                DarkColors
            } else {
                LightColors
            },
        content = content
    )
}
