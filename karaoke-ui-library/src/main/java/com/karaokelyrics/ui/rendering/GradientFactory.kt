package com.karaokelyrics.ui.rendering

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.karaokelyrics.ui.core.config.GradientPreset
import com.karaokelyrics.ui.core.config.GradientType
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Factory for creating gradient brushes used in karaoke text rendering.
 * All methods are pure functions with no side effects.
 */
object GradientFactory {

    /**
     * Create a gradient brush for a character based on configuration.
     */
    fun createCharacterGradient(
        charWidth: Float,
        charHeight: Float,
        charProgress: Float,
        config: KaraokeLibraryConfig,
        baseColor: Color
    ): Brush = when (config.visual.gradientType) {
        GradientType.PROGRESS -> {
            createProgressGradient(
                progress = charProgress,
                baseColor = baseColor,
                highlightColor = config.visual.colors.active,
                width = charWidth
            )
        }
        GradientType.MULTI_COLOR -> {
            val colors = config.visual.playingGradientColors.takeIf { it.size > 1 }
                ?: listOf(config.visual.colors.active, config.visual.colors.sung)
            createMultiColorGradient(
                colors = colors,
                angle = config.visual.gradientAngle,
                width = charWidth,
                height = charHeight
            )
        }
        GradientType.PRESET -> {
            val presetColors = getPresetColors(config.visual.gradientPreset)
                ?: listOf(config.visual.colors.active, config.visual.colors.sung)
            createMultiColorGradient(
                colors = presetColors,
                angle = config.visual.gradientAngle,
                width = charWidth,
                height = charHeight
            )
        }
        else -> {
            createLinearGradient(
                colors = listOf(config.visual.colors.active, config.visual.colors.sung),
                angle = config.visual.gradientAngle,
                width = charWidth,
                height = charHeight
            )
        }
    }

    /**
     * Create a linear gradient brush based on angle.
     */
    fun createLinearGradient(
        colors: List<Color>,
        angle: Float = 45f,
        width: Float = 1000f,
        height: Float = 100f
    ): Brush {
        val (start, end) = calculateGradientEndpoints(angle, width, height)
        return Brush.linearGradient(
            colors = colors,
            start = start,
            end = end
        )
    }

    /**
     * Create a progress-based gradient for karaoke highlighting effect.
     * Shows highlight color up to progress point, then base color.
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
     * Create a multi-color gradient with evenly distributed color stops.
     */
    fun createMultiColorGradient(
        colors: List<Color>,
        angle: Float = 45f,
        width: Float = 1000f,
        height: Float = 100f
    ): Brush {
        if (colors.size < 2) {
            val color = colors.firstOrNull() ?: Color.White
            return Brush.linearGradient(colors = listOf(color, color))
        }

        val stops = colors.mapIndexed { index, color ->
            (index.toFloat() / (colors.size - 1)) to color
        }.toTypedArray()

        val (start, end) = calculateGradientEndpoints(angle, width, height)
        return Brush.linearGradient(
            colorStops = stops,
            start = start,
            end = end
        )
    }

    /**
     * Get predefined gradient colors for a preset.
     */
    fun getPresetColors(preset: GradientPreset?): List<Color>? = when (preset) {
        GradientPreset.RAINBOW -> listOf(
            Color(0xFFFF0000),
            Color(0xFFFF7F00),
            Color(0xFFFFFF00),
            Color(0xFF00FF00),
            Color(0xFF0000FF),
            Color(0xFF4B0082),
            Color(0xFF9400D3)
        )
        GradientPreset.SUNSET -> listOf(
            Color(0xFFFF6B6B),
            Color(0xFFFFE66D),
            Color(0xFF4ECDC4)
        )
        GradientPreset.OCEAN -> listOf(
            Color(0xFF006BA6),
            Color(0xFF0496FF),
            Color(0xFF87CEEB)
        )
        GradientPreset.FIRE -> listOf(
            Color(0xFFFF0000),
            Color(0xFFFFA500),
            Color(0xFFFFFF00)
        )
        GradientPreset.NEON -> listOf(
            Color(0xFF00FFF0),
            Color(0xFFFF00FF),
            Color(0xFFFFFF00)
        )
        null -> null
    }

    /**
     * Calculate gradient start and end points based on angle.
     */
    private fun calculateGradientEndpoints(
        angle: Float,
        width: Float,
        height: Float
    ): Pair<Offset, Offset> {
        val angleRad = angle * PI / 180
        val cos = cos(angleRad).toFloat()
        val sin = sin(angleRad).toFloat()
        val halfWidth = width / 2
        val halfHeight = height / 2

        val start = Offset(
            halfWidth - halfWidth * cos - halfHeight * sin,
            halfHeight - halfWidth * sin + halfHeight * cos
        )
        val end = Offset(
            halfWidth + halfWidth * cos + halfHeight * sin,
            halfHeight + halfWidth * sin - halfHeight * cos
        )
        return start to end
    }
}
