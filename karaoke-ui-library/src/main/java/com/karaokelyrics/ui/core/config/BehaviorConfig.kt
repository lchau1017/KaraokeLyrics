package com.karaokelyrics.ui.core.config

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Behavior configuration for the karaoke display.
 * Controls scrolling, interaction, and performance settings.
 */
data class BehaviorConfig(
    // Scrolling
    val scrollBehavior: ScrollBehavior = ScrollBehavior.SMOOTH_CENTER,
    val scrollAnimationDuration: Int = 500,
    val scrollOffset: Dp = 100.dp,

    // Interaction
    val enableLineClick: Boolean = true
)

/**
 * Defines how the lyrics should scroll during playback.
 */
enum class ScrollBehavior {
    /** No automatic scrolling */
    NONE,

    /** Smooth scroll to center the playing line */
    SMOOTH_CENTER,

    /** Smooth scroll to put playing line at top */
    SMOOTH_TOP,

    /** Instant jump to center the playing line */
    INSTANT_CENTER,

    /** Page-like scrolling behavior */
    PAGED
}