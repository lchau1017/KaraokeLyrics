package com.karaokelyrics.app.domain.usecase.lyrics

import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.repository.LyricsRepository
import javax.inject.Inject

/**
 * Use case for loading lyrics from assets
 * Single Responsibility: Load lyrics from asset files
 */
class LoadLyricsFromAssetUseCase @Inject constructor(
    private val lyricsRepository: LyricsRepository
) {
    suspend operator fun invoke(fileName: String): Result<SyncedLyrics> {
        return lyricsRepository.loadLyricsFromAsset(fileName)
    }
}