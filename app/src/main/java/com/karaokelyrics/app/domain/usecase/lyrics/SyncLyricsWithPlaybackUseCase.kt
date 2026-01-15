package com.karaokelyrics.app.domain.usecase.lyrics

import com.karaokelyrics.app.domain.model.LyricsSyncState
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.repository.LyricsRepository
import com.karaokelyrics.app.domain.repository.PlayerRepository
import com.karaokelyrics.app.domain.usecase.SyncLyricsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Complex use case that orchestrates lyrics synchronization with playback
 * Single Responsibility: Coordinate lyrics sync with current playback
 */
class SyncLyricsWithPlaybackUseCase @Inject constructor(
    private val lyricsRepository: LyricsRepository,
    private val playerRepository: PlayerRepository,
    private val syncLyricsUseCase: SyncLyricsUseCase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<LyricsSyncState?> {
        return lyricsRepository.getCurrentLyrics()
            .flatMapLatest { lyrics ->
                if (lyrics != null) {
                    playerRepository.getPlaybackPosition()
                        .combine(flowOf(lyrics)) { position, lyricsData ->
                            syncLyricsUseCase(lyricsData, position)
                        }
                } else {
                    flowOf(null)
                }
            }
    }
}