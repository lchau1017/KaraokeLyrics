package com.karaokelyrics.ui.components.viewers

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.karaokelyrics.ui.components.KaraokeSingleLine
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.rendering.AnimationManager

/**
 * Elastic bounce viewer with physics-based spring animations.
 * Creates playful, energetic transitions.
 */
@Composable
internal fun ElasticBounceViewer(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
) {
    val animationManager = remember { AnimationManager() }

    val currentLineIndex = remember(currentTimeMs, lines) {
        animationManager.getCurrentLineIndex(lines, currentTimeMs)
    } ?: 0

    var previousIndex by remember { mutableStateOf(0) }

    // Trigger bounce animation on line change
    val bounceAnimation = remember { Animatable(1f) }

    LaunchedEffect(currentLineIndex) {
        if (currentLineIndex != previousIndex) {
            previousIndex = currentLineIndex
            // Bounce effect
            bounceAnimation.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            bounceAnimation.snapTo(0.5f)
            bounceAnimation.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        lines.forEachIndexed { index, line ->
            val distance = index - currentLineIndex

            if (kotlin.math.abs(distance) <= 2) {
                val lineState = remember(line, currentTimeMs) {
                    animationManager.getLineState(line, currentTimeMs)
                }

                // Position with bounce
                val yOffset = when {
                    lineState.isPlaying -> 0f
                    distance < 0 -> -200f // Above
                    distance > 0 -> 200f // Below
                    else -> 0f
                }

                val scale = if (lineState.isPlaying) bounceAnimation.value else 0.8f
                val alpha = when {
                    lineState.isPlaying -> 1f
                    kotlin.math.abs(distance) == 1 -> 0.4f
                    else -> 0.2f
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            translationY = yOffset * (1f - bounceAnimation.value)
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
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