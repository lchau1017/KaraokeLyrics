package com.karaokelyrics.app.domain.usecase.player

import com.karaokelyrics.app.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing current playback position
 * Single Responsibility: Provide playback position updates
 */
class ObservePlaybackPositionUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(): Flow<Long> {
        return playerRepository.getPlaybackPosition()
    }
}