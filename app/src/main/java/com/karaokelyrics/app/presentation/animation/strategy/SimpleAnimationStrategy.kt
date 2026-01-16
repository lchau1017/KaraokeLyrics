package com.karaokelyrics.app.presentation.animation.strategy

import com.karaokelyrics.app.domain.model.animation.AnimationContext
import javax.inject.Inject

/**
 * Simple fade animation strategy.
 * Single Responsibility: Only calculates simple fade animation.
 */
class SimpleAnimationStrategy @Inject constructor() : AnimationStrategy {

    override val name = "Simple"

    override fun calculate(
        progress: Float,
        context: AnimationContext
    ): AnimationState {
        // Simple fade in
        return AnimationState(
            alpha = progress
        )
    }
}