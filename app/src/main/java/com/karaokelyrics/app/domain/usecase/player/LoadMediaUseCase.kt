package com.karaokelyrics.app.domain.usecase.player

import com.karaokelyrics.app.domain.repository.PlayerRepository
import javax.inject.Inject

/**
 * Use case for loading media
 * Single Responsibility: Load and prepare media for playback
 */
class LoadMediaUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(assetPath: String) {
        playerRepository.loadMedia(assetPath)
    }
}