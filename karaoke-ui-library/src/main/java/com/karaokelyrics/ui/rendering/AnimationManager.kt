package com.karaokelyrics.ui.rendering

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Unified animation manager for all karaoke animation effects.
 * Consolidates line-level and character-level animations.
 */
class AnimationManager {

    /**
     * Combined animation state for both line and character levels.
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
     * Animate a line based on timing with all effects combined.
     */
    @Composable
    fun animateLine(
        lineStartTime: Int,
        lineEndTime: Int,
        currentTime: Int,
        scaleOnPlay: Float = 1.05f,
        animationDuration: Int = 700
    ): AnimationState {
        var state by remember { mutableStateOf(AnimationState()) }

        val scale by animateFloatAsState(
            targetValue = when {
                currentTime in lineStartTime..lineEndTime -> scaleOnPlay
                else -> 1f
            },
            animationSpec = tween(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing
            ),
            label = "lineScale"
        )

        val opacity by animateFloatAsState(
            targetValue = when {
                currentTime < lineStartTime - 3000 -> 0f
                currentTime < lineStartTime - 1000 -> 0.3f
                currentTime < lineStartTime -> 0.6f
                currentTime in lineStartTime..lineEndTime -> 1f
                currentTime > lineEndTime + 2000 -> 0.1f
                else -> 0.25f
            },
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            ),
            label = "lineOpacity"
        )

        val blur by animateFloatAsState(
            targetValue = when {
                currentTime < lineStartTime - 2000 -> 5f
                currentTime < lineStartTime -> 3f
                currentTime in lineStartTime..lineEndTime -> 0f
                else -> 2f
            },
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            ),
            label = "lineBlur"
        )

        LaunchedEffect(currentTime, lineStartTime, lineEndTime) {
            state = AnimationState(
                scale = scale,
                opacity = opacity,
                blur = blur,
                isPlaying = currentTime in lineStartTime..lineEndTime,
                isUpcoming = currentTime < lineStartTime,
                hasPlayed = currentTime > lineEndTime
            )
        }

        return state
    }

    /**
     * Calculate character animation state based on timing.
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
     * Create a pulsing animation for active elements.
     */
    @Composable
    fun animatePulse(
        enabled: Boolean,
        minScale: Float = 0.95f,
        maxScale: Float = 1.05f,
        duration: Int = 1000
    ): Float {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")

        val scale by infiniteTransition.animateFloat(
            initialValue = minScale,
            targetValue = maxScale,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )

        return if (enabled) scale else 1f
    }

    /**
     * Create a shimmer effect animation.
     */
    @Composable
    fun animateShimmer(
        enabled: Boolean,
        duration: Int = 2000
    ): Float {
        val infiniteTransition = rememberInfiniteTransition(label = "shimmer")

        val shimmerProgress by infiniteTransition.animateFloat(
            initialValue = -1f,
            targetValue = 2f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmerProgress"
        )

        return if (enabled) shimmerProgress else 0f
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