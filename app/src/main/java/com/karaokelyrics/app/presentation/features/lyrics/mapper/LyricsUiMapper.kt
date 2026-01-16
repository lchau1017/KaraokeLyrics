package com.karaokelyrics.app.presentation.features.lyrics.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.karaokelyrics.app.domain.model.ISyncedLine
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.presentation.features.lyrics.components.focus.LyricsFocusCalculator
import com.karaokelyrics.app.presentation.features.lyrics.components.opacity.LyricsOpacityCalculator
import com.karaokelyrics.app.presentation.features.lyrics.config.KaraokeConfig
import com.karaokelyrics.app.presentation.features.lyrics.model.*
import javax.inject.Inject

/**
 * Maps domain lyrics models to UI models.
 * Single Responsibility: Transform domain data to presentation layer.
 * All calculations are done here, not in the view.
 */
class LyricsUiMapper @Inject constructor() {

    /**
     * Maps SyncedLyrics to LyricsUiState with all pre-calculated values
     */
    fun mapToUiState(
        lyrics: SyncedLyrics,
        currentTimeMs: Int,
        textColor: Color,
        normalTextStyle: TextStyle,
        accompanimentTextStyle: TextStyle? = null,
        config: KaraokeConfig = KaraokeConfig.Default
    ): LyricsUiState {
        // Calculate focused indices first
        val focusedLineIndex = LyricsFocusCalculator.findFocusedLineIndex(lyrics.lines, currentTimeMs)
        val allFocusedIndices = LyricsFocusCalculator.findAllFocusedIndices(lyrics.lines, currentTimeMs)

        // Map each line to UI model
        val uiLines = lyrics.lines.mapIndexed { index, line ->
            // Determine text style based on line type
            val textStyle = when (line) {
                is KaraokeLine -> if (line.isAccompaniment && accompanimentTextStyle != null) {
                    accompanimentTextStyle
                } else {
                    normalTextStyle
                }
                else -> normalTextStyle
            }

            mapLineToUiModel(
                line = line,
                index = index,
                currentTimeMs = currentTimeMs,
                focusedIndices = allFocusedIndices,
                textColor = textColor,
                textStyle = textStyle,
                config = config
            )
        }

        return LyricsUiState(
            lines = uiLines,
            focusedLineIndex = focusedLineIndex,
            allFocusedIndices = allFocusedIndices,
            scrollTargetIndex = focusedLineIndex,
            currentTimeMs = currentTimeMs
        )
    }

    /**
     * Maps a single line to UI model with all calculations
     */
    private fun mapLineToUiModel(
        line: ISyncedLine,
        index: Int,
        currentTimeMs: Int,
        focusedIndices: List<Int>,
        textColor: Color,
        textStyle: TextStyle,
        config: KaraokeConfig
    ): LyricsLineUiModel {
        val isCurrentLine = index in focusedIndices
        val hasBeenPlayed = line.end <= currentTimeMs
        val isUpcoming = line.start > currentTimeMs

        // Calculate distance from current
        val distanceFromCurrent = if (focusedIndices.isEmpty()) {
            // When no line is focused, calculate distance based on timing
            when {
                hasBeenPlayed -> Int.MAX_VALUE // Past lines are far
                isUpcoming -> {
                    // Calculate distance based on time until play
                    val timeUntil = line.start - currentTimeMs
                    when {
                        timeUntil < 2000 -> 1
                        timeUntil < 4000 -> 2
                        timeUntil < 6000 -> 3
                        else -> Int.MAX_VALUE
                    }
                }
                else -> 0
            }
        } else {
            // Calculate distance from focused indices
            val minDistance = focusedIndices.minOf { kotlin.math.abs(index - it) }
            minDistance
        }

        // Calculate visual state
        val visualState = calculateVisualState(
            isCurrentLine = isCurrentLine,
            hasBeenPlayed = hasBeenPlayed,
            isUpcoming = isUpcoming,
            distanceFromCurrent = distanceFromCurrent,
            currentTimeMs = currentTimeMs,
            lineEndTime = line.end,
            textColor = textColor,
            textStyle = textStyle,
            config = config
        )

        // Calculate animation state
        val animationState = AnimationState(
            isActive = isCurrentLine,
            isPast = hasBeenPlayed,
            isUpcoming = isUpcoming,
            progress = calculateProgress(line, currentTimeMs),
            distanceFromCurrent = distanceFromCurrent,
            timeSincePlayed = if (hasBeenPlayed) (currentTimeMs - line.end).toLong() else 0L,
            timeUntilPlay = if (isUpcoming) (line.start - currentTimeMs).toLong() else 0L
        )

        // Calculate interaction state
        val interactionState = InteractionState(
            isClickable = isUpcoming,
            isHighlighted = isCurrentLine,
            isFocused = isCurrentLine
        )

        return LyricsLineUiModel(
            line = line,
            index = index,
            visualState = visualState,
            animationState = animationState,
            interactionState = interactionState
        )
    }

    /**
     * Calculate all visual properties
     */
    private fun calculateVisualState(
        isCurrentLine: Boolean,
        hasBeenPlayed: Boolean,
        isUpcoming: Boolean,
        distanceFromCurrent: Int,
        currentTimeMs: Int,
        lineEndTime: Int,
        textColor: Color,
        textStyle: TextStyle,
        config: KaraokeConfig
    ): VisualState {
        val opacity = LyricsOpacityCalculator.calculateOpacity(
            isCurrentLine = isCurrentLine,
            hasBeenPlayed = hasBeenPlayed,
            isUpcoming = isUpcoming,
            distanceFromCurrent = distanceFromCurrent,
            currentTimeMs = currentTimeMs,
            lineEndTime = lineEndTime,
            config = config
        )

        val scale = LyricsOpacityCalculator.calculateScale(
            isCurrentLine = isCurrentLine,
            hasBeenPlayed = hasBeenPlayed,
            isUpcoming = isUpcoming,
            distanceFromCurrent = distanceFromCurrent,
            config = config
        )

        val blur = LyricsOpacityCalculator.calculateBlur(
            useBlurEffect = true,
            isUpcoming = isUpcoming,
            distanceFromCurrent = distanceFromCurrent,
            config = config
        )

        return VisualState(
            opacity = opacity,
            scale = scale,
            blur = blur,
            color = textColor, // Don't modify alpha here, use opacity in rendering
            textStyle = textStyle
        )
    }

    /**
     * Calculate line progress (0.0 to 1.0)
     */
    private fun calculateProgress(line: ISyncedLine, currentTimeMs: Int): Float {
        if (currentTimeMs < line.start) return 0f
        if (currentTimeMs > line.end) return 1f
        
        val duration = line.end - line.start
        if (duration <= 0) return 0f
        
        return ((currentTimeMs - line.start).toFloat() / duration).coerceIn(0f, 1f)
    }
}