package com.karaokelyrics.app.domain.repository

import android.content.res.AssetFileDescriptor
import com.karaokelyrics.app.domain.model.MediaContent
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

    /**
     * Get audio file descriptor for media playback.
     */
    suspend fun getAudioFileDescriptor(fileName: String): Result<AssetFileDescriptor>

    /**
     * Get available media content.
     */
    fun getAvailableContent(): List<MediaContent>

    /**
     * Get default content to load.
     */
    fun getDefaultContent(): MediaContent
}