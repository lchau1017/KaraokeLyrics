package com.karaokelyrics.ui.rendering.animation

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Calculates character-level animation values for karaoke display.
 * Provides timing and transformation calculations for individual characters.
 */
class CharacterAnimationCalculator {

    /**
     * Animation state for a single character.
     */
    data class CharacterAnimationState(
        val scale: Float = 1f,
        val offset: Offset = Offset.Zero,
        val rotation: Float = 0f,
        val opacity: Float = 1f,
        val blur: Float = 0f
    )

    /**
     * Calculate animation state for a character based on timing.
     *
     * @param characterStartTime Start time of the character in milliseconds
     * @param characterEndTime End time of the character in milliseconds
     * @param currentTime Current playback time in milliseconds
     * @param animationDuration Total duration of the animation effect
     * @param maxScale Maximum scale factor for the character
     * @param floatOffset Maximum vertical offset for floating effect
     * @param rotationDegrees Maximum rotation in degrees
     */
    fun calculateCharacterAnimation(
        characterStartTime: Int,
        characterEndTime: Int,
        currentTime: Int,
        animationDuration: Float = 800f,
        maxScale: Float = 1.15f,
        floatOffset: Float = 6f,
        rotationDegrees: Float = 3f
    ): CharacterAnimationState {
        if (currentTime < characterStartTime) {
            // Character hasn't started yet
            return CharacterAnimationState(opacity = 0.8f)
        }

        if (currentTime > characterEndTime) {
            // Character has finished
            return CharacterAnimationState(opacity = 0.6f, blur = 2f)
        }

        // Character is playing - calculate animation progress
        val elapsed = (currentTime - characterStartTime).toFloat()
        val progress = (elapsed / animationDuration).coerceIn(0f, 1f)

        // Use easing function for smooth animation
        val easedProgress = easeInOutCubic(progress)

        // Calculate scale with pulse effect
        val pulseProgress = (elapsed % 400f) / 400f
        val pulseScale = 1f + (0.05f * sin(pulseProgress * 2 * PI).toFloat())
        val scale = interpolate(1f, maxScale, easedProgress) * pulseScale

        // Calculate floating offset with wave motion
        val floatProgress = (elapsed % 600f) / 600f
        val yOffset = floatOffset * sin(floatProgress * 2 * PI).toFloat()

        // Calculate subtle rotation
        val rotationProgress = (elapsed % 800f) / 800f
        val rotation = rotationDegrees * sin(rotationProgress * 2 * PI).toFloat()

        return CharacterAnimationState(
            scale = scale,
            offset = Offset(0f, -yOffset),
            rotation = rotation,
            opacity = 1f,
            blur = 0f
        )
    }

    /**
     * Calculate animation state for an entire word.
     *
     * @param wordStartTime Start time of the word
     * @param wordEndTime End time of the word
     * @param currentTime Current playback time
     * @param characterIndex Index of the character within the word
     * @param totalCharacters Total number of characters in the word
     */
    fun calculateWordAnimation(
        wordStartTime: Long,
        wordEndTime: Long,
        currentTime: Long,
        characterIndex: Int,
        totalCharacters: Int,
        animationDuration: Float = 800f
    ): CharacterAnimationState {
        if (currentTime < wordStartTime) {
            return CharacterAnimationState(opacity = 0.8f)
        }

        if (currentTime > wordEndTime) {
            return CharacterAnimationState(opacity = 0.6f, blur = 2f)
        }

        val wordDuration = wordEndTime - wordStartTime
        val charDuration = wordDuration / totalCharacters.toFloat()
        val charStartTime = wordStartTime + (characterIndex * charDuration).toLong()
        val charEndTime = charStartTime + charDuration.toLong()

        return calculateCharacterAnimation(
            characterStartTime = charStartTime.toInt(),
            characterEndTime = charEndTime.toInt(),
            currentTime = currentTime.toInt(),
            animationDuration = animationDuration
        )
    }

    /**
     * Calculate stagger delay for cascade animations.
     *
     * @param index Character or syllable index
     * @param delayPerItem Delay between each item in milliseconds
     */
    fun calculateStaggerDelay(index: Int, delayPerItem: Float = 50f): Float {
        return index * delayPerItem
    }

    /**
     * Easing function for smooth animation curves.
     */
    private fun easeInOutCubic(t: Float): Float {
        return if (t < 0.5f) {
            4f * t * t * t
        } else {
            1f - (-2f * t + 2f).let { it * it * it } / 2f
        }
    }

    /**
     * Linear interpolation between two values.
     */
    private fun interpolate(start: Float, end: Float, progress: Float): Float {
        return start + (end - start) * progress
    }

    /**
     * Calculate wave motion for group animations.
     */
    fun calculateWaveMotion(
        index: Int,
        totalItems: Int,
        currentTime: Long,
        waveSpeed: Float = 1000f,
        waveAmplitude: Float = 10f
    ): Offset {
        val phase = (index.toFloat() / totalItems) * 2 * PI
        val timePhase = (currentTime % waveSpeed) / waveSpeed * 2 * PI

        val yOffset = waveAmplitude * sin(phase + timePhase).toFloat()
        val xOffset = (waveAmplitude * 0.3f) * cos(phase + timePhase).toFloat()

        return Offset(xOffset, yOffset)
    }
}