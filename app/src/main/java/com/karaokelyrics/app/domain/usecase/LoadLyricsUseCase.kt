package com.karaokelyrics.app.domain.usecase

import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.repository.LyricsRepository
import javax.inject.Inject
import timber.log.Timber

/**
 * Domain use case for loading and processing lyrics files.
 * Orchestrates the loading, parsing, and processing of lyrics data.
 */
class LoadLyricsUseCase @Inject constructor(
    private val lyricsRepository: LyricsRepository,
    private val parseLyricsUseCase: ParseLyricsUseCase,
    private val processLyricsDataUseCase: ProcessLyricsDataUseCase
) {
    /**
     * Load lyrics from asset file and apply all processing steps.
     * Supports TTML, LRC, and Enhanced LRC formats.
     *
     * @param fileName Name of the lyrics file in assets
     * @return Processed SyncedLyrics ready for display
     */
    suspend operator fun invoke(fileName: String): Result<SyncedLyrics> = runCatching {
        Timber.d("LoadLyricsUseCase: Loading file: $fileName")

        // Step 1: Load raw file content from repository
        val fileContent = lyricsRepository.loadFileContent(fileName)
            .getOrThrow()
        Timber.d("LoadLyricsUseCase: Loaded ${fileContent.size} lines from file")

        // Step 2: Parse lyrics content using Kyrics library (auto-detects format)
        val parsedLyrics = parseLyricsUseCase(fileContent)
        Timber.d("LoadLyricsUseCase: Parsed ${parsedLyrics.lines.size} lyric lines")

        // Step 3: Apply business processing rules
        val processedLyrics = processLyricsDataUseCase(parsedLyrics)
        Timber.d("LoadLyricsUseCase: Processed ${processedLyrics.lines.size} lyric lines")

        // Step 4: Store processed lyrics in repository
        lyricsRepository.setCurrentLyrics(processedLyrics)

        processedLyrics
    }
}
