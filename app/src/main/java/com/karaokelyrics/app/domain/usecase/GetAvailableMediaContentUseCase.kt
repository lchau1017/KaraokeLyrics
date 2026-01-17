package com.karaokelyrics.app.domain.usecase

import com.karaokelyrics.app.domain.model.MediaContent
import com.karaokelyrics.app.domain.repository.LyricsRepository
import javax.inject.Inject

/**
 * Use case to get all available media content.
 */
class GetAvailableMediaContentUseCase @Inject constructor(private val lyricsRepository: LyricsRepository) {
    operator fun invoke(): List<MediaContent> = lyricsRepository.getAvailableContent()
}
