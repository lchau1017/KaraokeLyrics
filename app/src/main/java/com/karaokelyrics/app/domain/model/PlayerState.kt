package com.karaokelyrics.app.domain.model

/**
 * Data class representing the current player state
 */
data class PlayerState(
    val playbackPosition: Long = 0L,
    val isPlaying: Boolean = false,
    val duration: Long = 0L
)