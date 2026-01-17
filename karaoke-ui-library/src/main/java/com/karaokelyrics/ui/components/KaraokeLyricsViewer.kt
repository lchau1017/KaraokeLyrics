package com.karaokelyrics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.karaokelyrics.ui.components.viewers.CenterFocusedViewer
import com.karaokelyrics.ui.components.viewers.PagedViewer
import com.karaokelyrics.ui.components.viewers.SingleLineViewer
import com.karaokelyrics.ui.components.viewers.SmoothScrollViewer
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.config.ViewerType
import com.karaokelyrics.ui.core.models.ISyncedLine

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
    // Delegate to the appropriate viewer based on config
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(config.visual.backgroundColor)
            .padding(config.layout.containerPadding)
    ) {
        when (config.layout.viewerConfig.type) {
            ViewerType.CENTER_FOCUSED -> {
                CenterFocusedViewer(
                    lines = lines,
                    currentTimeMs = currentTimeMs,
                    config = config,
                    onLineClick = onLineClick,
                    onLineLongPress = onLineLongPress
                )
            }
            ViewerType.SMOOTH_SCROLL -> {
                SmoothScrollViewer(
                    lines = lines,
                    currentTimeMs = currentTimeMs,
                    config = config,
                    onLineClick = onLineClick,
                    onLineLongPress = onLineLongPress
                )
            }
            ViewerType.SINGLE_LINE -> {
                SingleLineViewer(
                    lines = lines,
                    currentTimeMs = currentTimeMs,
                    config = config,
                    onLineClick = onLineClick,
                    onLineLongPress = onLineLongPress
                )
            }
            ViewerType.PAGED -> {
                PagedViewer(
                    lines = lines,
                    currentTimeMs = currentTimeMs,
                    config = config,
                    onLineClick = onLineClick,
                    onLineLongPress = onLineLongPress
                )
            }
        }
    }
}