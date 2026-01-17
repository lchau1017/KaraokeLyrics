package com.karaokelyrics.ui.components.viewers

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.karaokelyrics.ui.components.KaraokeSingleLine
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.utils.LineStateUtils
import kotlin.math.cos
import kotlin.math.sin

/**
 * 3D carousel viewer with cylindrical arrangement of lines.
 * Rotates to bring active line to front.
 */
@Composable
internal fun Carousel3DViewer(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
) {
    val currentLineIndex = remember(currentTimeMs, lines) {
        LineStateUtils.getCurrentLineIndex(lines, currentTimeMs)
    } ?: 0

    // Animate rotation to current line
    val targetRotation = currentLineIndex * (360f / maxOf(lines.size, 1))
    val animatedRotation by animateFloatAsState(
        targetValue = -targetRotation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "carouselRotation"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val visibleRange = 5 // Show 5 lines around current
        val radius = 300f // Carousel radius

        lines.forEachIndexed { index, line ->
            val distance = index - currentLineIndex

            if (kotlin.math.abs(distance) <= visibleRange) {
                val lineState = remember(line, currentTimeMs) {
                    LineStateUtils.getLineState(line, currentTimeMs)
                }

                // Calculate position in 3D space
                val itemAngle = (index * 360f / lines.size) + animatedRotation
                val radians = Math.toRadians(itemAngle.toDouble())
                val x = (sin(radians) * radius).toFloat()
                val z = (cos(radians) * radius).toFloat()

                // Calculate opacity based on z position (front = 1, back = 0)
                val normalizedZ = (z + radius) / (2 * radius)
                val opacity = when {
                    lineState.isPlaying -> 1f
                    normalizedZ > 0.7f -> normalizedZ * 0.8f
                    else -> normalizedZ * 0.3f
                }

                // Scale based on z position (perspective)
                val scale = 0.5f + (normalizedZ * 0.5f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .graphicsLayer {
                            translationX = x
                            translationY = 0f
                            scaleX = scale
                            scaleY = scale
                            alpha = opacity
                            // Simulate depth with camera distance
                            cameraDistance = 12f * density
                            rotationY = -itemAngle / 4 // Slight tilt for 3D effect
                        }
                        .zIndex(z)
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