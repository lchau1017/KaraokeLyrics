package com.karaokelyrics.app.presentation.player

import kotlinx.coroutines.flow.Flow

/**
 * Player controller interface for presentation layer.
 * Handles media playback which is a UI/presentation concern.
 */
interface PlayerController {
    fun observePlaybackPosition(): Flow<Long>
    fun observeIsPlaying(): Flow<Boolean>
    suspend fun play()
    suspend fun pause()
    suspend fun seekTo(position: Long)
    suspend fun loadMedia(assetPath: String)
}
