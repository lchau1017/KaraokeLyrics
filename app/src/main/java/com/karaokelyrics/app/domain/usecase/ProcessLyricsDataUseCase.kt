package com.karaokelyrics.app.domain.usecase

import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.kyrics.models.KyricsLine
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
     */
    operator fun invoke(lyrics: SyncedLyrics): SyncedLyrics {
        // Sort lines by start time
        val sortedLines = lyrics.lines.sortedBy { it.start }

        // Validate timing data
        val validatedLines = sortedLines.filter { line ->
            when (line) {
                is KyricsLine -> validateKyricsLine(line)
                else -> line.start >= 0 && line.end > line.start
            }
        }

        // Apply any other business transformations
        val processedLines = validatedLines.map { line ->
            when (line) {
                is KyricsLine -> processKyricsLine(line)
                else -> line
            }
        }

        return SyncedLyrics(processedLines)
    }

    private fun validateKyricsLine(line: KyricsLine): Boolean {
        // Validate that line has valid timing
        if (line.start < 0 || line.end <= line.start) {
            return false
        }

        // Validate that syllables have valid timing
        return line.syllables.all { syllable ->
            syllable.start >= 0 && syllable.end > syllable.start
        }
    }

    private fun processKyricsLine(line: KyricsLine): KyricsLine {
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
}
