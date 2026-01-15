package com.karaokelyrics.app.presentation.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Constants for karaoke UI components
 */
object KaraokeConstants {

    // Alpha values
    const val INACTIVE_TEXT_ALPHA = 0.3f
    const val SHADOW_ALPHA = 0.4f

    // Blur effects
    const val DEFAULT_BLUR_RADIUS = 20f  // Increased from 8f for more visible blur

    // Layout
    val HORIZONTAL_PADDING = 24.dp
    val VERTICAL_PADDING = 8.dp
    const val LINE_HEIGHT_MULTIPLIER = 1.5f

    // Animation
    const val CHARACTER_FLOAT_OFFSET = 4f
    const val CHARACTER_SCALE_FACTOR = 0.1f
    const val BLUR_ANIMATION_FACTOR = 10f

    // Timing
    const val ANIMATION_CLEANUP_INTERVAL = 5000 // Every 5 seconds
}