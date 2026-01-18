package com.kyrics.state

import androidx.compose.runtime.Immutable

/**
 * Immutable UI state for a single karaoke line.
 * Contains pre-calculated visual properties for efficient rendering.
 */
@Immutable
data class LineUiState(
    /**
     * Whether this line is currently being played (time is within start-end range)
     */
    val isPlaying: Boolean = false,

    /**
     * Whether this line has already been played (time is past end)
     */
    val hasPlayed: Boolean = false,

    /**
     * Whether this line is upcoming (time is before start)
     */
    val isUpcoming: Boolean = true,

    /**
     * Distance from the current playing line (0 if this is the current line)
     */
    val distanceFromCurrent: Int = 0,

    /**
     * Pre-calculated opacity value (0.0 to 1.0)
     */
    val opacity: Float = 0.6f,

    /**
     * Pre-calculated scale value (typically 1.0 to 1.1)
     */
    val scale: Float = 1f,

    /**
     * Pre-calculated blur radius in dp
     */
    val blurRadius: Float = 0f
) {
    companion object {
        /**
         * State for a currently playing line
         */
        val Playing = LineUiState(
            isPlaying = true,
            hasPlayed = false,
            isUpcoming = false,
            distanceFromCurrent = 0,
            opacity = 1f,
            scale = 1.05f,
            blurRadius = 0f
        )

        /**
         * State for a line that has already played
         */
        val Played = LineUiState(
            isPlaying = false,
            hasPlayed = true,
            isUpcoming = false,
            opacity = 0.25f,
            scale = 1f,
            blurRadius = 2f
        )

        /**
         * State for an upcoming line
         */
        val Upcoming = LineUiState(
            isPlaying = false,
            hasPlayed = false,
            isUpcoming = true,
            opacity = 0.6f,
            scale = 1f,
            blurRadius = 3f
        )
    }
}
