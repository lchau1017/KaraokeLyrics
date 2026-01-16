package com.karaokelyrics.app.presentation.features.lyrics.state

import com.karaokelyrics.app.domain.model.LyricsSyncState
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.domain.model.theme.ThemeColors

data class LyricsUiState(
    val lyrics: SyncedLyrics? = null,
    val syncState: LyricsSyncState = LyricsSyncState(),
    val playbackPosition: Long = 0,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userSettings: UserSettings = UserSettings(),
    val themeColors: ThemeColors = ThemeColors(
        lyricsColorArgb = 0xFF1DB954.toInt(),
        backgroundColorArgb = 0xFF121212.toInt()
    )
)