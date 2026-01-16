package com.karaokelyrics.ui.api

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
        // TODO: Implementation will be added in Phase 3
        // This will use the internal components to render a single line
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
        // TODO: Implementation will be added in Phase 3
        // This will use LazyColumn with automatic scrolling
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
        // TODO: Implementation will be added in Phase 3
        // This allows apps to provide custom rendering logic
    }
}