package com.karaokelyrics.ui.state

import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import kotlin.math.abs

/**
 * Pure calculation logic for karaoke UI state.
 * This class has no Compose dependencies and is fully unit-testable.
 *
 * Extracts and consolidates state calculation logic that was previously
 * scattered across AnimationManager, EffectsManager, and various viewers.
 */
class KaraokeStateCalculator {

    /**
     * Calculate the complete UI state based on current time and configuration.
     *
     * @param lines List of synchronized lines
     * @param currentTimeMs Current playback time in milliseconds
     * @param config Library configuration for visual/animation settings
     * @return Complete UI state ready for rendering
     */
    fun calculateState(lines: List<ISyncedLine>, currentTimeMs: Int, config: KaraokeLibraryConfig): KaraokeUiState {
        if (lines.isEmpty()) {
            return KaraokeUiState(currentTimeMs = currentTimeMs, isInitialized = true)
        }

        val currentLineIndex = findCurrentLineIndex(lines, currentTimeMs)

        val lineStates = lines.mapIndexed { index, line ->
            index to calculateLineState(
                line = line,
                lineIndex = index,
                currentLineIndex = currentLineIndex,
                currentTimeMs = currentTimeMs,
                config = config
            )
        }.toMap()

        return KaraokeUiState(
            lines = lines,
            currentTimeMs = currentTimeMs,
            currentLineIndex = currentLineIndex,
            lineStates = lineStates,
            isInitialized = true
        )
    }

    /**
     * Find the index of the currently playing line.
     *
     * @param lines List of synchronized lines
     * @param currentTimeMs Current playback time
     * @return Index of the playing line, or null if no line is playing
     */
    fun findCurrentLineIndex(lines: List<ISyncedLine>, currentTimeMs: Int): Int? = lines.indexOfFirst { line ->
        currentTimeMs >= line.start && currentTimeMs <= line.end
    }.takeIf { it != -1 }

    /**
     * Calculate the UI state for a single line.
     *
     * @param line The line to calculate state for
     * @param lineIndex Index of this line in the list
     * @param currentLineIndex Index of the currently playing line (if any)
     * @param currentTimeMs Current playback time
     * @param config Library configuration
     * @return Calculated line UI state
     */
    fun calculateLineState(
        line: ISyncedLine,
        lineIndex: Int,
        currentLineIndex: Int?,
        currentTimeMs: Int,
        config: KaraokeLibraryConfig
    ): LineUiState {
        val isPlaying = currentTimeMs >= line.start && currentTimeMs <= line.end
        val hasPlayed = currentTimeMs > line.end
        val isUpcoming = currentTimeMs < line.start

        val distanceFromCurrent = calculateDistanceFromCurrent(lineIndex, currentLineIndex)

        val opacity = calculateOpacity(
            isPlaying = isPlaying,
            hasPlayed = hasPlayed,
            distance = distanceFromCurrent,
            config = config
        )

        val scale = calculateScale(
            isPlaying = isPlaying,
            config = config
        )

        val blurRadius = calculateBlurRadius(
            isPlaying = isPlaying,
            hasPlayed = hasPlayed,
            distance = distanceFromCurrent,
            config = config
        )

        return LineUiState(
            isPlaying = isPlaying,
            hasPlayed = hasPlayed,
            isUpcoming = isUpcoming,
            distanceFromCurrent = distanceFromCurrent,
            opacity = opacity,
            scale = scale,
            blurRadius = blurRadius
        )
    }

    /**
     * Calculate the distance (in lines) from the current playing line.
     */
    fun calculateDistanceFromCurrent(lineIndex: Int, currentLineIndex: Int?): Int = if (currentLineIndex != null) {
        abs(lineIndex - currentLineIndex)
    } else {
        lineIndex
    }

    /**
     * Calculate opacity based on line state and distance.
     * Matches the logic from EffectsManager.calculateOpacity
     */
    fun calculateOpacity(isPlaying: Boolean, hasPlayed: Boolean, distance: Int, config: KaraokeLibraryConfig): Float {
        val effects = config.effects
        return when {
            isPlaying -> effects.playingLineOpacity
            hasPlayed -> effects.playedLineOpacity
            else -> {
                // Upcoming line - reduce opacity based on distance
                val distanceReduction = (distance * effects.opacityFalloff).coerceAtMost(effects.maxOpacityReduction)
                (effects.upcomingLineOpacity - distanceReduction).coerceAtLeast(0.2f)
            }
        }
    }

    /**
     * Calculate scale based on playing state.
     * Matches the logic from AnimationManager.animateLine
     */
    fun calculateScale(isPlaying: Boolean, config: KaraokeLibraryConfig): Float = if (isPlaying && config.animation.enableLineAnimations) {
        config.animation.lineScaleOnPlay
    } else {
        1f
    }

    /**
     * Calculate blur radius based on line state and distance.
     * Matches the logic from EffectsManager.applyConditionalBlur
     */
    fun calculateBlurRadius(isPlaying: Boolean, hasPlayed: Boolean, distance: Int, config: KaraokeLibraryConfig): Float {
        val effects = config.effects
        if (!effects.enableBlur) {
            return 0f
        }

        val baseBlurRadius = when {
            isPlaying -> 0f
            hasPlayed -> effects.playedLineBlur.value
            distance > effects.distanceThreshold -> effects.distantLineBlur.value
            else -> effects.upcomingLineBlur.value
        }

        return baseBlurRadius * effects.blurIntensity
    }

    /**
     * Determine the appropriate line state category.
     * Useful for simple state checks without full calculation.
     */
    fun getLineStateCategory(line: ISyncedLine, currentTimeMs: Int): LineStateCategory = when {
        currentTimeMs >= line.start && currentTimeMs <= line.end -> LineStateCategory.PLAYING
        currentTimeMs > line.end -> LineStateCategory.PLAYED
        else -> LineStateCategory.UPCOMING
    }

    /**
     * Simple enumeration of line state categories
     */
    enum class LineStateCategory {
        PLAYING,
        PLAYED,
        UPCOMING
    }
}
