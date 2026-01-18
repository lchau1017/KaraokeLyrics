package com.kyrics.config

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
    // null means full width
    val maxLineWidth: Dp? = null,

    // RTL/LTR Support (null means auto-detect)
    val forceTextDirection: LayoutDirection? = null
)
