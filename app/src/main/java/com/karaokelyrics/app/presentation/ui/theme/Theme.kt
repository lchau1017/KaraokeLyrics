package com.karaokelyrics.app.presentation.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Spotify-inspired dark theme
private val SpotifyDarkColorScheme = darkColorScheme(
    primary = SpotifyGreen,
    onPrimary = SpotifyBlack,
    primaryContainer = SpotifyGreen.copy(alpha = 0.12f),
    onPrimaryContainer = SpotifyGreen,

    secondary = SpotifyGreen,
    onSecondary = SpotifyBlack,
    secondaryContainer = SpotifyMediumGray,
    onSecondaryContainer = SpotifyWhite,

    tertiary = SpotifyGreen,
    onTertiary = SpotifyBlack,

    background = SpotifyBlack,
    onBackground = SpotifyWhite,

    surface = SpotifyMediumGray,
    onSurface = SpotifyWhite,
    surfaceVariant = SpotifyDarkGray,
    onSurfaceVariant = SpotifyTextGray,

    outline = SpotifyLightGray,
    outlineVariant = SpotifyMediumGray,

    error = Color(0xFFE22134),
    onError = SpotifyWhite,
    errorContainer = Color(0xFFE22134).copy(alpha = 0.12f),
    onErrorContainer = Color(0xFFE22134),
)

// Light theme (also Spotify-inspired, but still dark)
private val SpotifyLightColorScheme = SpotifyDarkColorScheme

@Composable
fun KaraokeLyricsTheme(
    darkTheme: Boolean = true, // Always use dark theme for Spotify look
    content: @Composable () -> Unit
) {
    // Always use Spotify dark theme
    val colorScheme = SpotifyDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SpotifyBlack.toArgb()
            // Always use dark status bar for Spotify theme
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}