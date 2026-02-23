package com.karaokelyrics.app.domain.usecase

import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.parser.LyricsParser
import javax.inject.Inject

/**
 * Domain use case that orchestrates lyrics parsing.
 * Delegates to [LyricsParser] for format-specific parsing.
 */
class ParseLyricsUseCase @Inject constructor(
    private val lyricsParser: LyricsParser
) {
    operator fun invoke(lines: List<String>): SyncedLyrics {
        return SyncedLyrics(lyricsParser.parse(lines))
    }
}
