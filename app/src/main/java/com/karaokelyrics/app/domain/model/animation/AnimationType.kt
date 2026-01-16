package com.karaokelyrics.app.domain.model.animation

/**
 * Represents different types of animations that can be applied to lyrics.
 * This follows the Open/Closed Principle - new animation types can be added
 * without modifying existing code.
 */
sealed class AnimationType {

    /**
     * No animation applied.
     */
    object None : AnimationType()

    /**
     * Simple fade or highlight animation.
     */
    object Simple : AnimationType()

    /**
     * Character-based animation with specific style.
     */
    data class Character(
        val style: CharacterAnimationStyle
    ) : AnimationType()
}

/**
 * Different styles of character animations.
 */
enum class CharacterAnimationStyle {
    BOUNCE,
    SWELL,
    DIP_AND_RISE,
    WAVE,
    ROTATE,
    FADE
}