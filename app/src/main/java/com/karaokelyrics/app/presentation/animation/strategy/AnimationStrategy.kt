package com.karaokelyrics.app.presentation.animation.strategy

import androidx.compose.ui.geometry.Offset
import com.karaokelyrics.app.domain.model.animation.AnimationContext

/**
 * Strategy interface for animation calculations.
 * Follows Open/Closed Principle - new animation types can be added
 * by implementing this interface without modifying existing code.
 */
interface AnimationStrategy {

    /**
     * Calculate animation state for a given progress.
     *
     * @param progress Animation progress from 0.0 to 1.0
     * @param context Animation context with additional parameters
     * @return Calculated animation state
     */
    fun calculate(
        progress: Float,
        context: AnimationContext
    ): AnimationState

    /**
     * Get the name of this animation strategy.
     */
    val name: String
}

/**
 * Represents the current state of an animation.
 */
data class AnimationState(
    val offset: Offset = Offset.Zero,
    val rotation: Float = 0f,
    val scale: Float = 1f,
    val alpha: Float = 1f
)

/**
 * Represents animation state for multiple characters.
 */
data class CharacterAnimationStates(
    val states: List<AnimationState>
)