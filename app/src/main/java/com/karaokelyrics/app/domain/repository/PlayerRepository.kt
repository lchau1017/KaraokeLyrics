package com.karaokelyrics.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    fun observePlaybackPosition(): Flow<Long>
    fun observeIsPlaying(): Flow<Boolean>
    suspend fun play()
    suspend fun pause()
    suspend fun seekTo(position: Long)
    suspend fun loadMedia(assetPath: String)
}