package com.karaokelyrics.app.domain.usecase

import com.karaokelyrics.app.domain.model.karaoke.KaraokeSyllable
import javax.inject.Inject

/**
 * Data class representing the animation decision for a word.
 */
data class AnimationDecision(
    val useAwesomeAnimation: Boolean,
    val animationThresholdMs: Float,
    val wordDuration: Int,
    val perCharDuration: Float
)

/**
 * Domain use case for determining the appropriate animation type for karaoke words.
 * Encapsulates business rules for animation selection.
 */
class DetermineAnimationTypeUseCase @Inject constructor() {

    // Business rule constants - these define the animation thresholds
    private val fastCharAnimationThresholdMs = 100f  // Relaxed from 200ms
    private val minimumWordDurationMs = 500          // Relaxed from 1000ms

    operator fun invoke(
        wordSyllables: List<KaraokeSyllable>,
        isAccompanimentLine: Boolean,
        enableCharacterAnimations: Boolean
    ): AnimationDecision {
        if (wordSyllables.isEmpty()) {
            return AnimationDecision(
                useAwesomeAnimation = false,
                animationThresholdMs = fastCharAnimationThresholdMs,
                wordDuration = 0,
                perCharDuration = 0f
            )
        }

        val wordContent = wordSyllables.joinToString("") { it.content }
        val wordDuration = wordSyllables.last().end - wordSyllables.first().start
        val perCharDuration = if (wordContent.isNotEmpty() && wordDuration > 0) {
            wordDuration.toFloat() / wordContent.length
        } else {
            0f
        }

        // Business rules for animation selection
        val shouldUseSimpleAnimation = shouldUseSimpleAnimation(wordContent)
        val meetsTimingThreshold = perCharDuration > fastCharAnimationThresholdMs
        val meetsDurationRequirement = wordDuration >= minimumWordDurationMs
        val isMainVocalLine = !isAccompanimentLine
        val animationsEnabled = enableCharacterAnimations

        val useAwesomeAnimation = animationsEnabled &&
                meetsTimingThreshold &&
                meetsDurationRequirement &&
                !shouldUseSimpleAnimation &&
                isMainVocalLine

        return AnimationDecision(
            useAwesomeAnimation = useAwesomeAnimation,
            animationThresholdMs = fastCharAnimationThresholdMs,
            wordDuration = wordDuration,
            perCharDuration = perCharDuration
        )
    }

    /**
     * Determines if text should use simple animation based on language characteristics.
     * Business rule: CJK, Arabic, and Devanagari scripts use simple animations.
     */
    private fun shouldUseSimpleAnimation(text: String): Boolean {
        val cleanedStr = text.filter { !it.isWhitespace() && !isPunctuation(it) }
        if (cleanedStr.isEmpty()) return false

        return isPureCjk(cleanedStr) || cleanedStr.any { isArabic(it) || isDevanagari(it) }
    }

    private fun isPureCjk(text: String): Boolean {
        return text.all { char ->
            val codePoint = char.code
            // CJK Unified Ideographs, Hiragana, Katakana, CJK symbols
            codePoint in 0x3040..0x309F ||  // Hiragana
            codePoint in 0x30A0..0x30FF ||  // Katakana
            codePoint in 0x4E00..0x9FFF ||  // CJK Unified Ideographs
            codePoint in 0x3400..0x4DBF ||  // CJK Extension A
            codePoint in 0x20000..0x2A6DF || // CJK Extension B
            codePoint in 0x2A700..0x2B73F || // CJK Extension C
            codePoint in 0x2B740..0x2B81F || // CJK Extension D
            codePoint in 0x3000..0x303F     // CJK Symbols and Punctuation
        }
    }

    private fun isPunctuation(char: Char): Boolean {
        return char.category in listOf(
            CharCategory.CONNECTOR_PUNCTUATION,
            CharCategory.DASH_PUNCTUATION,
            CharCategory.END_PUNCTUATION,
            CharCategory.FINAL_QUOTE_PUNCTUATION,
            CharCategory.INITIAL_QUOTE_PUNCTUATION,
            CharCategory.OTHER_PUNCTUATION,
            CharCategory.START_PUNCTUATION
        )
    }

    private fun isArabic(char: Char): Boolean {
        return char.code in 0x0600..0x06FF || char.code in 0x0750..0x077F ||
               char.code in 0x08A0..0x08FF || char.code in 0xFB50..0xFDFF ||
               char.code in 0xFE70..0xFEFF
    }

    private fun isDevanagari(char: Char): Boolean {
        return char.code in 0x0900..0x097F
    }
}