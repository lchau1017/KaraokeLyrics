package com.karaokelyrics.app.domain.model

import androidx.compose.ui.graphics.Color

data class UserSettings(
    // Dark theme colors
    val darkLyricsColor: Color = Color(0xFF1DB954), // Spotify green
    val darkBackgroundColor: Color = Color(0xFF121212), // Spotify black

    // Light theme colors
    val lightLyricsColor: Color = Color(0xFF1DB954), // Spotify green
    val lightBackgroundColor: Color = Color(0xFFFFFFFF), // White

    // Font
    val fontSize: FontSize = FontSize.MEDIUM,

    // Features
    val enableAnimations: Boolean = true,
    val enableBlurEffect: Boolean = true,
    val enableCharacterAnimations: Boolean = true,

    // Theme
    val isDarkMode: Boolean = true
) {
    companion object {
        val DEFAULT_DARK_LYRICS_COLOR = Color(0xFF1DB954) // Spotify green
        val DEFAULT_DARK_BACKGROUND_COLOR = Color(0xFF121212) // Spotify black
        val DEFAULT_LIGHT_LYRICS_COLOR = Color(0xFF1DB954) // Spotify green
        val DEFAULT_LIGHT_BACKGROUND_COLOR = Color(0xFFFFFFFF) // White
    }

    // Computed properties for current theme colors
    val lyricsColor: Color
        get() = if (isDarkMode) darkLyricsColor else lightLyricsColor

    val backgroundColor: Color
        get() = if (isDarkMode) darkBackgroundColor else lightBackgroundColor
}

enum class FontSize(val sp: Int, val displayName: String) {
    SMALL(28, "Small"),
    MEDIUM(34, "Medium"),
    LARGE(40, "Large"),
    EXTRA_LARGE(46, "Extra Large")
}

// Preset color schemes
object ColorPresets {
    // Core colors
    val spotifyGreen = Color(0xFF1DB954)
    val spotifyBlack = Color(0xFF121212)
    val white = Color(0xFFFFFFFF)

    // Dark theme lyric colors
    val darkLyricColors = listOf(
        spotifyGreen,
        white,
        Color(0xFF9B59B6), // purple
        Color(0xFF3498DB), // blue
        Color(0xFFE74C3C), // red
        Color(0xFFF39C12), // orange
        Color(0xFFE91E63), // pink
        Color(0xFF00BCD4), // cyan
        Color(0xFFFFEB3B)  // yellow
    )

    // Light theme lyric colors
    val lightLyricColors = listOf(
        spotifyGreen,
        Color(0xFF1A1A1A), // dark gray for contrast
        Color(0xFF7B1FA2), // darker purple
        Color(0xFF1976D2), // darker blue
        Color(0xFFD32F2F), // darker red
        Color(0xFFFF8F00), // darker orange
        Color(0xFFC2185B), // darker pink
        Color(0xFF0097A7), // darker cyan
        Color(0xFFFBC02D)  // darker yellow
    )

    // Dark theme background colors
    val darkBackgroundColors = listOf(
        spotifyBlack,
        Color(0xFF2C2C2C), // dark gray
        Color.Black,
        Color(0xFF1A1A1A),
        Color(0xFF2A2A2A),
        Color(0xFF0D47A1), // dark blue
        Color(0xFF1B5E20), // dark green
        Color(0xFF4A148C)  // dark purple
    )

    // Light theme background colors
    val lightBackgroundColors = listOf(
        white,
        Color(0xFFF5F5F5), // light gray
        Color(0xFFE8F5E8), // light green
        Color(0xFFE3F2FD), // light blue
        Color(0xFFFFF3E0), // light orange
        Color(0xFFF3E5F5), // light purple
        Color(0xFFE0F2F1), // light cyan
        Color(0xFFFFFDE7)  // light yellow
    )
}