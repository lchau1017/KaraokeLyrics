package com.karaokelyrics.ui.rendering

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.sin

/**
 * Pure calculation functions for rendering karaoke effects.
 * Consolidates all time-based and state-based calculations.
 *
 * All functions are pure (no side effects) and can be easily unit tested.
 */
object RenderingCalculations {

    // ==================== Animation Calculations ====================

    /**
     * Animation state for character-level transformations.
     * Contains only the properties actually used for rendering.
     */
    @Stable
    data class CharacterAnimationState(val scale: Float = 1f, val offset: Offset = Offset.Zero, val rotation: Float = 0f) {
        companion object {
            val Default = CharacterAnimationState()
        }
    }

    /**
     * Calculate character animation state based on timing.
     * Pure function with no Compose dependencies.
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
        // Not yet playing
        if (currentTime < characterStartTime) {
            return CharacterAnimationState.Default
        }

        // Already played
        if (currentTime > characterEndTime) {
            return CharacterAnimationState.Default
        }

        // Character is playing - calculate animation
        val elapsed = (currentTime - characterStartTime).toFloat()
        val progress = (elapsed / animationDuration).coerceIn(0f, 1f)
        val easedProgress = easeInOutCubic(progress)

        // Scale with subtle pulse
        val pulseProgress = (elapsed % 400f) / 400f
        val pulseScale = 1f + (0.05f * sin(pulseProgress * 2 * PI).toFloat())
        val scale = lerp(1f, maxScale, easedProgress) * pulseScale

        // Floating offset with wave motion
        val floatProgress = (elapsed % 600f) / 600f
        val yOffset = floatOffset * sin(floatProgress * 2 * PI).toFloat()

        // Subtle rotation
        val rotationProgress = (elapsed % 800f) / 800f
        val rotation = rotationDegrees * sin(rotationProgress * 2 * PI).toFloat()

        return CharacterAnimationState(
            scale = scale,
            offset = Offset(0f, -yOffset),
            rotation = rotation
        )
    }

    /**
     * Calculate pulse scale based on current time.
     * Returns a scale value that oscillates between minScale and maxScale.
     */
    fun calculatePulseScale(currentTimeMs: Int, minScale: Float = 0.95f, maxScale: Float = 1.05f, duration: Int = 1000): Float {
        val progress = (currentTimeMs % duration).toFloat() / duration
        val sineValue = sin(progress * 2 * PI).toFloat()
        val normalizedSine = (sineValue + 1f) / 2f
        return minScale + (maxScale - minScale) * normalizedSine
    }

    // ==================== Color Calculations ====================

    /**
     * Calculate the color for a character based on its timing state.
     */
    fun calculateCharacterColor(
        currentTimeMs: Int,
        charStartTime: Int,
        charEndTime: Int,
        baseColor: Color,
        playingColor: Color,
        playedColor: Color
    ): Color = when {
        currentTimeMs > charEndTime -> playedColor
        currentTimeMs >= charStartTime -> {
            val progress = calculateProgress(currentTimeMs, charStartTime, charEndTime)
            lerpColor(baseColor, playingColor, progress)
        }
        else -> baseColor
    }

    /**
     * Calculate progress between start and end times (0.0 to 1.0).
     */
    fun calculateProgress(currentTime: Int, startTime: Int, endTime: Int): Float = if (endTime > startTime && currentTime >= startTime) {
        ((currentTime - startTime).toFloat() / (endTime - startTime))
            .coerceIn(0f, 1f)
    } else {
        0f
    }

    // ==================== Utility Functions ====================

    /**
     * Cubic ease-in-out function for smooth animations.
     */
    private fun easeInOutCubic(t: Float): Float = if (t < 0.5f) {
        4f * t * t * t
    } else {
        1f - (-2f * t + 2f).let { it * it * it } / 2f
    }

    /**
     * Linear interpolation between two float values.
     */
    fun lerp(start: Float, end: Float, fraction: Float): Float = start + (end - start) * fraction

    /**
     * Linear interpolation between two colors.
     */
    fun lerpColor(start: Color, end: Color, fraction: Float): Color = Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = start.alpha + (end.alpha - start.alpha) * fraction
    )
}
