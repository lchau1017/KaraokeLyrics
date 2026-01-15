package com.karaokelyrics.app.domain.usecase.lyrics

import com.karaokelyrics.app.domain.model.AnimationType
import com.karaokelyrics.app.domain.model.karaoke.KaraokeSyllable
import com.karaokelyrics.app.domain.util.TextAnalysisUtils
import javax.inject.Inject

/**
 * Use case for determining animation type based on text characteristics
 * Single Responsibility: Decide animation strategy for lyrics
 */
class DetermineAnimationTypeUseCase @Inject constructor() {
    
    operator fun invoke(syllables: List<KaraokeSyllable>): AnimationType {
        val text = syllables.joinToString("") { it.content }
        
        // Business rule: Use simple animation for certain scripts
        return when {
            shouldUseSimpleAnimation(text) -> AnimationType.SIMPLE
            hasComplexTiming(syllables) -> AnimationType.CHARACTER_BY_CHARACTER
            else -> AnimationType.GRADIENT
        }
    }
    
    private fun shouldUseSimpleAnimation(text: String): Boolean {
        return when (TextAnalysisUtils.detectLanguageType(text)) {
            com.karaokelyrics.app.domain.util.LanguageType.CJK,
            com.karaokelyrics.app.domain.util.LanguageType.ARABIC,
            com.karaokelyrics.app.domain.util.LanguageType.DEVANAGARI -> true
            else -> false
        }
    }
    
    private fun hasComplexTiming(syllables: List<KaraokeSyllable>): Boolean {
        // Business rule: Complex timing when syllables have very different durations
        if (syllables.size < 2) return false
        
        val durations = syllables.map { it.end - it.start }
        val avgDuration = durations.average()
        val maxDeviation = durations.maxOf { kotlin.math.abs(it - avgDuration) }
        
        return maxDeviation > avgDuration * 0.5
    }
}