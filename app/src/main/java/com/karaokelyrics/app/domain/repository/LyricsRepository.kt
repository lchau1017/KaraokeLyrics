package com.karaokelyrics.app.domain.repository

import com.karaokelyrics.app.domain.model.SyncedLyrics
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for lyrics operations
 * Interface Segregation Principle: Focused on lyrics operations only
 */
interface LyricsRepository {
    suspend fun loadLyricsFromAsset(fileName: String): Result<SyncedLyrics>
    suspend fun loadLyricsFromFile(filePath: String): Result<SyncedLyrics>
    fun getCurrentLyrics(): Flow<SyncedLyrics?>
    suspend fun setCurrentLyrics(lyrics: SyncedLyrics?)
    suspend fun clearCurrentLyrics()
}