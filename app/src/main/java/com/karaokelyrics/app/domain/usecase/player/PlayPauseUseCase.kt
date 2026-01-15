package com.karaokelyrics.app.domain.usecase.player

import com.karaokelyrics.app.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for handling play/pause functionality
 * Single Responsibility: Toggle playback state
 */
class PlayPauseUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(isPlaying: Boolean) {
        if (isPlaying) {
            playerRepository.pause()
        } else {
            playerRepository.play()
        }
    }

    fun observePlaybackState(): Flow<Boolean> {
        return playerRepository.isPlaying()
    }
}