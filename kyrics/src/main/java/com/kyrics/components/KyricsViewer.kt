package com.kyrics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.kyrics.components.viewers.*
import com.kyrics.config.KyricsConfig
import com.kyrics.config.ViewerType
import com.kyrics.models.ISyncedLine
import com.kyrics.state.KyricsUiState
import com.kyrics.state.rememberKyricsStateHolder

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
 */
@Composable
fun KyricsViewer(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    config: KyricsConfig = KyricsConfig.Default,
    modifier: Modifier = Modifier,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null
) {
    // Create and manage state holder internally
    val stateHolder = rememberKyricsStateHolder(config)

    // Update state when inputs change
    LaunchedEffect(lines) {
        stateHolder.setLines(lines)
    }

    LaunchedEffect(currentTimeMs) {
        stateHolder.updateTime(currentTimeMs)
    }

    // Get current UI state
    val uiState by stateHolder.uiState

    // Render the viewer
    KyricsViewerContent(
        uiState = uiState,
        config = config,
        modifier = modifier,
        onLineClick = onLineClick
    )
}

/**
 * Internal content renderer that uses KyricsUiState.
 * This separates state management from rendering.
 */
@Composable
private fun KyricsViewerContent(
    uiState: KyricsUiState,
    config: KyricsConfig,
    modifier: Modifier = Modifier,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(config.visual.backgroundColor)
            .padding(config.layout.containerPadding)
    ) {
        when (config.layout.viewerConfig.type) {
            ViewerType.CENTER_FOCUSED -> {
                CenterFocusedViewer(
                    uiState = uiState,
                    config = config,
                    onLineClick = onLineClick
                )
            }
            ViewerType.SMOOTH_SCROLL -> {
                SmoothScrollViewer(
                    uiState = uiState,
                    config = config,
                    onLineClick = onLineClick
                )
            }
            ViewerType.STACKED -> {
                StackedViewer(
                    uiState = uiState,
                    config = config,
                    onLineClick = onLineClick
                )
            }
            ViewerType.HORIZONTAL_PAGED -> {
                HorizontalPagedViewer(
                    uiState = uiState,
                    config = config,
                    onLineClick = onLineClick
                )
            }
            ViewerType.WAVE_FLOW -> {
                WaveFlowViewer(
                    uiState = uiState,
                    config = config,
                    onLineClick = onLineClick
                )
            }
            ViewerType.SPIRAL -> {
                SpiralViewer(
                    uiState = uiState,
                    config = config,
                    onLineClick = onLineClick
                )
            }
            ViewerType.CAROUSEL_3D -> {
                Carousel3DViewer(
                    uiState = uiState,
                    config = config,
                    onLineClick = onLineClick
                )
            }
            ViewerType.SPLIT_DUAL -> {
                SplitDualViewer(
                    uiState = uiState,
                    config = config,
                    onLineClick = onLineClick
                )
            }
            ViewerType.ELASTIC_BOUNCE -> {
                ElasticBounceViewer(
                    uiState = uiState,
                    config = config,
                    onLineClick = onLineClick
                )
            }
            ViewerType.FADE_THROUGH -> {
                FadeThroughViewer(
                    uiState = uiState,
                    config = config,
                    onLineClick = onLineClick
                )
            }
            ViewerType.RADIAL_BURST -> {
                RadialBurstViewer(
                    uiState = uiState,
                    config = config,
                    onLineClick = onLineClick
                )
            }
            ViewerType.FLIP_CARD -> {
                FlipCardViewer(
                    uiState = uiState,
                    config = config,
                    onLineClick = onLineClick
                )
            }
        }
    }
}
