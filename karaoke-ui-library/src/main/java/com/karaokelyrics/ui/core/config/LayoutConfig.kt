package com.karaokelyrics.ui.core.config

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Layout configuration for the karaoke display.
 * Controls spacing, padding, and text direction.
 */
data class LayoutConfig(
    // Viewer configuration
    val viewerConfig: ViewerConfig = ViewerConfig(),

    // Spacing
    val linePadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    val lineSpacing: Dp = 12.dp,
    val wordSpacing: Dp = 4.dp,
    val characterSpacing: Dp = 0.dp,

    // Line Height
    val lineHeightMultiplier: Float = 1.2f,
    val accompanimentLineHeightMultiplier: Float = 1.0f,

    // Container
    val containerPadding: PaddingValues = PaddingValues(16.dp),
    val maxLineWidth: Dp? = null, // null means full width

    // Scrollable content padding
    val contentTopPadding: Dp = 16.dp, // Padding at the top of scrollable content
    val contentBottomPaddingRatio: Float = 1.0f, // Bottom padding as ratio of viewport height (1.0 = full height)
    val scrollTopOffset: Dp = 100.dp, // Top offset when scrolling lines to top position

    // Active line group spacing
    val activeGroupSpacing: Dp = 60.dp, // Extra spacing before/after active line groups
    val upcomingGroupSpacing: Dp = 60.dp, // Extra spacing before upcoming lines

    // RTL/LTR Support
    val forceTextDirection: LayoutDirection? = null // null means auto-detect
)