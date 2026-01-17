package com.karaokelyrics.ui.components.viewers

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.karaokelyrics.ui.components.KaraokeSingleLine
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.utils.LineStateUtils
import kotlin.math.cos
import kotlin.math.sin

/**
 * Spiral viewer with lines arranged in a spiral pattern.
 * Active line at center with played lines spiraling outward.
 */
@Composable
internal fun SpiralViewer(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
) {
    val currentLineIndex = remember(currentTimeMs, lines) {
        LineStateUtils.getCurrentLineIndex(lines, currentTimeMs)
    } ?: 0

    val density = LocalDensity.current

    // Animate spiral rotation
    val infiniteTransition = rememberInfiniteTransition(label = "spiral")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        lines.forEachIndexed { index, line ->
            val distance = index - currentLineIndex

            // Show lines within range
            if (kotlin.math.abs(distance) <= 4) {
                val lineState = remember(line, currentTimeMs) {
                    LineStateUtils.getLineState(line, currentTimeMs)
                }

                // Calculate spiral position
                val angle = Math.toRadians((rotation + distance * 72).toDouble())
                val radius = kotlin.math.abs(distance) * 80f + if (lineState.isPlaying) 0f else 50f
                val spiralX = (cos(angle) * radius).toFloat()
                val spiralY = (sin(angle) * radius).toFloat()

                // Calculate opacity and scale
                val opacity = when {
                    lineState.isPlaying -> 1f
                    kotlin.math.abs(distance) == 1 -> 0.5f
                    kotlin.math.abs(distance) == 2 -> 0.3f
                    else -> 0.15f
                }

                val scale = if (lineState.isPlaying) 1f else (1f - kotlin.math.abs(distance) * 0.15f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .graphicsLayer {
                            translationX = spiralX
                            translationY = spiralY
                            scaleX = scale
                            scaleY = scale
                            alpha = opacity
                            rotationZ = if (lineState.isPlaying) 0f else (distance * 15f)
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