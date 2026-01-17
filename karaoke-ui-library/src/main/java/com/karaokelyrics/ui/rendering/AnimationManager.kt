package com.karaokelyrics.ui.rendering

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.sin

/**
 * Animation calculation utilities for karaoke effects.
 * Contains pure functions for calculating animation states based on timing.
 */
object AnimationManager {

    /**
     * Combined animation state for character-level animations.
     */
    @Stable
    data class AnimationState(
        val scale: Float = 1f,
        val opacity: Float = 1f,
        val blur: Float = 0f,
        val offset: Offset = Offset.Zero,
        val rotation: Float = 0f,
        val isPlaying: Boolean = false,
        val isUpcoming: Boolean = false,
        val hasPlayed: Boolean = false
    )

    /**
     * Calculate character animation state based on timing.
     * This is a pure function that calculates the animation state without any Compose state.
     */
    fun calculateCharacterAnimation(
        characterStartTime: Int,
        characterEndTime: Int,
        currentTime: Int,
        animationDuration: Float = 800f,
        maxScale: Float = 1.15f,
        floatOffset: Float = 6f,
        rotationDegrees: Float = 3f
    ): AnimationState {
        if (currentTime < characterStartTime) {
            return AnimationState(opacity = 0.8f)
        }

        if (currentTime > characterEndTime) {
            return AnimationState(opacity = 0.6f, blur = 2f)
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

        return AnimationState(
            scale = scale,
            offset = Offset(0f, -yOffset),
            rotation = rotation,
            opacity = 1f,
            blur = 0f,
            isPlaying = true
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

    /**
     * Calculate pulse scale based on current time.
     * Returns a scale value that oscillates between minScale and maxScale.
     *
     * @param currentTimeMs Current playback time in milliseconds
     * @param minScale Minimum scale value during pulse
     * @param maxScale Maximum scale value during pulse
     * @param duration Duration of one complete pulse cycle in milliseconds
     * @return Current scale value oscillating between minScale and maxScale
     */
    fun calculatePulseScale(
        currentTimeMs: Int,
        minScale: Float = 0.95f,
        maxScale: Float = 1.05f,
        duration: Int = 1000
    ): Float {
        val progress = (currentTimeMs % duration).toFloat() / duration
        // Use sine wave for smooth oscillation (0 to 1 to 0)
        val sineValue = sin(progress * 2 * PI).toFloat()
        // Map from [-1, 1] to [0, 1]
        val normalizedSine = (sineValue + 1f) / 2f
        // Interpolate between min and max scale
        return minScale + (maxScale - minScale) * normalizedSine
    }
}
