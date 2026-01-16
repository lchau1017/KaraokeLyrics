package com.karaokelyrics.app.domain.model.theme

/**
 * Represents the current theme colors based on theme mode.
 * This is a pure data model with no business logic.
 */
data class ThemeColors(
    val lyricsColorArgb: Int,
    val backgroundColorArgb: Int
)