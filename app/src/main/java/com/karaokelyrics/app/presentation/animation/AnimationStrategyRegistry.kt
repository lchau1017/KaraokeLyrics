package com.karaokelyrics.app.presentation.animation

import com.karaokelyrics.app.domain.model.animation.AnimationType
import com.karaokelyrics.app.domain.model.animation.CharacterAnimationStyle
import com.karaokelyrics.app.presentation.animation.strategy.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Registry for animation strategies.
 * Follows Open/Closed Principle - new strategies can be registered
 * without modifying existing code.
 */
@Singleton
class AnimationStrategyRegistry @Inject constructor(
    bounceStrategy: BounceAnimationStrategy,
    swellStrategy: SwellAnimationStrategy,
    dipAndRiseStrategy: DipAndRiseAnimationStrategy,
    simpleStrategy: SimpleAnimationStrategy
) {
    private val strategies = mutableMapOf<String, AnimationStrategy>()

    init {
        // Register default strategies
        register("bounce", bounceStrategy)
        register("swell", swellStrategy)
        register("dipAndRise", dipAndRiseStrategy)
        register("simple", simpleStrategy)
    }

    /**
     * Register a new animation strategy.
     *
     * @param key The key to identify the strategy
     * @param strategy The animation strategy
     */
    fun register(key: String, strategy: AnimationStrategy) {
        strategies[key] = strategy
    }

    /**
     * Get a strategy by key.
     *
     * @param key The key of the strategy
     * @return The animation strategy, or SimpleAnimationStrategy if not found
     */
    fun getStrategy(key: String): AnimationStrategy {
        return strategies[key] ?: strategies["simple"]!!
    }

    /**
     * Get a strategy for a given animation type.
     *
     * @param animationType The type of animation
     * @return The appropriate animation strategy
     */
    fun getStrategyForType(animationType: AnimationType): AnimationStrategy {
        return when (animationType) {
            is AnimationType.None -> strategies["simple"]!!
            is AnimationType.Simple -> strategies["simple"]!!
            is AnimationType.Character -> getStrategyForCharacterStyle(animationType.style)
        }
    }

    private fun getStrategyForCharacterStyle(style: CharacterAnimationStyle): AnimationStrategy {
        return when (style) {
            CharacterAnimationStyle.BOUNCE -> strategies["bounce"]!!
            CharacterAnimationStyle.SWELL -> strategies["swell"]!!
            CharacterAnimationStyle.DIP_AND_RISE -> strategies["dipAndRise"]!!
            CharacterAnimationStyle.WAVE -> strategies["swell"]!! // Use swell for wave
            CharacterAnimationStyle.ROTATE -> strategies["dipAndRise"]!! // Use dip for rotate
            CharacterAnimationStyle.FADE -> strategies["simple"]!!
        }
    }

    /**
     * Get all registered strategy names.
     */
    fun getRegisteredStrategies(): Set<String> = strategies.keys
}