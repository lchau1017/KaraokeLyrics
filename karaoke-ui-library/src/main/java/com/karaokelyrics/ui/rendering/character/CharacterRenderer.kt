package com.karaokelyrics.ui.rendering.character

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.KaraokeSyllable
import com.karaokelyrics.ui.rendering.AnimationManager
import com.karaokelyrics.ui.rendering.EffectsManager
import com.karaokelyrics.ui.rendering.color.ColorCalculator

/**
 * Handles the rendering of individual characters within syllables.
 * Manages character timing, animation, and effects.
 */
class CharacterRenderer {
    private val animationManager = AnimationManager()
    private val effectsManager = EffectsManager()
    private val colorCalculator = ColorCalculator()

    fun renderSyllableCharacters(
        drawScope: DrawScope,
        syllable: KaraokeSyllable,
        xOffset: Float,
        yOffset: Float,
        currentTimeMs: Int,
        config: KaraokeLibraryConfig,
        textStyle: TextStyle,
        baseColor: Color,
        shimmerProgress: Float,
        textMeasurer: TextMeasurer
    ) {
        with(drawScope) {
            val syllableDuration = syllable.end - syllable.start
            val charCount = syllable.content.length
            val charDuration = if (charCount > 0) syllableDuration.toFloat() / charCount else 0f

            var charX = xOffset

            syllable.content.forEachIndexed { charIndex, char ->
                val charStartTime = syllable.start + (charIndex * charDuration).toInt()
                val charEndTime = syllable.start + ((charIndex + 1) * charDuration).toInt()

                // Calculate character state and color
                val charColor = colorCalculator.calculateCharacterColor(
                    currentTimeMs = currentTimeMs,
                    charStartTime = charStartTime,
                    charEndTime = charEndTime,
                    baseColor = baseColor,
                    playingColor = config.visual.playingTextColor,
                    playedColor = config.visual.playedTextColor
                )

                // Measure character
                val charText = char.toString()
                val charLayout = textMeasurer.measure(charText, textStyle)

                // Determine if character should be animated
                val isCharActive = currentTimeMs >= charStartTime &&
                    currentTimeMs <= (charEndTime + config.animation.characterAnimationDuration.toInt())

                if (config.animation.enableCharacterAnimations && isCharActive) {
                    // Render with animation
                    renderAnimatedCharacter(
                        drawScope = this,
                        char = charText,
                        charLayout = charLayout,
                        charX = charX,
                        charY = yOffset,
                        charStartTime = charStartTime,
                        charEndTime = charEndTime,
                        currentTimeMs = currentTimeMs,
                        config = config,
                        charColor = charColor,
                        shimmerProgress = shimmerProgress
                    )
                } else {
                    // Render without animation
                    renderStaticCharacter(
                        drawScope = this,
                        char = charText,
                        charLayout = charLayout,
                        charX = charX,
                        charY = yOffset,
                        charStartTime = charStartTime,
                        charEndTime = charEndTime,
                        currentTimeMs = currentTimeMs,
                        config = config,
                        charColor = charColor,
                        baseColor = baseColor,
                        shimmerProgress = shimmerProgress
                    )
                }

                charX += charLayout.size.width
            }
        }
    }

    private fun DrawScope.renderAnimatedCharacter(
        drawScope: DrawScope,
        char: String,
        charLayout: androidx.compose.ui.text.TextLayoutResult,
        charX: Float,
        charY: Float,
        charStartTime: Int,
        charEndTime: Int,
        currentTimeMs: Int,
        config: KaraokeLibraryConfig,
        charColor: Color,
        shimmerProgress: Float
    ) {
        val animState = animationManager.calculateCharacterAnimation(
            characterStartTime = charStartTime,
            characterEndTime = charEndTime,
            currentTime = currentTimeMs,
            animationDuration = config.animation.characterAnimationDuration,
            maxScale = config.animation.characterMaxScale,
            floatOffset = config.animation.characterFloatOffset,
            rotationDegrees = config.animation.characterRotationDegrees
        )

        // Apply effects and draw character with animation
        effectsManager.renderCharacterWithEffects(
            drawScope = drawScope,
            charLayout = charLayout,
            charX = charX,
            charY = charY,
            charColor = charColor,
            config = config,
            charProgress = calculateProgress(currentTimeMs, charStartTime, charEndTime),
            shimmerProgress = shimmerProgress,
            animationState = animState
        )
    }

    private fun DrawScope.renderStaticCharacter(
        drawScope: DrawScope,
        char: String,
        charLayout: androidx.compose.ui.text.TextLayoutResult,
        charX: Float,
        charY: Float,
        charStartTime: Int,
        charEndTime: Int,
        currentTimeMs: Int,
        config: KaraokeLibraryConfig,
        charColor: Color,
        baseColor: Color,
        shimmerProgress: Float
    ) {
        effectsManager.renderCharacterWithEffects(
            drawScope = drawScope,
            charLayout = charLayout,
            charX = charX,
            charY = charY,
            charColor = charColor,
            config = config,
            charProgress = calculateProgress(currentTimeMs, charStartTime, charEndTime),
            shimmerProgress = shimmerProgress
        )
    }

    private fun calculateProgress(currentTimeMs: Int, startTime: Int, endTime: Int): Float {
        return if (endTime > startTime && currentTimeMs >= startTime) {
            ((currentTimeMs - startTime).toFloat() / (endTime - startTime))
                .coerceIn(0f, 1f)
        } else 0f
    }
}