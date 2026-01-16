package com.karaokelyrics.app.domain.usecase.animation

import com.karaokelyrics.app.domain.model.animation.*
import com.karaokelyrics.app.domain.model.karaoke.KaraokeSyllable
import javax.inject.Inject

/**
 * Use case for determining the appropriate animation configuration for a syllable.
 * This encapsulates the business logic for animation selection.
 *
 * Single Responsibility: Only handles animation configuration determination.
 */
class DetermineAnimationConfigUseCase @Inject constructor() {

    companion object {
        private const val CHARACTER_ANIMATION_THRESHOLD_MS = 500L
        private const val SIMPLE_ANIMATION_DURATION_MS = 300L
        private const val CHARACTER_ANIMATION_BASE_DURATION_MS = 400L
        private const val CHARACTER_ANIMATION_DELAY_PER_CHAR_MS = 50L
    }

    /**
     * Determine the animation configuration for a given syllable.
     *
     * @param syllable The syllable to animate
     * @param context The context for animation decisions
     * @return AnimationConfig with appropriate settings
     */
    operator fun invoke(
        syllable: KaraokeSyllable,
        context: AnimationContext
    ): AnimationConfig {
        // No animation if disabled
        if (!context.animationsEnabled) {
            return AnimationConfig(
                type = AnimationType.None,
                durationMs = 0
            )
        }

        // Background vocals get simple animation
        if (context.isBackgroundVocal) {
            return AnimationConfig(
                type = AnimationType.Simple,
                durationMs = SIMPLE_ANIMATION_DURATION_MS,
                easing = EasingFunction.EASE_IN_OUT
            )
        }

        // Determine if character animation is appropriate
        val shouldUseCharacterAnimation = context.characterAnimationsEnabled &&
                context.syllableDurationMs >= CHARACTER_ANIMATION_THRESHOLD_MS &&
                syllable.content.length > 1

        return if (shouldUseCharacterAnimation) {
            createCharacterAnimation(syllable, context)
        } else {
            createSimpleAnimation()
        }
    }

    private fun createCharacterAnimation(
        syllable: KaraokeSyllable,
        context: AnimationContext
    ): AnimationConfig {
        // Select animation style based on position and duration
        val style = selectCharacterAnimationStyle(context)

        return AnimationConfig(
            type = AnimationType.Character(style),
            durationMs = CHARACTER_ANIMATION_BASE_DURATION_MS +
                    (syllable.content.length * CHARACTER_ANIMATION_DELAY_PER_CHAR_MS),
            easing = getEasingForStyle(style)
        )
    }

    private fun createSimpleAnimation(): AnimationConfig {
        return AnimationConfig(
            type = AnimationType.Simple,
            durationMs = SIMPLE_ANIMATION_DURATION_MS,
            easing = EasingFunction.EASE_IN_OUT
        )
    }

    private fun selectCharacterAnimationStyle(context: AnimationContext): CharacterAnimationStyle {
        // Business logic for selecting animation style
        return when {
            context.syllableIndex == 0 -> CharacterAnimationStyle.BOUNCE
            context.syllableIndex == context.totalSyllablesInLine - 1 -> CharacterAnimationStyle.DIP_AND_RISE
            context.syllableDurationMs > 800 -> CharacterAnimationStyle.SWELL
            else -> CharacterAnimationStyle.WAVE
        }
    }

    private fun getEasingForStyle(style: CharacterAnimationStyle): EasingFunction {
        return when (style) {
            CharacterAnimationStyle.BOUNCE -> EasingFunction.BOUNCE
            CharacterAnimationStyle.SWELL -> EasingFunction.EASE_IN_OUT
            CharacterAnimationStyle.DIP_AND_RISE -> EasingFunction.SPRING
            CharacterAnimationStyle.WAVE -> EasingFunction.EASE_IN_OUT
            CharacterAnimationStyle.ROTATE -> EasingFunction.LINEAR
            CharacterAnimationStyle.FADE -> EasingFunction.EASE_OUT
        }
    }
}