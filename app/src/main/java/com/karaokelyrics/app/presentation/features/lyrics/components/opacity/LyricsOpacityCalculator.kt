package com.karaokelyrics.app.presentation.features.lyrics.components.opacity

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import com.karaokelyrics.app.presentation.features.lyrics.config.KaraokeConfig

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
        lineEndTime: Int,
        config: KaraokeConfig = KaraokeConfig.Default
    ): Float {
        return when {
            isCurrentLine -> config.activeLineOpacity
            hasBeenPlayed -> calculatePlayedOpacity(currentTimeMs - lineEndTime, config)
            isUpcoming -> calculateUpcomingOpacity(distanceFromCurrent, config)
            else -> config.distantLineOpacity
        }
    }

    private fun calculatePlayedOpacity(timeSincePlayed: Int, config: KaraokeConfig): Float {
        val time = timeSincePlayed.coerceAtLeast(0)
        return when {
            time < config.recentlyPlayedWindow -> config.recentlyPlayedOpacity
            time < config.recentlyPlayedWindow * 2 -> config.recentlyPlayedOpacity * 0.75f
            time < config.fadeOutWindow -> config.recentlyPlayedOpacity * 0.5f
            else -> config.playedLineOpacity
        }
    }

    private fun calculateUpcomingOpacity(distance: Int, config: KaraokeConfig): Float {
        return when (distance) {
            1 -> config.upcomingLineOpacity
            2 -> config.upcomingLineOpacity * 0.75f
            3 -> config.upcomingLineOpacity * 0.6f
            else -> config.distantLineOpacity
        }
    }

    fun calculateScale(
        isCurrentLine: Boolean,
        hasBeenPlayed: Boolean,
        isUpcoming: Boolean,
        distanceFromCurrent: Int,
        config: KaraokeConfig = KaraokeConfig.Default
    ): Float {
        return when {
            isCurrentLine -> config.activeLineScale
            else -> config.normalLineScale
        }
    }

    fun calculateBlur(
        useBlurEffect: Boolean,
        isUpcoming: Boolean,
        distanceFromCurrent: Int,
        config: KaraokeConfig = KaraokeConfig.Default
    ): Float {
        if (!useBlurEffect || !isUpcoming) return 0f

        return when (distanceFromCurrent) {
            in 4..6 -> config.distantLineBlurRadius * 0.6f
            in 7..Int.MAX_VALUE -> config.distantLineBlurRadius
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