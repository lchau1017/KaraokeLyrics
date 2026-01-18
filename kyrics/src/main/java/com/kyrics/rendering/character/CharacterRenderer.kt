package com.kyrics.rendering.character

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.kyrics.config.KyricsConfig
import com.kyrics.models.KyricsSyllable
import com.kyrics.rendering.EffectsManager
import com.kyrics.rendering.RenderingCalculations

/**
 * Handles the rendering of individual characters within syllables.
 * Uses RenderingCalculations for state calculations and EffectsManager for Canvas rendering.
 *
 * This is an object (singleton) as it has no mutable state.
 */
object CharacterRenderer {

    @Suppress("LongParameterList")
    fun renderSyllableCharacters(
        drawScope: DrawScope,
        syllable: KyricsSyllable,
        xOffset: Float,
        yOffset: Float,
        currentTimeMs: Int,
        config: KyricsConfig,
        textStyle: TextStyle,
        baseColor: Color,
        textMeasurer: TextMeasurer
    ) {
        val syllableDuration = syllable.end - syllable.start
        val charCount = syllable.content.length
        val charDuration = if (charCount > 0) syllableDuration.toFloat() / charCount else 0f

        var charX = xOffset

        syllable.content.forEachIndexed { charIndex, char ->
            val charStartTime = syllable.start + (charIndex * charDuration).toInt()
            val charEndTime = syllable.start + ((charIndex + 1) * charDuration).toInt()

            // Calculate character color using RenderingCalculations
            val charColor = RenderingCalculations.calculateCharacterColor(
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

            // Calculate progress for gradient effects
            val charProgress = RenderingCalculations.calculateProgress(
                currentTime = currentTimeMs,
                startTime = charStartTime,
                endTime = charEndTime
            )

            // Determine if character should be animated
            val isCharActive = currentTimeMs >= charStartTime &&
                currentTimeMs <= (charEndTime + config.animation.characterAnimationDuration.toInt())

            // Calculate animation state if needed
            val animationState = if (config.animation.enableCharacterAnimations && isCharActive) {
                RenderingCalculations.calculateCharacterAnimation(
                    characterStartTime = charStartTime,
                    characterEndTime = charEndTime,
                    currentTime = currentTimeMs,
                    animationDuration = config.animation.characterAnimationDuration,
                    maxScale = config.animation.characterMaxScale,
                    floatOffset = config.animation.characterFloatOffset,
                    rotationDegrees = config.animation.characterRotationDegrees
                )
            } else {
                null
            }

            // Render character with effects
            EffectsManager.renderCharacterWithEffects(
                drawScope = drawScope,
                charLayout = charLayout,
                charX = charX,
                charY = yOffset,
                charColor = charColor,
                config = config,
                charProgress = charProgress,
                animationState = animationState
            )

            charX += charLayout.size.width
        }
    }
}
