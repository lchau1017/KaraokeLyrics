package com.karaokelyrics.app.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for player operations
 * Interface Segregation Principle: Focused on player operations only
 */
interface PlayerRepository {
    fun observePlaybackPosition(): Flow<Long>
    fun observeIsPlaying(): Flow<Boolean>
    fun observeDuration(): Flow<Long>
    suspend fun play()
    suspend fun pause()
    suspend fun seekTo(position: Long)
    suspend fun loadMedia(assetPath: String)
    fun getPlaybackPosition(): Flow<Long>
    fun isPlaying(): Flow<Boolean>
}