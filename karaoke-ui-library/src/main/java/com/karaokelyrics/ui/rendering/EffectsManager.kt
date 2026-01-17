package com.karaokelyrics.ui.rendering

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig

/**
 * Handles Canvas-based rendering of characters with effects.
 * Delegates calculations to RenderingCalculations and gradients to GradientFactory.
 *
 * This is an object (singleton) as it has no mutable state.
 */
object EffectsManager {

    /**
     * Render a character with all configured effects (animation, gradient).
     *
     * @param drawScope The Canvas draw scope
     * @param charLayout Measured character layout
     * @param charX X position to draw at
     * @param charY Y position to draw at
     * @param charColor Base color for the character
     * @param config Library configuration
     * @param charProgress Progress of character animation (0.0 to 1.0)
     * @param animationState Optional animation transformations (scale, rotation, offset)
     */
    fun renderCharacterWithEffects(
        drawScope: DrawScope,
        charLayout: TextLayoutResult,
        charX: Float,
        charY: Float,
        charColor: Color,
        config: KaraokeLibraryConfig,
        charProgress: Float,
        animationState: RenderingCalculations.CharacterAnimationState? = null
    ) {
        with(drawScope) {
            if (animationState != null && (animationState.scale != 1f || animationState.rotation != 0f)) {
                val pivotX = charX + charLayout.size.width / 2f
                val pivotY = charY + charLayout.size.height / 2f

                drawIntoCanvas {
                    scale(scale = animationState.scale, pivot = Offset(pivotX, pivotY)) {
                        rotate(degrees = animationState.rotation, pivot = Offset(pivotX, pivotY)) {
                            drawCharacter(
                                drawScope = this,
                                charLayout = charLayout,
                                charX = charX + animationState.offset.x,
                                charY = charY + animationState.offset.y,
                                charColor = charColor,
                                config = config,
                                charProgress = charProgress
                            )
                        }
                    }
                }
            } else {
                drawCharacter(
                    drawScope = this,
                    charLayout = charLayout,
                    charX = charX,
                    charY = charY,
                    charColor = charColor,
                    config = config,
                    charProgress = charProgress
                )
            }
        }
    }

    /**
     * Draw a single character with gradient or solid color.
     */
    private fun drawCharacter(
        drawScope: DrawScope,
        charLayout: TextLayoutResult,
        charX: Float,
        charY: Float,
        charColor: Color,
        config: KaraokeLibraryConfig,
        charProgress: Float
    ) {
        with(drawScope) {
            if (config.visual.gradientEnabled && charProgress > 0f) {
                val gradient = GradientFactory.createCharacterGradient(
                    charWidth = charLayout.size.width.toFloat(),
                    charHeight = charLayout.size.height.toFloat(),
                    charProgress = charProgress,
                    config = config,
                    baseColor = charColor
                )
                drawText(
                    textLayoutResult = charLayout,
                    brush = gradient,
                    topLeft = Offset(charX, charY)
                )
            } else {
                drawText(
                    textLayoutResult = charLayout,
                    color = charColor,
                    topLeft = Offset(charX, charY)
                )
            }
        }
    }
}
