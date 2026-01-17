package com.karaokelyrics.ui.rendering.character

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.KaraokeSyllable
import com.karaokelyrics.ui.rendering.AnimationManager
import com.karaokelyrics.ui.rendering.EffectsManager

/**
 * Handles the rendering of individual characters within syllables.
 * Manages character timing, animation, and effects.
 */
class CharacterRenderer {
    private val effectsManager = EffectsManager()

    fun renderSyllableCharacters(
        drawScope: DrawScope,
        syllable: KaraokeSyllable,
        xOffset: Float,
        yOffset: Float,
        currentTimeMs: Int,
        config: KaraokeLibraryConfig,
        textStyle: TextStyle,
        baseColor: Color,
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
                val charColor = effectsManager.calculateCharacterColor(
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
                        charLayout = charLayout,
                        charX = charX,
                        charY = yOffset,
                        charStartTime = charStartTime,
                        charEndTime = charEndTime,
                        currentTimeMs = currentTimeMs,
                        config = config,
                        charColor = charColor
                    )
                } else {
                    // Render without animation
                    renderStaticCharacter(
                        drawScope = this,
                        charLayout = charLayout,
                        charX = charX,
                        charY = yOffset,
                        charStartTime = charStartTime,
                        charEndTime = charEndTime,
                        currentTimeMs = currentTimeMs,
                        config = config,
                        charColor = charColor
                    )
                }

                charX += charLayout.size.width
            }
        }
    }

    private fun DrawScope.renderAnimatedCharacter(
        drawScope: DrawScope,
        charLayout: TextLayoutResult,
        charX: Float,
        charY: Float,
        charStartTime: Int,
        charEndTime: Int,
        currentTimeMs: Int,
        config: KaraokeLibraryConfig,
        charColor: Color
    ) {
        val animState = AnimationManager.calculateCharacterAnimation(
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
            animationState = animState
        )
    }

    private fun DrawScope.renderStaticCharacter(
        drawScope: DrawScope,
        charLayout: TextLayoutResult,
        charX: Float,
        charY: Float,
        charStartTime: Int,
        charEndTime: Int,
        currentTimeMs: Int,
        config: KaraokeLibraryConfig,
        charColor: Color
    ) {
        effectsManager.renderCharacterWithEffects(
            drawScope = drawScope,
            charLayout = charLayout,
            charX = charX,
            charY = charY,
            charColor = charColor,
            config = config,
            charProgress = calculateProgress(currentTimeMs, charStartTime, charEndTime)
        )
    }

    private fun calculateProgress(currentTimeMs: Int, startTime: Int, endTime: Int): Float {
        return if (endTime > startTime && currentTimeMs >= startTime) {
            ((currentTimeMs - startTime).toFloat() / (endTime - startTime))
                .coerceIn(0f, 1f)
        } else 0f
    }
}
