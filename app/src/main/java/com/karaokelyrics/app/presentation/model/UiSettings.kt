package com.karaokelyrics.app.presentation.model

/**
 * UI-specific settings for presentation layer.
 * Contains colors, fonts, and visual preferences.
 */
data class UiSettings(
    // Dark theme colors (ARGB integers)
    val darkLyricsColorArgb: Int = 0xFF1DB954.toInt(), // Spotify green
    val darkBackgroundColorArgb: Int = 0xFF121212.toInt(), // Spotify black

    // Light theme colors (ARGB integers)
    val lightLyricsColorArgb: Int = 0xFF1DB954.toInt(), // Spotify green
    val lightBackgroundColorArgb: Int = 0xFFFFFFFF.toInt(), // White

    // Font
    val fontSize: FontSize = FontSize.MEDIUM,

    // Features
    val enableAnimations: Boolean = true,
    val enableBlurEffect: Boolean = true,
    val enableCharacterAnimations: Boolean = true,

    // Timing
    val lyricsTimingOffsetMs: Int = 200, // Lyrics appear 200ms before audio

    // Theme
    val isDarkMode: Boolean = true
) {
    // Computed properties for current theme colors
    val lyricsColorArgb: Int
        get() = if (isDarkMode) darkLyricsColorArgb else lightLyricsColorArgb

    val backgroundColorArgb: Int
        get() = if (isDarkMode) darkBackgroundColorArgb else lightBackgroundColorArgb
}

enum class FontSize(val sp: Int, val displayName: String) {
    SMALL(28, "Small"),
    MEDIUM(34, "Medium"),
    LARGE(40, "Large"),
    EXTRA_LARGE(46, "Extra Large")
}

// Preset color schemes (ARGB integers)
object ColorPresets {
    // Core colors
    val spotifyGreen = 0xFF1DB954.toInt()
    val spotifyBlack = 0xFF121212.toInt()
    val white = 0xFFFFFFFF.toInt()

    // Dark theme lyric colors
    val darkLyricColors = listOf(
        spotifyGreen,
        white,
        0xFF9B59B6.toInt(), // purple
        0xFF3498DB.toInt(), // blue
        0xFFE74C3C.toInt(), // red
        0xFFF39C12.toInt(), // orange
        0xFFE91E63.toInt(), // pink
        0xFF00BCD4.toInt(), // cyan
        0xFFFFEB3B.toInt()  // yellow
    )

    // Light theme lyric colors
    val lightLyricColors = listOf(
        spotifyGreen,
        0xFF1A1A1A.toInt(), // dark gray for contrast
        0xFF7B1FA2.toInt(), // darker purple
        0xFF1976D2.toInt(), // darker blue
        0xFFD32F2F.toInt(), // darker red
        0xFFFF8F00.toInt(), // darker orange
        0xFFC2185B.toInt(), // darker pink
        0xFF0097A7.toInt(), // darker cyan
        0xFFFBC02D.toInt()  // darker yellow
    )

    // Dark theme background colors
    val darkBackgroundColors = listOf(
        spotifyBlack,
        0xFF2C2C2C.toInt(), // dark gray
        0xFF000000.toInt(), // black
        0xFF1A1A1A.toInt(),
        0xFF2A2A2A.toInt(),
        0xFF0D47A1.toInt(), // dark blue
        0xFF1B5E20.toInt(), // dark green
        0xFF4A148C.toInt()  // dark purple
    )

    // Light theme background colors
    val lightBackgroundColors = listOf(
        white,
        0xFFF5F5F5.toInt(), // light gray
        0xFFE8F5E8.toInt(), // light green
        0xFFE3F2FD.toInt(), // light blue
        0xFFFFF3E0.toInt(), // light orange
        0xFFF3E5F5.toInt(), // light purple
        0xFFE0F2F1.toInt(), // light cyan
        0xFFFFFDE7.toInt()  // light yellow
    )
}