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
 * Elastic bounce viewer with physics-based spring animations.
 * Creates playful, energetic transitions.
 */
@Suppress("CognitiveComplexMethod")
@Composable
internal fun ElasticBounceViewer(uiState: KyricsUiState, config: KyricsConfig, onLineClick: ((ISyncedLine, Int) -> Unit)? = null) {
    val currentLineIndex = uiState.currentLineIndex ?: 0
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
        uiState.lines.forEachIndexed { index, line ->
            val distance = index - currentLineIndex
            val lineUiState = uiState.getLineState(index)

            if (kotlin.math.abs(distance) <= config.effects.visibleLineRange) {
                // Position with bounce
                val yOffset = when {
                    lineUiState.isPlaying -> 0f
                    distance < 0 -> -200f
                    distance > 0 -> 200f
                    else -> 0f
                }

                val scale = if (lineUiState.isPlaying) bounceAnimation.value else 0.8f
                val alpha = when {
                    lineUiState.isPlaying -> 1f
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
