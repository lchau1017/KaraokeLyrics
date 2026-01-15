package com.karaokelyrics.app.domain.usecase

import com.karaokelyrics.app.domain.model.SyncedLyrics
import javax.inject.Inject

/**
 * Domain use case that orchestrates TTML parsing.
 * This encapsulates the business logic for parsing TTML content,
 * delegating the actual XML parsing to the data layer.
 */
class ParseTtmlUseCase @Inject constructor() {

    /**
     * Parse TTML content into domain model.
     * @param lines List of lines from TTML file
     * @return Parsed SyncedLyrics domain model
     */
    operator fun invoke(lines: List<String>): SyncedLyrics {
        // Business logic for TTML parsing coordination
        // The actual XML parsing will be delegated to data layer parser
        // This use case orchestrates the parsing process and applies business rules

        // For now, we'll create a data layer parser instance
        // In a more complete implementation, this would be injected
        val parser = com.karaokelyrics.app.data.parser.TtmlParser()
        return parser.parse(lines)
    }
}