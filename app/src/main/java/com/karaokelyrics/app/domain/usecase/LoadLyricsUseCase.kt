package com.karaokelyrics.app.domain.usecase

import com.karaokelyrics.app.domain.repository.LyricsRepository
import com.karaokelyrics.app.domain.model.SyncedLyrics
import javax.inject.Inject

class LoadLyricsUseCase @Inject constructor(
    private val lyricsRepository: LyricsRepository
) {
    suspend operator fun invoke(fileName: String): Result<SyncedLyrics> {
        return lyricsRepository.loadLyricsFromAsset(fileName)
    }
}