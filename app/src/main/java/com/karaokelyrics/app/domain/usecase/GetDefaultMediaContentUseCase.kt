package com.karaokelyrics.app.domain.usecase

import com.karaokelyrics.app.domain.model.MediaContent
import com.karaokelyrics.app.domain.repository.LyricsRepository
import javax.inject.Inject

/**
 * Use case to get the default media content to load on app start.
 */
class GetDefaultMediaContentUseCase @Inject constructor(private val lyricsRepository: LyricsRepository) {
    operator fun invoke(): MediaContent = lyricsRepository.getDefaultContent()
}
