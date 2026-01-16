package com.karaokelyrics.app.presentation.animation.strategy

import com.karaokelyrics.app.domain.model.animation.AnimationContext
import kotlin.math.sin
import javax.inject.Inject

/**
 * Animation strategy that creates a swelling effect.
 * Single Responsibility: Only calculates swell animation.
 */
class SwellAnimationStrategy @Inject constructor() : AnimationStrategy {

    override val name = "Swell"

    override fun calculate(
        progress: Float,
        context: AnimationContext
    ): AnimationState {
        // Smooth swell using sine curve
        val swellAmount = calculateSwellAmount(progress)

        return AnimationState(
            scale = 1f + swellAmount * 0.3f, // Scale up to 30%
            alpha = 0.7f + swellAmount * 0.3f // Fade in effect
        )
    }

    private fun calculateSwellAmount(progress: Float): Float {
        // Smooth sine curve for natural swelling
        return sin(progress * Math.PI).toFloat()
    }
}