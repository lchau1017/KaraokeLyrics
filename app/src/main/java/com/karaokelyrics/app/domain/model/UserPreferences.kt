package com.karaokelyrics.app.domain.model

/**
 * Domain-level user preferences.
 * Only contains business logic settings, no UI concerns.
 */
data class UserPreferences(
    val lyricsTimingOffsetMs: Int = 200, // Business logic: sync timing
    val preferredLanguage: String = "en",
    val enabledFeatures: Set<String> = setOf("karaoke", "sync"),
    val playbackSettings: PlaybackSettings = PlaybackSettings()
)

data class PlaybackSettings(
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val shuffleEnabled: Boolean = false,
    val playbackSpeed: Float = 1.0f
)

enum class RepeatMode {
    OFF,
    ONE,
    ALL
}