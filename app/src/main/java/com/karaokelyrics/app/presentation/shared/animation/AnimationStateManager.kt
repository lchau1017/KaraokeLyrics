package com.karaokelyrics.app.presentation.shared.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateMapOf

/**
 * Manages animation state for karaoke syllables to prevent re-animation
 */
class AnimationStateManager {
    // Track when each syllable started animating
    private val syllableAnimationStartTimes = mutableStateMapOf<String, Int>()

    /**
     * Get or record the animation start time for a syllable
     */
    fun getAnimationStartTime(
        syllableKey: String,
        currentTimeMs: Int,
        isActive: Boolean
    ): Int? {
        // Check if already animating
        val existingStartTime = syllableAnimationStartTimes[syllableKey]
        if (existingStartTime != null) {
            return existingStartTime
        }

        // Record new animation start if becoming active
        if (isActive) {
            syllableAnimationStartTimes[syllableKey] = currentTimeMs
            return currentTimeMs
        }

        return null
    }

    /**
     * Clear old animation states to prevent memory leaks
     */
    fun clearOldAnimations(currentTimeMs: Int, maxAge: Int = 10000) {
        val cutoffTime = currentTimeMs - maxAge
        syllableAnimationStartTimes.entries.removeIf { (_, startTime) ->
            startTime < cutoffTime
        }
    }

    /**
     * Clear all animations (useful when colors or other visual properties change)
     */
    fun clearAllAnimations() {
        syllableAnimationStartTimes.clear()
    }

    /**
     * Create a unique key for a syllable
     */
    fun createSyllableKey(start: Int, end: Int, content: String): String {
        return "$start-$end-${content.hashCode()}"
    }
}

@Composable
fun rememberAnimationStateManager(): AnimationStateManager {
    return remember { AnimationStateManager() }
}