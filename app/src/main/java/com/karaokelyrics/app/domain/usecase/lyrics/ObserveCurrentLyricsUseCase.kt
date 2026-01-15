package com.karaokelyrics.app.domain.usecase.lyrics

import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.repository.LyricsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing current lyrics
 * Single Responsibility: Provide current lyrics updates
 */
class ObserveCurrentLyricsUseCase @Inject constructor(
    private val lyricsRepository: LyricsRepository
) {
    operator fun invoke(): Flow<SyncedLyrics?> {
        return lyricsRepository.getCurrentLyrics()
    }
}