package com.karaokelyrics.app.domain.usecase.lyrics

import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.repository.LyricsRepository
import javax.inject.Inject

/**
 * Use case for loading lyrics from files
 * Single Responsibility: Load lyrics from file system
 */
class LoadLyricsFromFileUseCase @Inject constructor(
    private val lyricsRepository: LyricsRepository
) {
    suspend operator fun invoke(filePath: String): Result<SyncedLyrics> {
        return lyricsRepository.loadLyricsFromFile(filePath)
    }
}