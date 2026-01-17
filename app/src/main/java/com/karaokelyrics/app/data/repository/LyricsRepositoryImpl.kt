package com.karaokelyrics.app.data.repository

import android.content.res.AssetFileDescriptor
import com.karaokelyrics.app.data.source.local.AssetDataSource
import com.karaokelyrics.app.data.source.local.MediaContentProvider
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.repository.LyricsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class LyricsRepositoryImpl @Inject constructor(
    private val assetDataSource: AssetDataSource,
    private val mediaContentProvider: MediaContentProvider
) : LyricsRepository {

    private val _currentLyrics = MutableStateFlow<SyncedLyrics?>(null)

    /**
     * Load raw file content from assets.
     * This is pure data access without any parsing or processing.
     */
    override suspend fun loadFileContent(fileName: String): Result<List<String>> = assetDataSource.readTextFile(fileName)

    /**
     * Get audio file descriptor for media playback.
     */
    override suspend fun getAudioFileDescriptor(fileName: String): Result<AssetFileDescriptor> =
        assetDataSource.getAssetFileDescriptor(fileName)

    /**
     * Get available media content.
     */
    override fun getAvailableContent(): List<com.karaokelyrics.app.domain.model.MediaContent> =
        mediaContentProvider.getAvailableContent().map {
            com.karaokelyrics.app.domain.model.MediaContent(
                id = it.id,
                title = it.title,
                lyricsFileName = it.lyricsFileName,
                audioFileName = it.audioFileName,
                artist = it.artist,
                album = it.album
            )
        }

    /**
     * Get default content to load.
     */
    override fun getDefaultContent(): com.karaokelyrics.app.domain.model.MediaContent {
        val content = mediaContentProvider.getDefaultContent()
        return com.karaokelyrics.app.domain.model.MediaContent(
            id = content.id,
            title = content.title,
            lyricsFileName = content.lyricsFileName,
            audioFileName = content.audioFileName,
            artist = content.artist,
            album = content.album
        )
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
