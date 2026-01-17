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

    // Pulse Animation (for active lines)
    val enablePulse: Boolean = false,
    val pulseMinScale: Float = 0.98f,
    val pulseMaxScale: Float = 1.02f,
    val pulseDuration: Int = 1500,

    // Shimmer Animation (for highlighting effects)
    val enableShimmer: Boolean = false,
    val shimmerDuration: Int = 2000,
    val shimmerIntensity: Float = 0.3f,

    // Color Transition Animation
    val enableColorTransition: Boolean = true,
    val colorTransitionDuration: Int = 300,

    // Orchestrated Animations (for staggered effects)
    val enableOrchestratedAnimations: Boolean = false,
    val orchestrationDelayPerItem: Long = 50L,

    // Transition Animations
    val fadeInDuration: Float = 300f,
    val fadeOutDuration: Float = 500f,

    // Easing
    val animationEasing: Easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f) // FastOutSlowIn
)