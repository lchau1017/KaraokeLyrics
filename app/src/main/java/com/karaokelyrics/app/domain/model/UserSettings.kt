package com.karaokelyrics.app.domain.model

data class UserSettings(
    // Dark theme colors (ARGB integers)
    val darkLyricsColorArgb: Int = ColorPalette.Dark.defaultLyrics,
    val darkBackgroundColorArgb: Int = ColorPalette.Dark.defaultBackground,

    // Light theme colors (ARGB integers)
    val lightLyricsColorArgb: Int = ColorPalette.Light.defaultLyrics,
    val lightBackgroundColorArgb: Int = ColorPalette.Light.defaultBackground,

    // Font
    val fontSize: FontSize = FontSize.MEDIUM,

    // Features
    val enableAnimations: Boolean = true,
    val enableBlurEffect: Boolean = false,
    val enableCharacterAnimations: Boolean = true,

    // Timing
    val lyricsTimingOffsetMs: Int = 200, // Lyrics appear 200ms before audio

    // Theme
    val isDarkMode: Boolean = true,

    // Lyrics Source (for testing different formats)
    val lyricsSource: LyricsSource = LyricsSource.TTML
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

/**
 * Lyrics source format for testing different parsers.
 */
enum class LyricsSource(val extension: String, val displayName: String) {
    TTML("ttml", "TTML"),
    ENHANCED_LRC("elrc", "Enhanced LRC"),
    LRC("lrc", "LRC")
}
