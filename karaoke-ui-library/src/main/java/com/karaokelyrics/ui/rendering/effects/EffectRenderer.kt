package com.karaokelyrics.ui.rendering.effects

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import com.karaokelyrics.ui.core.config.GradientPreset
import com.karaokelyrics.ui.core.config.GradientType
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig

/**
 * Handles rendering of visual effects for characters.
 * Includes shadows, glows, gradients, and shimmer effects.
 */
class EffectRenderer {

    fun renderCharacterWithEffects(
        drawScope: DrawScope,
        charLayout: TextLayoutResult,
        charX: Float,
        charY: Float,
        charColor: Color,
        config: KaraokeLibraryConfig,
        charProgress: Float,
        shimmerProgress: Float
    ) {
        with(drawScope) {
            // 1. Draw shadow layer if enabled
            if (config.visual.shadowEnabled) {
                renderShadow(
                    charLayout = charLayout,
                    charX = charX,
                    charY = charY,
                    shadowColor = config.visual.shadowColor,
                    shadowOffset = config.visual.shadowOffset
                )
            }

            // 2. Draw glow layers if enabled
            if (config.visual.glowEnabled && charProgress > 0f) {
                renderGlow(
                    charLayout = charLayout,
                    charX = charX,
                    charY = charY,
                    glowColor = config.visual.glowColor
                )
            }

            // 3. Draw main character with appropriate effect
            when {
                config.animation.enableShimmer && shimmerProgress > 0f -> {
                    renderWithShimmer(
                        charLayout = charLayout,
                        charX = charX,
                        charY = charY,
                        charColor = charColor,
                        shimmerProgress = shimmerProgress
                    )
                }
                config.visual.gradientEnabled && charProgress > 0f -> {
                    renderWithGradient(
                        charLayout = charLayout,
                        charX = charX,
                        charY = charY,
                        charColor = charColor,
                        charProgress = charProgress,
                        config = config
                    )
                }
                else -> {
                    // Plain text rendering
                    drawText(
                        textLayoutResult = charLayout,
                        color = charColor,
                        topLeft = Offset(charX, charY)
                    )
                }
            }
        }
    }

    private fun DrawScope.renderShadow(
        charLayout: TextLayoutResult,
        charX: Float,
        charY: Float,
        shadowColor: Color,
        shadowOffset: Offset
    ) {
        drawText(
            textLayoutResult = charLayout,
            color = shadowColor.copy(alpha = 0.3f),
            topLeft = Offset(
                charX + shadowOffset.x,
                charY + shadowOffset.y
            )
        )
    }

    private fun DrawScope.renderGlow(
        charLayout: TextLayoutResult,
        charX: Float,
        charY: Float,
        glowColor: Color
    ) {
        // Outer glow layers
        listOf(
            Offset(-2f, -2f) to 0.2f,
            Offset(2f, 2f) to 0.2f,
            Offset(-1f, -1f) to 0.3f,
            Offset(1f, 1f) to 0.3f
        ).forEach { (offset, alpha) ->
            drawText(
                textLayoutResult = charLayout,
                color = glowColor.copy(alpha = alpha),
                topLeft = Offset(charX + offset.x, charY + offset.y)
            )
        }
    }

    private fun DrawScope.renderWithShimmer(
        charLayout: TextLayoutResult,
        charX: Float,
        charY: Float,
        charColor: Color,
        shimmerProgress: Float
    ) {
        val shimmerGradient = GradientFactory.createShimmerGradient(
            progress = (charX / size.width + shimmerProgress) % 1f,
            baseColor = charColor,
            shimmerColor = Color(
                red = minOf(1f, charColor.red + 0.3f),
                green = minOf(1f, charColor.green + 0.3f),
                blue = minOf(1f, charColor.blue + 0.3f),
                alpha = charColor.alpha
            ),
            width = charLayout.size.width.toFloat()
        )

        drawText(
            textLayoutResult = charLayout,
            brush = shimmerGradient,
            topLeft = Offset(charX, charY)
        )
    }

    private fun DrawScope.renderWithGradient(
        charLayout: TextLayoutResult,
        charX: Float,
        charY: Float,
        charColor: Color,
        charProgress: Float,
        config: KaraokeLibraryConfig
    ) {
        val gradient = createGradientBrush(
            charLayout = charLayout,
            charProgress = charProgress,
            config = config,
            baseColor = charColor
        )

        drawText(
            textLayoutResult = charLayout,
            brush = gradient,
            topLeft = Offset(charX, charY)
        )
    }

    private fun createGradientBrush(
        charLayout: TextLayoutResult,
        charProgress: Float,
        config: KaraokeLibraryConfig,
        baseColor: Color
    ) = when (config.visual.gradientType) {
        GradientType.PROGRESS -> {
            GradientFactory.createProgressGradient(
                progress = charProgress,
                baseColor = baseColor,
                highlightColor = config.visual.colors.active,
                width = charLayout.size.width.toFloat()
            )
        }
        GradientType.MULTI_COLOR -> {
            val colors = config.visual.playingGradientColors.takeIf { it.size > 1 }
                ?: listOf(config.visual.colors.active, config.visual.colors.sung)
            GradientFactory.createMultiColorGradient(
                colors = colors,
                angle = config.visual.gradientAngle,
                width = charLayout.size.width.toFloat(),
                height = charLayout.size.height.toFloat()
            )
        }
        GradientType.PRESET -> {
            val presetColors = when (config.visual.gradientPreset) {
                GradientPreset.RAINBOW -> GradientFactory.Presets.Rainbow
                GradientPreset.SUNSET -> GradientFactory.Presets.Sunset
                GradientPreset.OCEAN -> GradientFactory.Presets.Ocean
                GradientPreset.FIRE -> GradientFactory.Presets.Fire
                GradientPreset.NEON -> GradientFactory.Presets.Neon
                null -> listOf(config.visual.colors.active, config.visual.colors.sung)
            }
            GradientFactory.createMultiColorGradient(
                colors = presetColors,
                angle = config.visual.gradientAngle,
                width = charLayout.size.width.toFloat(),
                height = charLayout.size.height.toFloat()
            )
        }
        else -> {
            GradientFactory.createLinearGradient(
                colors = listOf(config.visual.colors.active, config.visual.colors.sung),
                angle = config.visual.gradientAngle,
                width = charLayout.size.width.toFloat(),
                height = charLayout.size.height.toFloat()
            )
        }
    }
}