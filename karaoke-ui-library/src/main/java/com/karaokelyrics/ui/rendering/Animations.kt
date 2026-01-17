package com.karaokelyrics.ui.rendering

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

/**
 * Standalone animation composables for karaoke effects.
 * These are pure composable functions that can be used independently.
 */

/**
 * Create a pulsing animation for active elements.
 *
 * @param enabled Whether the pulse animation is enabled
 * @param minScale Minimum scale value during pulse
 * @param maxScale Maximum scale value during pulse
 * @param duration Duration of one pulse cycle in milliseconds
 * @return Current scale value (1f if disabled)
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
