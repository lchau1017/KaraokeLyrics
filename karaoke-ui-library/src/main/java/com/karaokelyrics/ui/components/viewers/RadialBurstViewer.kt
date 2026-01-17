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
 * Radial burst viewer with lines emerging from center.
 * Creates ripple/explosion effect with pulsing active line.
 */
@Composable
internal fun RadialBurstViewer(
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

    // Pulse animation for active line
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Burst animation on line change
    var previousIndex by remember { mutableStateOf(-1) }
    val burstAnimation = remember { Animatable(0f) }

    LaunchedEffect(currentLineIndex) {
        if (currentLineIndex != previousIndex) {
            previousIndex = currentLineIndex
            burstAnimation.snapTo(0f)
            burstAnimation.animateTo(
                targetValue = 1f,
                animationSpec = tween(600, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        lines.forEachIndexed { index, line ->
            val distance = kotlin.math.abs(index - currentLineIndex)

            if (distance <= 3) {
                val lineState = remember(line, currentTimeMs) {
                    animationManager.getLineState(line, currentTimeMs)
                }

                val radiusMultiplier = when {
                    lineState.isPlaying -> 0f
                    else -> distance.toFloat()
                }

                val expandRadius = burstAnimation.value * 150f * radiusMultiplier
                val opacity = when {
                    lineState.isPlaying -> 1f
                    else -> (1f - burstAnimation.value) * 0.5f
                }

                val scale = when {
                    lineState.isPlaying -> pulseScale
                    else -> 1f - (distance * 0.2f)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            // Create radial expansion
                            val angle = (index * 137.5f) // Golden angle for better distribution
                            val radians = Math.toRadians(angle.toDouble())
                            translationX = (kotlin.math.cos(radians) * expandRadius).toFloat()
                            translationY = (kotlin.math.sin(radians) * expandRadius).toFloat()
                            scaleX = scale
                            scaleY = scale
                            alpha = opacity
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