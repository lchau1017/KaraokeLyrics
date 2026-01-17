package com.karaokelyrics.ui.components.viewers

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import com.karaokelyrics.ui.components.KaraokeSingleLineStateless
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.state.KaraokeUiState

/**
 * Stacked viewer with z-layer overlapping effect.
 * Active line appears on top with played lines stacking underneath.
 */
@Composable
internal fun StackedViewer(
    uiState: KaraokeUiState,
    config: KaraokeLibraryConfig,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
) {
    val currentLineIndex = uiState.currentLineIndex

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        uiState.lines.forEachIndexed { index, line ->
            val lineUiState = uiState.getLineState(index)

            val distance = currentLineIndex?.let { index - it } ?: 999

            // Determine if line should be shown - only show active and next upcoming line
            val shouldShow = when {
                lineUiState.isPlaying -> true
                lineUiState.isUpcoming && distance == 1 -> true
                else -> false
            }

            if (shouldShow) {
                val zIndex = when {
                    lineUiState.isPlaying -> 1000f
                    else -> 999f
                }

                val yOffset = when {
                    lineUiState.isPlaying -> 0f
                    lineUiState.isUpcoming && distance == 1 -> 60f
                    else -> 0f
                }

                val opacity = when {
                    lineUiState.isPlaying -> 1f
                    lineUiState.isUpcoming && distance == 1 -> 0.25f
                    else -> 0f
                }

                val scale = when {
                    lineUiState.isPlaying -> 1f
                    lineUiState.isUpcoming && distance == 1 -> 0.7f
                    else -> 1f
                }

                val animatedYOffset by animateFloatAsState(
                    targetValue = yOffset,
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                    label = "yOffset"
                )

                val animatedOpacity by animateFloatAsState(
                    targetValue = opacity,
                    animationSpec = tween(durationMillis = 300),
                    label = "opacity"
                )

                val animatedScale by animateFloatAsState(
                    targetValue = scale,
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
                    label = "scale"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(zIndex)
                        .graphicsLayer {
                            translationY = animatedYOffset
                            scaleX = animatedScale
                            scaleY = animatedScale
                            alpha = animatedOpacity
                        }
                ) {
                    KaraokeSingleLineStateless(
                        line = line,
                        lineUiState = lineUiState,
                        currentTimeMs = uiState.currentTimeMs,
                        config = config,
                        onLineClick = onLineClick?.let { { it(line, index) } }
                    )
                }
            }
        }
    }
}
