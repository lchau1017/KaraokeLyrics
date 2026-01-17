package com.karaokelyrics.ui.rendering.color

import androidx.compose.ui.graphics.Color

/**
 * Calculates colors for characters based on timing and state.
 * Handles color interpolation and transitions.
 */
class ColorCalculator {

    /**
     * Calculate the color for a character based on its timing state
     */
    fun calculateCharacterColor(
        currentTimeMs: Int,
        charStartTime: Int,
        charEndTime: Int,
        baseColor: Color,
        playingColor: Color,
        playedColor: Color
    ): Color {
        return when {
            // Character has finished playing
            currentTimeMs > charEndTime -> playedColor

            // Character is currently playing
            currentTimeMs >= charStartTime -> {
                val progress = calculateProgress(currentTimeMs, charStartTime, charEndTime)
                lerpColor(baseColor, playingColor, progress)
            }

            // Character hasn't started yet
            else -> baseColor
        }
    }

    /**
     * Calculate line-level color based on state
     */
    fun calculateLineColor(
        isPlaying: Boolean,
        hasPlayed: Boolean,
        isAccompaniment: Boolean,
        playingTextColor: Color,
        playedTextColor: Color,
        upcomingTextColor: Color,
        accompanimentTextColor: Color
    ): Color {
        return when {
            isAccompaniment -> accompanimentTextColor
            isPlaying -> upcomingTextColor // Base color for unplayed chars in active line
            hasPlayed -> playedTextColor
            else -> upcomingTextColor
        }
    }

    /**
     * Calculate progress between start and end times
     */
    private fun calculateProgress(
        currentTime: Int,
        startTime: Int,
        endTime: Int
    ): Float {
        return if (endTime > startTime) {
            ((currentTime - startTime).toFloat() / (endTime - startTime))
                .coerceIn(0f, 1f)
        } else {
            1f
        }
    }

    /**
     * Interpolate between two colors
     */
    private fun lerpColor(start: Color, end: Color, fraction: Float): Color {
        return Color(
            red = start.red + (end.red - start.red) * fraction,
            green = start.green + (end.green - start.green) * fraction,
            blue = start.blue + (end.blue - start.blue) * fraction,
            alpha = start.alpha + (end.alpha - start.alpha) * fraction
        )
    }
}