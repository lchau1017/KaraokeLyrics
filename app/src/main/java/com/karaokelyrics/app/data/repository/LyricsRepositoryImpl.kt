package com.karaokelyrics.app.data.repository

import com.karaokelyrics.app.data.datasource.local.LyricsLocalDataSource
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.repository.LyricsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for lyrics operations
 * Dependency Inversion Principle: Depends on abstraction (LyricsRepository)
 */
@Singleton
class LyricsRepositoryImpl @Inject constructor(
    private val lyricsDataSource: LyricsLocalDataSource
) : LyricsRepository {

    private val _currentLyrics = MutableStateFlow<SyncedLyrics?>(null)

    override suspend fun loadLyricsFromAsset(fileName: String): Result<SyncedLyrics> {
        return lyricsDataSource.loadFromAsset(fileName).also { result ->
            result.getOrNull()?.let { lyrics ->
                _currentLyrics.value = lyrics
            }
        }
    }

    override suspend fun loadLyricsFromFile(filePath: String): Result<SyncedLyrics> {
        return lyricsDataSource.loadFromFile(filePath).also { result ->
            result.getOrNull()?.let { lyrics ->
                _currentLyrics.value = lyrics
            }
        }
    }

    override fun getCurrentLyrics(): Flow<SyncedLyrics?> {
        return _currentLyrics.asStateFlow()
    }

    override suspend fun setCurrentLyrics(lyrics: SyncedLyrics?) {
        _currentLyrics.value = lyrics
    }

    override suspend fun clearCurrentLyrics() {
        _currentLyrics.value = null
    }
}