package com.kyrics.components.viewers

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.kyrics.components.KyricsSingleLine
import com.kyrics.config.KyricsConfig
import com.kyrics.models.ISyncedLine
import com.kyrics.state.KyricsUiState

/**
 * Radial burst viewer with lines emerging from center.
 * Creates ripple/explosion effect with pulsing active line.
 */
@Suppress("CognitiveComplexMethod")
@Composable
internal fun RadialBurstViewer(uiState: KyricsUiState, config: KyricsConfig, onLineClick: ((ISyncedLine, Int) -> Unit)? = null) {
    val currentLineIndex = uiState.currentLineIndex ?: 0

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
        uiState.lines.forEachIndexed { index, line ->
            val distance = kotlin.math.abs(index - currentLineIndex)
            val lineUiState = uiState.getLineState(index)

            if (distance <= config.effects.visibleLineRange) {
                val radiusMultiplier = when {
                    lineUiState.isPlaying -> 0f
                    else -> distance.toFloat()
                }

                val expandRadius = burstAnimation.value * 150f * radiusMultiplier
                val opacity = when {
                    lineUiState.isPlaying -> 1f
                    else -> (1f - burstAnimation.value) * 0.5f
                }

                val scale = when {
                    lineUiState.isPlaying -> pulseScale
                    else -> 1f - (distance * 0.2f)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            val angle = (index * 137.5f)
                            val radians = Math.toRadians(angle.toDouble())
                            translationX = (kotlin.math.cos(radians) * expandRadius).toFloat()
                            translationY = (kotlin.math.sin(radians) * expandRadius).toFloat()
                            scaleX = scale
                            scaleY = scale
                            alpha = opacity
                        }
                ) {
                    KyricsSingleLine(
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
