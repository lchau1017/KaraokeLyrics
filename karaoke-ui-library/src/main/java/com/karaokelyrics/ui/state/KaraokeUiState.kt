package com.karaokelyrics.ui.state

import androidx.compose.runtime.Immutable
import com.karaokelyrics.ui.core.models.ISyncedLine

/**
 * Immutable UI state for the karaoke viewer.
 * Contains all data needed to render the karaoke display.
 */
@Immutable
data class KaraokeUiState(
    /**
     * List of synchronized lines to display
     */
    val lines: List<ISyncedLine> = emptyList(),

    /**
     * Current playback time in milliseconds
     */
    val currentTimeMs: Int = 0,

    /**
     * Index of the currently playing line, or null if no line is playing
     */
    val currentLineIndex: Int? = null,

    /**
     * Pre-calculated state for each line, keyed by line index
     */
    val lineStates: Map<Int, LineUiState> = emptyMap(),

    /**
     * Whether the state has been initialized with lines
     */
    val isInitialized: Boolean = false
) {
    /**
     * Get the currently playing line, if any
     */
    val currentLine: ISyncedLine?
        get() = currentLineIndex?.let { lines.getOrNull(it) }

    /**
     * Get the state for a specific line by index
     */
    fun getLineState(index: Int): LineUiState {
        return lineStates[index] ?: LineUiState()
    }
}
