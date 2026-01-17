package com.karaokelyrics.app.presentation.features.lyrics.calculator.impl

import com.karaokelyrics.app.domain.model.ISyncedLine
import com.karaokelyrics.app.presentation.features.lyrics.calculator.TimingCalculator
import com.karaokelyrics.app.presentation.features.lyrics.model.TimingContext
import com.karaokelyrics.app.presentation.features.lyrics.model.TimingState
import javax.inject.Inject

/**
 * Default implementation of timing calculations.
 */
class DefaultTimingCalculator @Inject constructor() : TimingCalculator {

    override fun calculateTiming(
        line: ISyncedLine,
        currentTimeMs: Int,
        timingOffset: Int
    ): TimingContext {
        val adjustedTime = currentTimeMs + timingOffset
        
        val state = when {
            adjustedTime < line.start -> TimingState.UPCOMING
            adjustedTime in line.start..line.end -> TimingState.ACTIVE
            adjustedTime > line.end && (adjustedTime - line.end) < RECENT_WINDOW_MS -> TimingState.RECENT
            else -> TimingState.PAST
        }

        val progress = when (state) {
            TimingState.UPCOMING -> 0f
            TimingState.ACTIVE -> {
                val duration = line.end - line.start
                if (duration > 0) {
                    ((adjustedTime - line.start).toFloat() / duration).coerceIn(0f, 1f)
                } else 0f
            }
            else -> 1f
        }

        // Calculate distance from active line
        // This will be updated by the mapper with actual focused indices
        val distanceFromActive = when (state) {
            TimingState.ACTIVE -> 0
            TimingState.RECENT -> 1
            TimingState.UPCOMING -> calculateUpcomingDistance(line.start - adjustedTime)
            TimingState.PAST -> Int.MAX_VALUE
        }

        return TimingContext(
            currentTimeMs = adjustedTime,
            lineStartMs = line.start,
            lineEndMs = line.end,
            progress = progress,
            state = state,
            distanceFromActive = distanceFromActive
        )
    }

    private fun calculateUpcomingDistance(timeUntil: Int): Int {
        return when {
            timeUntil < 2000 -> 1
            timeUntil < 4000 -> 2
            timeUntil < 6000 -> 3
            timeUntil < 8000 -> 4
            else -> Int.MAX_VALUE
        }
    }

    companion object {
        private const val RECENT_WINDOW_MS = 1000
    }
}