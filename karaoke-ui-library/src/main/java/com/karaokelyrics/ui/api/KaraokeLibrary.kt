package com.karaokelyrics.ui.api

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine

/**
 * Main entry point for the Karaoke UI Library.
 * Provides high-level composable functions for displaying karaoke content.
 */
object KaraokeLibrary {

    /**
     * Display a single karaoke line with synchronized highlighting.
     * Handles character-by-character progression and all visual effects.
     *
     * @param line The synchronized line to display
     * @param currentTimeMs Current playback time in milliseconds
     * @param config Complete configuration for visual, animation, and behavior
     * @param modifier Modifier for the composable
     * @param onLineClick Optional callback when line is clicked
     */
    @Composable
    fun KaraokeSingleLine(
        line: ISyncedLine,
        currentTimeMs: Int,
        config: KaraokeLibraryConfig = KaraokeLibraryConfig.Default,
        modifier: Modifier = Modifier,
        onLineClick: ((ISyncedLine) -> Unit)? = null
    ) {
        com.karaokelyrics.ui.components.KaraokeSingleLine(
            line = line,
            currentTimeMs = currentTimeMs,
            config = config,
            modifier = modifier,
            onLineClick = onLineClick
        )
    }

    /**
     * Complete karaoke lyrics viewer with automatic scrolling and synchronization.
     * This container manages the entire lyrics display experience, including:
     * - Auto-scrolling to keep current line in view
     * - Distance-based visual effects (blur, opacity)
     * - Intelligent spacing between line groups
     * - Smooth transitions and animations
     *
     * @param lines List of synchronized lines to display
     * @param currentTimeMs Current playback time in milliseconds
     * @param config Complete configuration for visual, animation, and behavior
     * @param modifier Modifier for the composable
     * @param onLineClick Optional callback when a line is clicked
     * @param onLineLongPress Optional callback when a line is long-pressed
     */
    @Composable
    fun KaraokeLyricsViewer(
        lines: List<ISyncedLine>,
        currentTimeMs: Int,
        config: KaraokeLibraryConfig = KaraokeLibraryConfig.Default,
        modifier: Modifier = Modifier,
        onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
        onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
    ) {
        com.karaokelyrics.ui.components.KaraokeLyricsViewer(
            lines = lines,
            currentTimeMs = currentTimeMs,
            config = config,
            modifier = modifier,
            onLineClick = onLineClick,
            onLineLongPress = onLineLongPress
        )
    }
}