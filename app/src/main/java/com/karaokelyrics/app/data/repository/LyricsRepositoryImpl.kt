package com.karaokelyrics.app.data.repository

import android.content.Context
import com.karaokelyrics.app.domain.repository.LyricsRepository
import com.karaokelyrics.app.domain.model.SyncedLyrics
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LyricsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LyricsRepository {

    private val _currentLyrics = MutableStateFlow<SyncedLyrics?>(null)

    /**
     * Load raw file content from assets.
     * This is pure data access without any parsing or processing.
     */
    override suspend fun loadFileContent(fileName: String): Result<List<String>> =
        withContext(Dispatchers.IO) {
            runCatching {
                context.assets.open(fileName).bufferedReader().use {
                    it.readLines()
                }
            }
        }

    /**
     * Store processed lyrics in repository.
     */
    override suspend fun setCurrentLyrics(lyrics: SyncedLyrics) {
        _currentLyrics.value = lyrics
    }

    /**
     * Get current lyrics as a flow for observing changes.
     */
    override fun getCurrentLyrics(): Flow<SyncedLyrics?> = _currentLyrics.asStateFlow()
}