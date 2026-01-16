package com.karaokelyrics.app.presentation.features.lyrics.components.opacity

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color

/**
 * Calculates opacity values for lyrics lines based on their state.
 * Single Responsibility: Opacity calculation only.
 */
object LyricsOpacityCalculator {

    fun calculateOpacity(
        isCurrentLine: Boolean,
        hasBeenPlayed: Boolean,
        isUpcoming: Boolean,
        distanceFromCurrent: Int,
        currentTimeMs: Int,
        lineEndTime: Int
    ): Float {
        return when {
            isCurrentLine -> 1f // Currently playing
            hasBeenPlayed -> calculatePlayedOpacity(currentTimeMs - lineEndTime)
            isUpcoming -> calculateUpcomingOpacity(distanceFromCurrent)
            else -> 0.25f // Default minimum opacity
        }
    }

    private fun calculatePlayedOpacity(timeSincePlayed: Int): Float {
        val time = timeSincePlayed.coerceAtLeast(0)
        return when {
            time < 500 -> 0.8f
            time < 1000 -> 0.6f
            time < 2000 -> 0.4f
            else -> 0.25f // Minimum opacity for old played lines
        }
    }

    private fun calculateUpcomingOpacity(distance: Int): Float {
        return when (distance) {
            1 -> 0.6f
            2 -> 0.45f
            3 -> 0.35f
            else -> 0.25f
        }
    }

    fun calculateScale(
        isCurrentLine: Boolean,
        hasBeenPlayed: Boolean,
        isUpcoming: Boolean,
        distanceFromCurrent: Int
    ): Float {
        return when {
            isCurrentLine -> 1.05f // Only scale the currently playing line
            else -> 1.0f // All other lines remain at normal scale
        }
    }

    fun calculateBlur(
        useBlurEffect: Boolean,
        isUpcoming: Boolean,
        distanceFromCurrent: Int
    ): Float {
        if (!useBlurEffect || !isUpcoming) return 0f

        return when (distanceFromCurrent) {
            in 4..6 -> 1.5f
            in 7..Int.MAX_VALUE -> 2.5f
            else -> 0f
        }
    }

    fun calculateTextColor(
        baseColor: Color,
        opacity: Float
    ): Color {
        return baseColor.copy(alpha = opacity)
    }
}

@Composable
fun animatedOpacity(targetValue: Float): State<Float> {
    return animateFloatAsState(
        targetValue = targetValue,
        label = "opacity"
    )
}