package com.karaokelyrics.ui.components.viewers

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.karaokelyrics.ui.components.KaraokeSingleLine
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.utils.LineStateUtils

/**
 * Stacked viewer with z-layer overlapping effect.
 * Active line appears on top with played lines stacking underneath.
 */
@Composable
internal fun StackedViewer(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
) {
    // Find current line index
    val currentLineIndex = remember(currentTimeMs, lines) {
        LineStateUtils.getCurrentLineIndex(lines, currentTimeMs)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        lines.forEachIndexed { index, line ->
            val lineState = remember(line, currentTimeMs) {
                LineStateUtils.getLineState(line, currentTimeMs)
            }

            val distance = currentLineIndex?.let {
                index - it
            } ?: 999

            // Determine if line should be shown - only show active and upcoming
            val shouldShow = when {
                lineState.isPlaying -> true
                lineState.isUpcoming && distance <= 2 -> true // Show next 2 upcoming lines
                else -> false // Don't show played lines
            }

            if (shouldShow) {
                // Calculate z-index - active on top, upcoming below
                val zIndex = when {
                    lineState.isPlaying -> 1000f // Active line on top
                    else -> 999f - distance // Upcoming lines below
                }

                // Calculate vertical offset for stacking effect
                val yOffset = when {
                    lineState.isPlaying -> 0f
                    lineState.isUpcoming && distance == 1 -> 50f // Next line slightly below
                    lineState.isUpcoming && distance == 2 -> 100f // Second upcoming further below
                    else -> 150f
                }

                // Calculate opacity
                val opacity = when {
                    lineState.isPlaying -> 1f
                    lineState.isUpcoming && distance == 1 -> 0.5f
                    lineState.isUpcoming && distance == 2 -> 0.3f
                    else -> 0f
                }

                // Calculate scale for depth effect
                val scale = when {
                    lineState.isPlaying -> 1f
                    lineState.isUpcoming && distance == 1 -> 0.9f
                    lineState.isUpcoming && distance == 2 -> 0.85f
                    else -> 0.8f
                }

                // Animate the transition
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
                    KaraokeSingleLine(
                        line = line,
                        currentTimeMs = currentTimeMs,
                        config = config,
                        onLineClick = onLineClick?.let { { it(line, index) } }
                    )
                }
            }
        }
    }
}