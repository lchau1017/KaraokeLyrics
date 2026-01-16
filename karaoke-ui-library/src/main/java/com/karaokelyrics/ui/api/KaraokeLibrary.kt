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
     *
     * @param line The synchronized line to display
     * @param currentTimeMs Current playback time in milliseconds
     * @param config Complete configuration for visual, animation, and behavior
     * @param modifier Modifier for the composable
     * @param onLineClick Optional callback when line is clicked
     */
    @Composable
    fun KaraokeLineDisplay(
        line: ISyncedLine,
        currentTimeMs: Int,
        config: KaraokeLibraryConfig = KaraokeLibraryConfig.Default,
        modifier: Modifier = Modifier,
        onLineClick: ((ISyncedLine) -> Unit)? = null
    ) {
        com.karaokelyrics.ui.components.KaraokeLineDisplay(
            line = line,
            currentTimeMs = currentTimeMs,
            config = config,
            modifier = modifier,
            onLineClick = onLineClick
        )
    }

    /**
     * Display multiple karaoke lines with automatic scrolling and synchronization.
     *
     * @param lines List of synchronized lines to display
     * @param currentTimeMs Current playback time in milliseconds
     * @param config Complete configuration for visual, animation, and behavior
     * @param modifier Modifier for the composable
     * @param onLineClick Optional callback when a line is clicked
     * @param onLineLongPress Optional callback when a line is long-pressed
     */
    @Composable
    fun KaraokeLyricsDisplay(
        lines: List<ISyncedLine>,
        currentTimeMs: Int,
        config: KaraokeLibraryConfig = KaraokeLibraryConfig.Default,
        modifier: Modifier = Modifier,
        onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
        onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
    ) {
        com.karaokelyrics.ui.components.KaraokeLyricsDisplay(
            lines = lines,
            currentTimeMs = currentTimeMs,
            config = config,
            modifier = modifier,
            onLineClick = onLineClick,
            onLineLongPress = onLineLongPress
        )
    }

    /**
     * Advanced: Display karaoke lines with a custom renderer.
     * Provides full control over how each line is rendered.
     *
     * @param lines List of synchronized lines to display
     * @param currentTimeMs Current playback time in milliseconds
     * @param config Complete configuration for visual, animation, and behavior
     * @param lineRenderer Custom composable for rendering each line
     * @param modifier Modifier for the composable
     */
    @Composable
    fun KaraokeCustomDisplay(
        lines: List<ISyncedLine>,
        currentTimeMs: Int,
        config: KaraokeLibraryConfig,
        lineRenderer: @Composable (ISyncedLine, Int, KaraokeLibraryConfig) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            LazyColumn {
                itemsIndexed(lines) { index, line ->
                    lineRenderer(line, currentTimeMs, config)
                }
            }
        }
    }
}