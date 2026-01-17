package com.karaokelyrics.ui.core.config

/**
 * Defines different viewer types for the karaoke display.
 * Each type has its own scrolling and positioning behavior.
 */
enum class ViewerType {
    /**
     * Center-focused viewer for karaoke/performance mode.
     * - Active line always centered in viewport
     * - Played lines fade out above
     * - Upcoming lines hidden below
     * - Ideal for: Karaoke apps, live performances, demos
     */
    CENTER_FOCUSED,

    /**
     * Smooth scrolling viewer for reading/subtitle mode.
     * - Active line scrolls to top third of viewport
     * - Multiple lines visible at once
     * - Natural reading flow
     * - Ideal for: Subtitle display, lyrics reading, following along
     */
    SMOOTH_SCROLL,

    /**
     * Minimal viewer showing only active line.
     * - Only current line visible
     * - No context lines shown
     * - Maximum focus on current content
     * - Ideal for: Single-line displays, widgets, minimal UI
     */
    SINGLE_LINE,

    /**
     * Paged viewer that shows groups of lines.
     * - Lines grouped by verse/section
     * - Swipe or auto-advance between pages
     * - All lines in group visible at once
     * - Ideal for: Verse-by-verse display, presentation mode
     */
    PAGED
}

/**
 * Configuration specific to each viewer type.
 */
data class ViewerConfig(
    val type: ViewerType = ViewerType.SMOOTH_SCROLL,

    // CENTER_FOCUSED specific
    val centerOffset: Float = 0.5f, // 0.5 = exact center, 0.3 = upper third
    val visibleLinesBefore: Int = 1, // How many played lines to keep visible
    val visibleLinesAfter: Int = 1, // How many upcoming lines to show

    // SMOOTH_SCROLL specific
    val scrollPosition: Float = 0.33f, // Where to position active line (0.33 = top third)
    val smoothScrollDuration: Int = 500, // Animation duration in ms

    // SINGLE_LINE specific
    val transitionAnimation: Boolean = true, // Animate line changes

    // PAGED specific
    val autoAdvancePages: Boolean = true, // Auto-advance to next page
    val pageTransitionDelay: Int = 500 // Delay before advancing pages
)