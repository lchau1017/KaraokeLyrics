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
import com.karaokelyrics.ui.rendering.AnimationManager

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
    val animationManager = remember { AnimationManager() }

    // Find current line index
    val currentLineIndex = remember(currentTimeMs, lines) {
        animationManager.getCurrentLineIndex(lines, currentTimeMs)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        lines.forEachIndexed { index, line ->
            val lineState = remember(line, currentTimeMs) {
                animationManager.getLineState(line, currentTimeMs)
            }

            val distance = currentLineIndex?.let {
                index - it
            } ?: 999

            // Determine if line should be shown - only show active and next upcoming line
            val shouldShow = when {
                lineState.isPlaying -> true
                lineState.isUpcoming && distance == 1 -> true // Show only the very next line
                else -> false // Don't show played lines or other upcoming
            }

            if (shouldShow) {
                // Calculate z-index - active on top, next below
                val zIndex = when {
                    lineState.isPlaying -> 1000f // Active line on top
                    else -> 999f // Next line below
                }

                // Calculate vertical offset for stacking effect
                val yOffset = when {
                    lineState.isPlaying -> 0f
                    lineState.isUpcoming && distance == 1 -> 60f // Next line below and back
                    else -> 0f
                }

                // Calculate opacity - next line much more transparent
                val opacity = when {
                    lineState.isPlaying -> 1f
                    lineState.isUpcoming && distance == 1 -> 0.25f // Much more transparent
                    else -> 0f
                }

                // Calculate scale for depth effect - next line much smaller
                val scale = when {
                    lineState.isPlaying -> 1f
                    lineState.isUpcoming && distance == 1 -> 0.7f // Much smaller for depth
                    else -> 1f
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