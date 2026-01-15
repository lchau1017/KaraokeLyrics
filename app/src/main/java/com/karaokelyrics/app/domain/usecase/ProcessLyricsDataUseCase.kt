package com.karaokelyrics.app.domain.usecase

import com.karaokelyrics.app.domain.model.ISyncedLine
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import javax.inject.Inject

/**
 * Domain use case for processing and transforming lyrics data.
 * Applies business rules to transform raw parsed data into the final format.
 */
class ProcessLyricsDataUseCase @Inject constructor() {

    /**
     * Process and transform lyrics data.
     * Applies business rules like:
     * - Sorting lines by start time
     * - Validating timing data
     * - Merging overlapping lines
     * - Handling special cases (accompaniment tracks, etc.)
     */
    operator fun invoke(lyrics: SyncedLyrics): SyncedLyrics {
        // Sort lines by start time
        val sortedLines = lyrics.lines.sortedBy { line ->
            when (line) {
                is KaraokeLine -> line.start
                else -> 0
            }
        }

        // Validate timing data
        val validatedLines = sortedLines.filter { line ->
            when (line) {
                is KaraokeLine -> validateKaraokeLine(line)
                else -> true
            }
        }

        // Apply any other business transformations
        val processedLines = validatedLines.map { line ->
            when (line) {
                is KaraokeLine -> processKaraokeLine(line)
                else -> line
            }
        }

        return SyncedLyrics(processedLines)
    }

    private fun validateKaraokeLine(line: KaraokeLine): Boolean {
        // Validate that line has valid timing
        if (line.start < 0 || line.end <= line.start) {
            return false
        }

        // Validate that syllables have valid timing
        return line.syllables.all { syllable ->
            syllable.start >= 0 && syllable.end > syllable.start
        }
    }

    private fun processKaraokeLine(line: KaraokeLine): KaraokeLine {
        // Trim trailing spaces from last syllable
        val processedSyllables = line.syllables.toMutableList()
        if (processedSyllables.isNotEmpty()) {
            val lastIndex = processedSyllables.lastIndex
            processedSyllables[lastIndex] = processedSyllables[lastIndex].copy(
                content = processedSyllables[lastIndex].content.trimEnd()
            )
        }

        // Ensure syllables are sorted by start time
        val sortedSyllables = processedSyllables.sortedBy { it.start }

        return line.copy(syllables = sortedSyllables)
    }

    /**
     * Merge overlapping or duplicate lines based on business rules.
     */
    fun mergeOverlappingLines(lyrics: SyncedLyrics): SyncedLyrics {
        val mergedLines = mutableListOf<ISyncedLine>()
        var previousLine: KaraokeLine? = null

        for (line in lyrics.lines) {
            when (line) {
                is KaraokeLine -> {
                    if (previousLine != null && shouldMergeLines(previousLine, line)) {
                        // Merge lines
                        previousLine = mergeTwoLines(previousLine, line)
                    } else {
                        // Add previous line if exists
                        previousLine?.let { mergedLines.add(it) }
                        previousLine = line
                    }
                }
                else -> mergedLines.add(line)
            }
        }

        // Add the last line
        previousLine?.let { mergedLines.add(it) }

        return SyncedLyrics(mergedLines)
    }

    private fun shouldMergeLines(line1: KaraokeLine, line2: KaraokeLine): Boolean {
        // Business rule: Merge if lines overlap and have same alignment
        return line1.end > line2.start &&
               line1.alignment == line2.alignment &&
               line1.isAccompaniment == line2.isAccompaniment
    }

    private fun mergeTwoLines(line1: KaraokeLine, line2: KaraokeLine): KaraokeLine {
        return KaraokeLine(
            syllables = line1.syllables + line2.syllables,
            start = line1.start,
            end = maxOf(line1.end, line2.end),
            alignment = line1.alignment,
            isAccompaniment = line1.isAccompaniment
        )
    }
}