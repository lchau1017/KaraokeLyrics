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

}