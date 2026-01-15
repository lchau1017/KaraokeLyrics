package com.karaokelyrics.app.domain.repository

import com.karaokelyrics.app.domain.model.SyncedLyrics
import kotlinx.coroutines.flow.Flow

interface LyricsRepository {
    suspend fun loadLyricsFromAsset(fileName: String): Result<SyncedLyrics>
    fun getCurrentLyrics(): Flow<SyncedLyrics?>
}