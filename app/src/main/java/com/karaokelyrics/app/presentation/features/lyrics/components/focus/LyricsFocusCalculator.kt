package com.karaokelyrics.app.presentation.features.lyrics.components.focus

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import com.karaokelyrics.app.domain.model.ISyncedLine
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine

/**
 * Calculates which lyrics lines are currently focused/active.
 * Single Responsibility: Focus state calculation only.
 */
object LyricsFocusCalculator {

    fun findFocusedLineIndex(
        lines: List<ISyncedLine>,
        currentTimeMs: Int
    ): Int {
        val rawIndex = lines.indexOfLast { line ->
            currentTimeMs >= line.start && currentTimeMs < line.end
        }

        // If it's an accompaniment line, find the nearest main line
        if (rawIndex >= 0) {
            val line = lines[rawIndex] as? KaraokeLine
            if (line != null && line.isAccompaniment) {
                // Find the previous non-accompaniment line
                for (i in rawIndex downTo 0) {
                    val checkLine = lines[i] as? KaraokeLine
                    if (checkLine == null || !checkLine.isAccompaniment) {
                        return i
                    }
                }
            }
        }
        return rawIndex
    }

    fun findAllFocusedIndices(
        lines: List<ISyncedLine>,
        currentTimeMs: Int
    ): List<Int> {
        return lines.mapIndexedNotNull { index, line ->
            // Only highlight if we're actually within the line timing
            if (currentTimeMs >= line.start && currentTimeMs < line.end) index else null
        }
    }

    fun calculateDistanceFromCurrent(
        index: Int,
        focusedIndices: List<Int>,
        lines: List<ISyncedLine>,
        currentTimeMs: Int
    ): Int {
        return when {
            focusedIndices.isEmpty() -> {
                // No active line - calculate based on whether played or upcoming
                val line = lines[index]
                val hasBeenPlayed = line.end <= currentTimeMs

                if (hasBeenPlayed) {
                    // Distance from last played line
                    val lastPlayedIndex = lines.indexOfLast { it.end <= currentTimeMs }
                    if (lastPlayedIndex >= 0) kotlin.math.abs(index - lastPlayedIndex) + 3
                    else Int.MAX_VALUE
                } else {
                    // Distance from next upcoming line
                    val nextIndex = lines.indexOfFirst { it.start > currentTimeMs }
                    if (nextIndex >= 0) kotlin.math.abs(index - nextIndex) + 3
                    else Int.MAX_VALUE
                }
            }
            index < focusedIndices.first() -> focusedIndices.first() - index
            index > focusedIndices.last() -> index - focusedIndices.last()
            else -> 0
        }
    }
}

@Composable
fun rememberFocusedLineIndex(
    lyrics: SyncedLyrics,
    currentTimeMs: Int
): State<Int> {
    return remember(lyrics.lines, currentTimeMs) {
        derivedStateOf {
            LyricsFocusCalculator.findFocusedLineIndex(lyrics.lines, currentTimeMs)
        }
    }
}

@Composable
fun rememberAllFocusedIndices(
    lyrics: SyncedLyrics,
    currentTimeMs: Int
): State<List<Int>> {
    return remember(lyrics.lines, currentTimeMs) {
        derivedStateOf {
            LyricsFocusCalculator.findAllFocusedIndices(lyrics.lines, currentTimeMs)
        }
    }
}