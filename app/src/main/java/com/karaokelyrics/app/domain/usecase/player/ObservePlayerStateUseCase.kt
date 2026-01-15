package com.karaokelyrics.app.domain.usecase.player

import com.karaokelyrics.app.domain.model.PlayerState
import com.karaokelyrics.app.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Use case for observing player state
 * Single Responsibility: Combine player state streams into unified state
 */
class ObservePlayerStateUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(): Flow<PlayerState> {
        return combine(
            playerRepository.observePlaybackPosition(),
            playerRepository.observeIsPlaying(),
            playerRepository.observeDuration()
        ) { position, isPlaying, duration ->
            PlayerState(
                playbackPosition = position,
                isPlaying = isPlaying,
                duration = duration
            )
        }
    }
}