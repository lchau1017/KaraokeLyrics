package com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.animation

import com.karaokelyrics.app.data.util.TextLayoutCalculationUtil.SyllableLayout
import com.karaokelyrics.app.presentation.shared.animation.AnimationCalculator

/**
 * Handles character animation calculations.
 * Single Responsibility: Character animation timing and effects only.
 */
class CharacterAnimationCalculator {

    data class CharacterAnimationState(
        val timing: AnimationCalculator.CharacterAnimationTiming,
        val effects: AnimationCalculator.AnimationEffects
    )

    fun calculateCharacterAnimation(
        syllableLayout: SyllableLayout,
        characterIndex: Int,
        currentTimeMs: Int,
        animationStartTime: Int?
    ): CharacterAnimationState? {
        val wordAnimInfo = syllableLayout.wordAnimInfo ?: return null

        val absoluteCharIndex = syllableLayout.charOffsetInWord + characterIndex
        val numCharsInWord = wordAnimInfo.wordContent.length

        // Calculate animation timing
        val timing = AnimationCalculator.calculateCharacterTiming(
            characterIndex = absoluteCharIndex,
            totalCharacters = numCharsInWord,
            wordStartTime = wordAnimInfo.wordStartTime.toInt(),
            wordEndTime = wordAnimInfo.wordEndTime.toInt(),
            wordDuration = wordAnimInfo.wordDuration.toFloat(),
            currentTimeMs = currentTimeMs,
            animationStartTime = animationStartTime
        )

        // Calculate animation effects
        val effects = AnimationCalculator.calculateCharacterEffects(
            progress = timing.awesomeProgress,
            shouldAnimate = timing.shouldAnimate,
            wordDuration = wordAnimInfo.wordDuration.toFloat(),
            numCharsInWord = numCharsInWord
        )

        return CharacterAnimationState(timing, effects)
    }
}