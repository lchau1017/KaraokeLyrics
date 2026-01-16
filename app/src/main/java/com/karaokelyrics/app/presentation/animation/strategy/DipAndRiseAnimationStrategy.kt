package com.karaokelyrics.app.presentation.animation.strategy

import androidx.compose.ui.geometry.Offset
import com.karaokelyrics.app.domain.model.animation.AnimationContext
import kotlin.math.cos
import javax.inject.Inject

/**
 * Animation strategy that creates a dip and rise effect.
 * Single Responsibility: Only calculates dip and rise animation.
 */
class DipAndRiseAnimationStrategy @Inject constructor() : AnimationStrategy {

    override val name = "DipAndRise"

    override fun calculate(
        progress: Float,
        context: AnimationContext
    ): AnimationState {
        // Calculate vertical movement
        val verticalOffset = calculateVerticalOffset(progress)

        // Slight rotation during dip
        val rotation = calculateRotation(progress)

        return AnimationState(
            offset = Offset(0f, verticalOffset * 15f), // 15 pixels movement
            rotation = rotation,
            scale = 1f - (kotlin.math.abs(verticalOffset) * 0.1f) // Slight shrink at extremes
        )
    }

    private fun calculateVerticalOffset(progress: Float): Float {
        // Cosine curve creates dip then rise
        return -cos(progress * Math.PI * 2).toFloat()
    }

    private fun calculateRotation(progress: Float): Float {
        // Slight rotation, max 5 degrees
        return kotlin.math.sin(progress * Math.PI * 2).toFloat() * 5f
    }
}