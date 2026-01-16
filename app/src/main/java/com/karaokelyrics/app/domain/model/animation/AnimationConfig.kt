package com.karaokelyrics.app.domain.model.animation

/**
 * Configuration for animations applied to lyrics.
 * This separates animation configuration from rendering logic.
 */
data class AnimationConfig(
    val type: AnimationType,
    val durationMs: Long,
    val delayMs: Long = 0,
    val easing: EasingFunction = EasingFunction.LINEAR
)

/**
 * Easing functions for animations.
 * Follows Open/Closed Principle - new easing functions can be added as enum values.
 */
enum class EasingFunction {
    LINEAR,
    EASE_IN,
    EASE_OUT,
    EASE_IN_OUT,
    SPRING,
    BOUNCE,
    ACCELERATE,
    DECELERATE
}

/**
 * Context information for determining animation behavior.
 */
data class AnimationContext(
    val syllableDurationMs: Long,
    val lineIndex: Int,
    val syllableIndex: Int,
    val totalSyllablesInLine: Int,
    val isBackgroundVocal: Boolean,
    val animationsEnabled: Boolean,
    val characterAnimationsEnabled: Boolean
)