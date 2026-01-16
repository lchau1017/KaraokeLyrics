package com.karaokelyrics.app.data.factory

import com.karaokelyrics.app.data.parser.ParsedLyricsData
import com.karaokelyrics.app.data.parser.ParsedLine
import com.karaokelyrics.app.data.parser.ParsedSyllable
import com.karaokelyrics.app.domain.model.ISyncedLine
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.model.karaoke.KaraokeAlignment
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.domain.model.karaoke.KaraokeSyllable
import javax.inject.Inject

/**
 * Factory for creating domain models from parsed lyrics data.
 * Single Responsibility: Only handles domain model creation.
 * This separates parsing logic from domain model construction.
 */
class LyricsFactory @Inject constructor() {

    /**
     * Create SyncedLyrics domain model from parsed data.
     *
     * @param parsedData The parsed lyrics data
     * @return SyncedLyrics domain model
     */
    fun createSyncedLyrics(parsedData: ParsedLyricsData): SyncedLyrics {
        val lines = parsedData.lines.map { parsedLine ->
            createKaraokeLine(parsedLine)
        }
        return SyncedLyrics(lines)
    }

    private fun createKaraokeLine(parsedLine: ParsedLine): ISyncedLine {
        val syllables = parsedLine.syllables.map { parsedSyllable ->
            createKaraokeSyllable(parsedSyllable)
        }

        return KaraokeLine(
            syllables = syllables,
            start = parsedLine.startMs,
            end = parsedLine.endMs,
            alignment = KaraokeAlignment.Center,
            isAccompaniment = parsedLine.isBackgroundVocal
        )
    }

    private fun createKaraokeSyllable(parsedSyllable: ParsedSyllable): KaraokeSyllable {
        return KaraokeSyllable(
            content = parsedSyllable.content,
            start = parsedSyllable.startMs,
            end = parsedSyllable.endMs
        )
    }
}