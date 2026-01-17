package com.karaokelyrics.app.domain.usecase

import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.parser.TtmlParser
import javax.inject.Inject

/**
 * Domain use case that orchestrates TTML parsing.
 * This encapsulates the business logic for parsing TTML content,
 * delegating the actual XML parsing to the data layer.
 */
class ParseTtmlUseCase @Inject constructor(
    private val ttmlParser: TtmlParser
) {

    /**
     * Parse TTML content into domain model.
     * @param lines List of lines from TTML file
     * @return Parsed SyncedLyrics domain model
     */
    operator fun invoke(lines: List<String>): SyncedLyrics {
        // Business logic for TTML parsing coordination
        // The actual XML parsing is delegated to the injected parser
        // This use case orchestrates the parsing process and applies business rules
        return ttmlParser.parse(lines)
    }
}