package com.karaokelyrics.app.presentation.ui.components.animation

import com.karaokelyrics.app.presentation.ui.utils.EasingFunctions.DipAndRise
import com.karaokelyrics.app.presentation.ui.utils.EasingFunctions.Swell
import com.karaokelyrics.app.presentation.ui.utils.EasingFunctions.Bounce

/**
 * Calculates animation values for karaoke effects
 */
object AnimationCalculator {

    const val FAST_CHAR_ANIMATION_THRESHOLD_MS = 200f
    const val SIMPLE_ANIMATION_DURATION_MS = 700f
    const val ANIMATION_BUFFER_MS = 500

    data class CharacterAnimationTiming(
        val awesomeStartTime: Long,
        val awesomeDuration: Float,
        val awesomeProgress: Float,
        val shouldAnimate: Boolean
    )

    data class AnimationEffects(
        val floatOffset: Float,
        val scale: Float,
        val blurRadius: Float
    )

    /**
     * Calculate timing for character animation
     */
    fun calculateCharacterTiming(
        characterIndex: Int,
        totalCharacters: Int,
        wordStartTime: Int,
        wordEndTime: Int,
        wordDuration: Float,
        currentTimeMs: Int,
        animationStartTime: Int?
    ): CharacterAnimationTiming {
        val awesomeDuration = wordDuration * 0.8f

        // Calculate when this character should start
        val earliestStartTime = wordStartTime
        val latestStartTime = wordEndTime - awesomeDuration.toLong()

        val charRatio = if (totalCharacters > 1) {
            characterIndex.toFloat() / (totalCharacters - 1)
        } else {
            0.5f
        }

        val awesomeStartTime = (earliestStartTime +
            (latestStartTime - earliestStartTime) * charRatio).toLong()

        // Use fixed animation start time to prevent re-animation
        val actualStartTime = animationStartTime ?: currentTimeMs
        val fixedAwesomeStart = maxOf(actualStartTime.toLong(), awesomeStartTime)
        val timeSinceStart = currentTimeMs - fixedAwesomeStart.toInt()

        val shouldAnimate = timeSinceStart >= 0 &&
                           timeSinceStart.toFloat() <= awesomeDuration

        val progress = when {
            shouldAnimate -> (timeSinceStart.toFloat() / awesomeDuration).coerceIn(0f, 1f)
            timeSinceStart.toFloat() > awesomeDuration -> 1f
            else -> 0f
        }

        return CharacterAnimationTiming(
            awesomeStartTime = awesomeStartTime,
            awesomeDuration = awesomeDuration,
            awesomeProgress = progress,
            shouldAnimate = shouldAnimate
        )
    }

    /**
     * Calculate character animation effects
     */
    fun calculateCharacterEffects(
        progress: Float,
        shouldAnimate: Boolean,
        wordDuration: Float,
        numCharsInWord: Int
    ): AnimationEffects {
        val floatOffset = if (shouldAnimate && progress < 1f) {
            6f * DipAndRise(
                dip = ((0.6 * (wordDuration - FAST_CHAR_ANIMATION_THRESHOLD_MS * numCharsInWord) / 1000))
                    .coerceIn(0.0, 0.6)
            ).transform(1.0f - progress)
        } else {
            0f
        }

        val scale = if (shouldAnimate && progress < 1f) {
            1f + Swell(
                (0.15 * (wordDuration - FAST_CHAR_ANIMATION_THRESHOLD_MS * numCharsInWord) / 1000)
                    .coerceIn(0.0, 0.15)
            ).transform(progress)
        } else {
            1f
        }

        val blurRadius = if (shouldAnimate && progress < 1f) {
            12f * Bounce.transform(progress)
        } else {
            0f
        }

        return AnimationEffects(
            floatOffset = floatOffset,
            scale = scale,
            blurRadius = blurRadius
        )
    }

    /**
     * Calculate simple animation progress
     */
    fun calculateSimpleAnimationProgress(
        syllableStartTime: Int,
        currentTimeMs: Int,
        animationStartTime: Int?
    ): Float {
        val actualStartTime = animationStartTime ?: syllableStartTime
        val timeSinceStart = currentTimeMs - actualStartTime

        return if (timeSinceStart >= 0 && timeSinceStart <= SIMPLE_ANIMATION_DURATION_MS) {
            (timeSinceStart / SIMPLE_ANIMATION_DURATION_MS).coerceIn(0f, 1f)
        } else {
            1f
        }
    }

    /**
     * Determine if we should use character animation for a word
     */
    fun shouldUseCharacterAnimation(
        wordDuration: Float,
        wordContent: String,
        isAccompaniment: Boolean
    ): Boolean {
        val perCharDuration = wordDuration / wordContent.length
        return perCharDuration > FAST_CHAR_ANIMATION_THRESHOLD_MS &&
               wordDuration >= 1000 &&
               !isAccompaniment &&
               !wordContent.all { it.isWhitespace() || it in ".,!?;:" }
    }
}