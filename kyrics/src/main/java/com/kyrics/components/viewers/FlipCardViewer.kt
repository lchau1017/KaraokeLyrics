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
 * Flip card viewer with 3D card flip transitions.
 * Creates a page-turning effect with front/back metaphor.
 */
@Suppress("CognitiveComplexMethod")
@Composable
internal fun FlipCardViewer(uiState: KyricsUiState, config: KyricsConfig, onLineClick: ((ISyncedLine, Int) -> Unit)? = null) {
    val currentLineIndex = uiState.currentLineIndex ?: 0

    var previousIndex by remember { mutableStateOf(-1) }
    val flipAnimation = remember { Animatable(0f) }

    LaunchedEffect(currentLineIndex) {
        if (currentLineIndex != previousIndex && previousIndex != -1) {
            // Flip animation
            flipAnimation.animateTo(
                targetValue = 180f,
                animationSpec = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                )
            )
            previousIndex = currentLineIndex
            flipAnimation.snapTo(0f)
        } else {
            previousIndex = currentLineIndex
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val rotation = flipAnimation.value

        // Show current or previous line based on rotation
        val showingIndex = if (rotation < 90f) {
            if (previousIndex == -1) currentLineIndex else previousIndex
        } else {
            currentLineIndex
        }

        val line = uiState.lines.getOrNull(showingIndex)
        val lineUiState = uiState.getLineState(showingIndex)

        line?.let {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .graphicsLayer {
                        rotationY = if (rotation > 90f) rotation - 180f else rotation
                        cameraDistance = 12f * density
                        alpha = if (rotation > 85f && rotation < 95f) 0f else 1f
                    }
            ) {
                KyricsSingleLine(
                    line = it,
                    lineUiState = lineUiState,
                    currentTimeMs = uiState.currentTimeMs,
                    config = config,
                    onLineClick = onLineClick?.let { click -> { click(it, showingIndex) } }
                )
            }
        }

        // Shadow/depth effect
        if (rotation > 0f && rotation < 180f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .graphicsLayer {
                        alpha = (kotlin.math.sin(Math.toRadians(rotation.toDouble())) * 0.3).toFloat()
                        scaleX = 1f - (kotlin.math.sin(Math.toRadians(rotation.toDouble())) * 0.1).toFloat()
                        scaleY = 1f - (kotlin.math.sin(Math.toRadians(rotation.toDouble())) * 0.1).toFloat()
                    }
            )
        }
    }
}
