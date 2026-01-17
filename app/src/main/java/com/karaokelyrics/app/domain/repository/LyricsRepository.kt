package com.karaokelyrics.app.domain.repository

import com.karaokelyrics.app.domain.model.SyncedLyrics
import kotlinx.coroutines.flow.Flow

interface LyricsRepository {
    /**
     * Load raw file content from assets.
     * This is pure data access without any parsing or processing.
     */
    suspend fun loadFileContent(fileName: String): Result<List<String>>

    /**
     * Store processed lyrics in repository.
     */
    suspend fun setCurrentLyrics(lyrics: SyncedLyrics)

    /**
     * Get current lyrics as a flow for observing changes.
     */
    fun getCurrentLyrics(): Flow<SyncedLyrics?>
}