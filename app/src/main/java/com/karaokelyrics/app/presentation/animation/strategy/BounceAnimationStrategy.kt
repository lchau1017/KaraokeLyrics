package com.karaokelyrics.app.presentation.animation.strategy

import androidx.compose.ui.geometry.Offset
import com.karaokelyrics.app.domain.model.animation.AnimationContext
import kotlin.math.abs
import kotlin.math.sin
import javax.inject.Inject

/**
 * Animation strategy that creates a bouncing effect.
 * Single Responsibility: Only calculates bounce animation.
 */
class BounceAnimationStrategy @Inject constructor() : AnimationStrategy {

    override val name = "Bounce"

    override fun calculate(
        progress: Float,
        context: AnimationContext
    ): AnimationState {
        // Calculate bounce using damped sine wave
        val bounceHeight = calculateBounceHeight(progress)
        val offset = Offset(0f, -bounceHeight * 20f) // 20 pixels max bounce

        return AnimationState(
            offset = offset,
            scale = 1f + (bounceHeight * 0.1f) // Slight scale during bounce
        )
    }

    private fun calculateBounceHeight(progress: Float): Float {
        // Damped sine wave for natural bounce
        val frequency = 4f // Number of bounces
        val damping = 1f - progress // Reduces bounce over time
        return abs(sin(progress * Math.PI * frequency).toFloat()) * damping
    }
}