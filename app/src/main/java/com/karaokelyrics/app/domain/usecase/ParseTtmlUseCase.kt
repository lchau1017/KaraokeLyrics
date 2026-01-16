package com.karaokelyrics.app.domain.usecase

import com.karaokelyrics.app.data.factory.LyricsFactory
import com.karaokelyrics.app.data.parser.TtmlXmlParser
import com.karaokelyrics.app.data.parser.TimeFormatParser
import com.karaokelyrics.app.domain.model.SyncedLyrics
import javax.inject.Inject

/**
 * Domain use case that orchestrates TTML parsing.
 * This encapsulates the business logic for parsing TTML content,
 * delegating the actual XML parsing to the data layer.
 *
 * Follows Single Responsibility Principle by only orchestrating the parsing process.
 */
class ParseTtmlUseCase @Inject constructor(
    private val ttmlParser: TtmlXmlParser,
    private val lyricsFactory: LyricsFactory
) {

    /**
     * Parse TTML content into domain model.
     * @param lines List of lines from TTML file
     * @return Parsed SyncedLyrics domain model
     */
    suspend operator fun invoke(lines: List<String>): SyncedLyrics {
        // Step 1: Parse XML structure
        val content = lines.joinToString("\n")
        val parsedData = ttmlParser.parse(content)

        // Step 2: Create domain models
        return lyricsFactory.createSyncedLyrics(parsedData)
    }
}