package com.karaokelyrics.ui.rendering.effects

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Factory for creating gradient effects for karaoke display.
 */
object GradientFactory {

    /**
     * Create a linear gradient brush based on angle.
     *
     * @param colors List of gradient colors
     * @param angle Angle in degrees (0 = left to right, 90 = top to bottom)
     * @param width Component width for calculating offsets
     * @param height Component height for calculating offsets
     */
    fun createLinearGradient(
        colors: List<Color>,
        angle: Float = 45f,
        width: Float = 1000f,
        height: Float = 100f
    ): Brush {
        val angleRad = angle * PI / 180
        val cos = cos(angleRad).toFloat()
        val sin = sin(angleRad).toFloat()

        // Calculate gradient endpoints based on angle
        val halfWidth = width / 2
        val halfHeight = height / 2

        return Brush.linearGradient(
            colors = colors,
            start = Offset(
                halfWidth - halfWidth * cos - halfHeight * sin,
                halfHeight - halfWidth * sin + halfHeight * cos
            ),
            end = Offset(
                halfWidth + halfWidth * cos + halfHeight * sin,
                halfHeight + halfWidth * sin - halfHeight * cos
            )
        )
    }

    /**
     * Create a progress-based gradient for syllable highlighting.
     *
     * @param progress Animation progress from 0 to 1
     * @param baseColor Base color for the text
     * @param highlightColor Color for the highlighted portion
     */
    fun createProgressGradient(
        progress: Float,
        baseColor: Color,
        highlightColor: Color,
        width: Float = 1000f
    ): Brush {
        if (progress <= 0f) {
            return Brush.linearGradient(
                colors = listOf(baseColor, baseColor),
                start = Offset.Zero,
                end = Offset(width, 0f)
            )
        }

        if (progress >= 1f) {
            return Brush.linearGradient(
                colors = listOf(highlightColor, highlightColor),
                start = Offset.Zero,
                end = Offset(width, 0f)
            )
        }

        val stopPosition = progress.coerceIn(0f, 1f)

        return Brush.linearGradient(
            colorStops = arrayOf(
                0f to highlightColor,
                stopPosition to highlightColor,
                stopPosition to baseColor,
                1f to baseColor
            ),
            start = Offset.Zero,
            end = Offset(width, 0f)
        )
    }

    /**
     * Create a shimmer gradient for loading or highlight effects.
     *
     * @param progress Shimmer animation progress
     * @param baseColor Base color
     * @param shimmerColor Shimmer highlight color
     * @param width Component width
     */
    fun createShimmerGradient(
        progress: Float,
        baseColor: Color,
        shimmerColor: Color,
        width: Float = 1000f
    ): Brush {
        val shimmerWidth = 0.3f // Width of shimmer band
        val position = progress.coerceIn(0f, 1f)

        return Brush.linearGradient(
            colorStops = arrayOf(
                0f to baseColor,
                (position - shimmerWidth).coerceAtLeast(0f) to baseColor,
                position to shimmerColor,
                (position + shimmerWidth).coerceAtMost(1f) to baseColor,
                1f to baseColor
            ),
            start = Offset.Zero,
            end = Offset(width, 0f)
        )
    }

    /**
     * Create a multi-color gradient for dramatic effects.
     *
     * @param colors List of colors to blend
     * @param angle Gradient angle in degrees
     * @param width Component width
     * @param height Component height
     */
    fun createMultiColorGradient(
        colors: List<Color>,
        angle: Float = 45f,
        width: Float = 1000f,
        height: Float = 100f
    ): Brush {
        if (colors.size < 2) {
            return Brush.linearGradient(
                colors = listOf(colors.firstOrNull() ?: Color.White, colors.firstOrNull() ?: Color.White)
            )
        }

        // Create smooth color stops
        val stops = colors.mapIndexed { index, color ->
            (index.toFloat() / (colors.size - 1)) to color
        }.toTypedArray()

        val angleRad = angle * PI / 180
        val cos = cos(angleRad).toFloat()
        val sin = sin(angleRad).toFloat()

        val halfWidth = width / 2
        val halfHeight = height / 2

        return Brush.linearGradient(
            colorStops = stops,
            start = Offset(
                halfWidth - halfWidth * cos - halfHeight * sin,
                halfHeight - halfWidth * sin + halfHeight * cos
            ),
            end = Offset(
                halfWidth + halfWidth * cos + halfHeight * sin,
                halfHeight + halfWidth * sin - halfHeight * cos
            )
        )
    }

    /**
     * Create presets for common gradient effects.
     */
    object Presets {
        val Rainbow = listOf(
            Color(0xFFFF0000),
            Color(0xFFFF7F00),
            Color(0xFFFFFF00),
            Color(0xFF00FF00),
            Color(0xFF0000FF),
            Color(0xFF4B0082),
            Color(0xFF9400D3)
        )

        val Sunset = listOf(
            Color(0xFFFF6B6B),
            Color(0xFFFFE66D),
            Color(0xFF4ECDC4)
        )

        val Ocean = listOf(
            Color(0xFF006BA6),
            Color(0xFF0496FF),
            Color(0xFF87CEEB)
        )

        val Fire = listOf(
            Color(0xFFFF0000),
            Color(0xFFFFA500),
            Color(0xFFFFFF00)
        )

        val Neon = listOf(
            Color(0xFF00FFF0),
            Color(0xFFFF00FF),
            Color(0xFFFFFF00)
        )
    }
}