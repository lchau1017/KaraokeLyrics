package com.karaokelyrics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.karaokelyrics.ui.components.viewers.*
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
            ViewerType.STACKED -> {
                StackedViewer(
                    lines = lines,
                    currentTimeMs = currentTimeMs,
                    config = config,
                    onLineClick = onLineClick,
                    onLineLongPress = onLineLongPress
                )
            }
            ViewerType.HORIZONTAL_PAGED -> {
                HorizontalPagedViewer(
                    lines = lines,
                    currentTimeMs = currentTimeMs,
                    config = config,
                    onLineClick = onLineClick,
                    onLineLongPress = onLineLongPress
                )
            }
            ViewerType.WAVE_FLOW -> {
                WaveFlowViewer(
                    lines = lines,
                    currentTimeMs = currentTimeMs,
                    config = config,
                    onLineClick = onLineClick,
                    onLineLongPress = onLineLongPress
                )
            }
            ViewerType.SPIRAL -> {
                SpiralViewer(
                    lines = lines,
                    currentTimeMs = currentTimeMs,
                    config = config,
                    onLineClick = onLineClick,
                    onLineLongPress = onLineLongPress
                )
            }
            ViewerType.CAROUSEL_3D -> {
                Carousel3DViewer(
                    lines = lines,
                    currentTimeMs = currentTimeMs,
                    config = config,
                    onLineClick = onLineClick,
                    onLineLongPress = onLineLongPress
                )
            }
            ViewerType.SPLIT_DUAL -> {
                SplitDualViewer(
                    lines = lines,
                    currentTimeMs = currentTimeMs,
                    config = config,
                    onLineClick = onLineClick,
                    onLineLongPress = onLineLongPress
                )
            }
            ViewerType.ELASTIC_BOUNCE -> {
                ElasticBounceViewer(
                    lines = lines,
                    currentTimeMs = currentTimeMs,
                    config = config,
                    onLineClick = onLineClick,
                    onLineLongPress = onLineLongPress
                )
            }
            ViewerType.FADE_THROUGH -> {
                FadeThroughViewer(
                    lines = lines,
                    currentTimeMs = currentTimeMs,
                    config = config,
                    onLineClick = onLineClick,
                    onLineLongPress = onLineLongPress
                )
            }
            ViewerType.RADIAL_BURST -> {
                RadialBurstViewer(
                    lines = lines,
                    currentTimeMs = currentTimeMs,
                    config = config,
                    onLineClick = onLineClick,
                    onLineLongPress = onLineLongPress
                )
            }
            ViewerType.FLIP_CARD -> {
                FlipCardViewer(
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