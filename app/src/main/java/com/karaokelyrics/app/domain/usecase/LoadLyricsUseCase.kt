package com.karaokelyrics.app.domain.usecase

import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.parser.LyricsParser
import com.karaokelyrics.app.domain.repository.LyricsRepository
import javax.inject.Inject

/**
 * Domain use case for loading and processing lyrics files.
 * Orchestrates the loading, parsing, and processing of lyrics data.
 */
class LoadLyricsUseCase @Inject constructor(
    private val lyricsRepository: LyricsRepository,
    private val lyricsParser: LyricsParser,
    private val processLyricsDataUseCase: ProcessLyricsDataUseCase
) {
    /**
     * Load lyrics from asset file and apply all processing steps.
     * Supports TTML format.
     *
     * @param fileName Name of the lyrics file in assets
     * @return Processed SyncedLyrics ready for display
     */
    suspend operator fun invoke(fileName: String): Result<SyncedLyrics> = runCatching {
        // Step 1: Load raw file content from repository
        val fileContent = lyricsRepository.loadFileContent(fileName)
            .getOrThrow()

        // Step 2: Parse lyrics content
        val parsedLyrics = SyncedLyrics(lyricsParser.parse(fileContent))

        // Step 3: Apply business processing rules
        val processedLyrics = processLyricsDataUseCase(parsedLyrics)

        // Step 4: Store processed lyrics in repository
        lyricsRepository.setCurrentLyrics(processedLyrics)

        processedLyrics
    }
}
