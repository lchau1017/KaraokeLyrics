package com.karaokelyrics.ui.components.viewers

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.karaokelyrics.ui.components.KaraokeSingleLine
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.state.KaraokeUiState
import kotlin.math.sin

/**
 * Wave flow viewer with sinusoidal motion pattern.
 * Lines flow in a wave pattern with the active line at the peak.
 */
@Composable
internal fun WaveFlowViewer(uiState: KaraokeUiState, config: KaraokeLibraryConfig, onLineClick: ((ISyncedLine, Int) -> Unit)? = null) {
    val currentLineIndex = uiState.currentLineIndex ?: 0
    val density = LocalDensity.current

    // Animate wave motion
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        uiState.lines.forEachIndexed { index, line ->
            val distance = index - currentLineIndex
            val lineUiState = uiState.getLineState(index)

            // Show lines within range
            if (kotlin.math.abs(distance) <= config.effects.visibleLineRange) {
                // Calculate wave position
                val wavePosition = distance * 60f
                val waveHeight = sin(Math.toRadians((waveOffset + distance * 45).toDouble())).toFloat() * 30f

                // Calculate opacity based on distance
                val opacity = when {
                    lineUiState.isPlaying -> 1f
                    kotlin.math.abs(distance) == 1 -> 0.6f
                    kotlin.math.abs(distance) == 2 -> 0.4f
                    else -> 0.2f
                }

                // Scale based on position in wave
                val scale = if (lineUiState.isPlaying) 1.1f else (1f - kotlin.math.abs(distance) * 0.1f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            translationX = wavePosition
                            translationY = with(density) { (distance * 80).dp.toPx() } + waveHeight
                            scaleX = scale
                            scaleY = scale
                            alpha = opacity
                        }
                ) {
                    KaraokeSingleLine(
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
