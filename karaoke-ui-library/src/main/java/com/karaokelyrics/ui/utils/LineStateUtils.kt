package com.karaokelyrics.ui.utils

import com.karaokelyrics.ui.core.models.ISyncedLine

/**
 * Utility functions for calculating line states in karaoke display.
 */
object LineStateUtils {

    /**
     * Data class representing the state of a line.
     */
    data class LineState(
        val isPlaying: Boolean,
        val hasPlayed: Boolean,
        val isUpcoming: Boolean
    )

    /**
     * Calculate the state of a line based on current time.
     */
    fun getLineState(line: ISyncedLine, currentTimeMs: Int): LineState {
        return LineState(
            isPlaying = currentTimeMs in line.start..line.end,
            hasPlayed = currentTimeMs > line.end,
            isUpcoming = currentTimeMs < line.start
        )
    }

    /**
     * Find the index of the currently playing line.
     */
    fun getCurrentLineIndex(lines: List<ISyncedLine>, currentTimeMs: Int): Int? {
        return lines.indexOfFirst { line ->
            currentTimeMs in line.start..line.end
        }.takeIf { it != -1 }
    }

    /**
     * Find the index of the last played line.
     */
    fun getLastPlayedLineIndex(lines: List<ISyncedLine>, currentTimeMs: Int): Int? {
        return lines.indexOfLast { line ->
            currentTimeMs > line.end
        }.takeIf { it != -1 }
    }

    /**
     * Calculate the distance from the current playing line.
     */
    fun getDistanceFromCurrentLine(
        lineIndex: Int,
        currentLineIndex: Int?
    ): Int {
        return currentLineIndex?.let {
            kotlin.math.abs(lineIndex - it)
        } ?: 999
    }

    /**
     * Check if this is the first active line in a group.
     */
    fun isFirstActiveLine(
        index: Int,
        lines: List<ISyncedLine>,
        currentTimeMs: Int
    ): Boolean {
        if (index == 0) return false

        val currentLine = lines[index]
        val previousLine = lines[index - 1]

        val isCurrentPlaying = currentTimeMs in currentLine.start..currentLine.end
        val isPreviousPlaying = currentTimeMs in previousLine.start..previousLine.end

        return isCurrentPlaying && !isPreviousPlaying
    }

    /**
     * Check if this is the last played line.
     */
    fun isLastPlayedLine(
        index: Int,
        lines: List<ISyncedLine>,
        currentTimeMs: Int
    ): Boolean {
        val hasPlayed = currentTimeMs > lines[index].end
        val hasNextLine = index < lines.size - 1
        val nextLineNotPlayed = hasNextLine && currentTimeMs <= lines[index + 1].end

        return hasPlayed && nextLineNotPlayed
    }

    /**
     * Check if this is the first upcoming line.
     */
    fun isFirstUpcomingLine(
        index: Int,
        lines: List<ISyncedLine>,
        currentTimeMs: Int
    ): Boolean {
        if (index == 0) return false

        val isUpcoming = currentTimeMs < lines[index].start
        val previousLineStarted = currentTimeMs >= lines[index - 1].start

        return isUpcoming && previousLineStarted
    }
}