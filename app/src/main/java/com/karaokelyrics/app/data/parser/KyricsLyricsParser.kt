package com.karaokelyrics.app.data.parser

import com.karaokelyrics.app.domain.model.LyricsLine
import com.karaokelyrics.app.domain.model.LyricsSyllable
import com.karaokelyrics.app.domain.parser.LyricsParser
import com.kyrics.parseLyrics
import com.kyrics.parser.ParseResult
import timber.log.Timber
import javax.inject.Inject

/**
 * Data layer implementation of [LyricsParser].
 * Uses the Kyrics library to parse TTML format and maps to domain models.
 */
class KyricsLyricsParser @Inject constructor() : LyricsParser {

    override fun parse(lines: List<String>): List<LyricsLine> {
        val content = lines.joinToString("\n")
        Timber.d("KyricsLyricsParser: Parsing content with ${content.length} chars")
        return when (val result = parseLyrics(content)) {
            is ParseResult.Success -> {
                Timber.d("KyricsLyricsParser: Success - parsed ${result.lines.size} lines")
                result.lines.map { kyricsLine ->
                    LyricsLine(
                        start = kyricsLine.start,
                        end = kyricsLine.end,
                        syllables = kyricsLine.syllables.map { kyricsSyllable ->
                            LyricsSyllable(
                                start = kyricsSyllable.start,
                                end = kyricsSyllable.end,
                                content = kyricsSyllable.content
                            )
                        }
                    )
                }
            }
            is ParseResult.Failure -> {
                Timber.e("KyricsLyricsParser: Failed - ${result.error}")
                emptyList()
            }
        }
    }
}
