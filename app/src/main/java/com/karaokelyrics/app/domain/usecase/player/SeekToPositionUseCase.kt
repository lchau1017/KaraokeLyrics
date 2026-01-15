package com.karaokelyrics.app.domain.usecase.player

import com.karaokelyrics.app.domain.repository.PlayerRepository
import javax.inject.Inject

/**
 * Use case for seeking to a specific position in the playback
 * Single Responsibility: Handle position seeking
 */
class SeekToPositionUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(position: Long) {
        require(position >= 0) { "Position cannot be negative" }
        playerRepository.seekTo(position)
    }
}