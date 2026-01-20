package com.karaokelyrics.app.domain.usecase

import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.kyrics.parseLyrics
import com.kyrics.parser.ParseResult
import javax.inject.Inject
import timber.log.Timber

/**
 * Domain use case that orchestrates lyrics parsing.
 * Uses Kyrics library for parsing TTML, LRC, and Enhanced LRC formats.
 */
class ParseLyricsUseCase @Inject constructor() {

    /**
     * Parse lyrics content into domain model.
     * Supports TTML, LRC, and Enhanced LRC formats with auto-detection.
     *
     * @param lines List of lines from lyrics file
     * @return Parsed SyncedLyrics domain model
     */
    operator fun invoke(lines: List<String>): SyncedLyrics {
        val content = lines.joinToString("\n")
        Timber.d("ParseLyricsUseCase: Parsing content with ${content.length} chars")
        return when (val result = parseLyrics(content)) {
            is ParseResult.Success -> {
                Timber.d("ParseLyricsUseCase: Success - parsed ${result.lines.size} lines")
                SyncedLyrics(result.lines)
            }
            is ParseResult.Failure -> {
                Timber.e("ParseLyricsUseCase: Failed - ${result.error}")
                SyncedLyrics(emptyList())
            }
        }
    }
}
