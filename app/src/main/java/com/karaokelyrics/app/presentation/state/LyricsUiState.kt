package com.karaokelyrics.app.presentation.state

import com.karaokelyrics.app.domain.model.LyricsSyncState
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.model.UserSettings

data class LyricsUiState(
    val lyrics: SyncedLyrics? = null,
    val syncState: LyricsSyncState = LyricsSyncState(),
    val playbackPosition: Long = 0,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userSettings: UserSettings = UserSettings()
)