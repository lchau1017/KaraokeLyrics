package com.karaokelyrics.ui.core.config

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

/**
 * Animation configuration for the karaoke display.
 * Controls all animation behaviors and timings.
 */
data class AnimationConfig(
    // Character Animations
    val enableCharacterAnimations: Boolean = true,
    val characterAnimationDuration: Float = 800f,
    val characterMaxScale: Float = 1.15f,
    val characterFloatOffset: Float = 6f,
    val characterRotationDegrees: Float = 3f,

    // Line Animations
    val enableLineAnimations: Boolean = true,
    val lineScaleOnPlay: Float = 1.05f,
    val lineAnimationDuration: Float = 700f,

    // Transition Animations
    val fadeInDuration: Float = 300f,
    val fadeOutDuration: Float = 500f,

    // Easing
    val animationEasing: Easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f) // FastOutSlowIn
)