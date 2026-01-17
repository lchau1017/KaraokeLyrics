package com.karaokelyrics.app.presentation.ui.components.rendering

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.karaokelyrics.app.domain.model.karaoke.KaraokeSyllable
import com.karaokelyrics.app.presentation.ui.utils.SyllableLayout

/**
 * Factory for creating gradient brushes for karaoke effects
 */
object GradientBrushFactory {

    /**
     * Create a gradient brush for the karaoke progress effect
     */
    fun createKaraokeGradient(
        lineLayouts: List<SyllableLayout>,
        currentTimeMs: Int,
        isRtl: Boolean,
        activeColor: Color = Color.White,
        inactiveColor: Color = Color.White.copy(alpha = 0.3f)
    ): Brush {
        if (lineLayouts.isEmpty()) {
            return Brush.horizontalGradient(listOf(inactiveColor, inactiveColor))
        }

        val totalMinX = lineLayouts.minOf { it.position.x }
        val totalMaxX = lineLayouts.maxOf { it.position.x + it.width }
        val totalWidth = totalMaxX - totalMinX

        if (totalWidth <= 0f) {
            val isFinished = currentTimeMs >= lineLayouts.last().syllable.end
            val color = if (isFinished) activeColor else inactiveColor
            return Brush.horizontalGradient(listOf(color, color))
        }

        val firstSyllableStart = lineLayouts.first().syllable.start
        val lastSyllableEnd = lineLayouts.last().syllable.end

        // Before the line starts
        if (currentTimeMs < firstSyllableStart) {
            return Brush.horizontalGradient(listOf(inactiveColor, inactiveColor))
        }

        // After the line ends - return inactive (not active)
        if (currentTimeMs >= lastSyllableEnd) {
            return Brush.horizontalGradient(listOf(inactiveColor, inactiveColor))
        }

        // Find current position in the line
        val currentPixelPosition = calculateCurrentPixelPosition(
            lineLayouts, currentTimeMs, isRtl, totalMinX, totalMaxX
        )

        val progress = (currentPixelPosition - totalMinX) / totalWidth
        return createGradientWithTransition(
            progress, activeColor, inactiveColor, totalMinX, totalMaxX
        )
    }

    private fun calculateCurrentPixelPosition(
        lineLayouts: List<SyllableLayout>,
        currentTimeMs: Int,
        isRtl: Boolean,
        totalMinX: Float,
        totalMaxX: Float
    ): Float {
        // Find the currently active syllable
        val activeSyllableLayout = lineLayouts.find {
            currentTimeMs >= it.syllable.start && currentTimeMs < it.syllable.end
        }

        return when {
            activeSyllableLayout != null -> {
                // We're in a syllable - calculate position within it
                val syllableProgress = activeSyllableLayout.syllable.progress(currentTimeMs)
                if (isRtl) {
                    activeSyllableLayout.position.x + activeSyllableLayout.width * (1f - syllableProgress)
                } else {
                    activeSyllableLayout.position.x + activeSyllableLayout.width * syllableProgress
                }
            }
            else -> {
                // Between syllables - find the last completed one
                val lastFinished = lineLayouts.lastOrNull { currentTimeMs >= it.syllable.end }
                if (lastFinished != null) {
                    // Position at the end of the last finished syllable
                    if (isRtl) {
                        lastFinished.position.x
                    } else {
                        lastFinished.position.x + lastFinished.width
                    }
                } else {
                    // No syllable finished yet
                    if (isRtl) totalMaxX else totalMinX
                }
            }
        }
    }

    private fun createGradientWithTransition(
        progress: Float,
        activeColor: Color,
        inactiveColor: Color,
        startX: Float,
        endX: Float
    ): Brush {
        val fadeWidth = 0.02f // Sharp transition
        val fadeStart = (progress - fadeWidth).coerceAtLeast(0f)
        val fadeEnd = (progress + fadeWidth).coerceAtMost(1f)

        return Brush.horizontalGradient(
            colorStops = arrayOf(
                0f to activeColor,
                fadeStart to activeColor,
                progress to activeColor.copy(alpha = 0.8f),
                fadeEnd to inactiveColor,
                1f to inactiveColor
            ),
            startX = startX,
            endX = endX
        )
    }
}