package com.karaokelyrics.app.domain.usecase

import com.karaokelyrics.app.domain.model.LyricsSyncState
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.kyrics.models.KyricsLine
import com.kyrics.models.SyncedLine
import javax.inject.Inject

class SyncLyricsUseCase @Inject constructor() {

    operator fun invoke(lyrics: SyncedLyrics, position: Long, timingOffsetMs: Int = 200): LyricsSyncState {
        // Add configurable offset so lyrics appear before audio
        val positionMs = (position + timingOffsetMs).toInt()

        // Find current line index
        val currentLineIndex = lyrics.lines.indexOfLast {
            it.start <= positionMs
        }.takeIf { it >= 0 && lyrics.lines[it].end >= positionMs } ?: -1

        // Get all highlighted lines (for multi-voice support)
        val allFocusedIndices = lyrics.lines.mapIndexedNotNull { index, line ->
            if (line.start <= positionMs && line.end >= positionMs) index else null
        }

        val currentLine = lyrics.lines.getOrNull(currentLineIndex)

        // Calculate progress for karaoke lines
        val (lineProgress, syllableIndex, syllableProgress) = when (currentLine) {
            is KyricsLine -> calculateKaraokeProgress(currentLine, positionMs)
            else -> Triple(
                calculateSimpleProgress(currentLine, positionMs),
                -1,
                0f
            )
        }

        val nextLineStartTime = lyrics.lines.getOrNull(currentLineIndex + 1)?.start

        return LyricsSyncState(
            currentLineIndex = currentLineIndex,
            lineProgress = lineProgress,
            currentSyllableIndex = syllableIndex,
            activeSyllableProgress = syllableProgress,
            nextLineStartTime = nextLineStartTime,
            allFocusedLineIndices = allFocusedIndices
        )
    }

    private fun calculateKaraokeProgress(line: KyricsLine, position: Int): Triple<Float, Int, Float> {
        if (position < line.start) return Triple(0f, -1, 0f)
        if (position > line.end) return Triple(1f, line.syllables.size - 1, 1f)

        val activeSyllableIndex = line.syllables.indexOfFirst {
            position in it.start..it.end
        }

        if (activeSyllableIndex == -1) {
            // Between syllables
            val lastCompletedIndex = line.syllables.indexOfLast {
                position > it.end
            }
            val progress = (position - line.start).toFloat() / (line.end - line.start)
            return Triple(progress, lastCompletedIndex, 1f)
        }

        val activeSyllable = line.syllables[activeSyllableIndex]
        val syllableProgress = (position - activeSyllable.start).toFloat() /
            (activeSyllable.end - activeSyllable.start)

        val overallProgress = (activeSyllableIndex + syllableProgress) / line.syllables.size

        return Triple(overallProgress, activeSyllableIndex, syllableProgress)
    }

    private fun calculateSimpleProgress(line: SyncedLine?, position: Int): Float {
        if (line == null) return 0f

        if (position < line.start) return 0f
        if (position > line.end) return 1f

        return (position - line.start).toFloat() / (line.end - line.start)
    }
}
