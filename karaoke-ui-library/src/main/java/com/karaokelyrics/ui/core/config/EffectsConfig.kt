package com.karaokelyrics.ui.core.config

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Visual effects configuration for the karaoke display.
 * Controls blur, shadows, glow, and opacity settings.
 */
data class EffectsConfig(
    // Blur Effects
    val enableBlur: Boolean = true,
    val blurIntensity: Float = 1.0f,
    val playedLineBlur: Dp = 2.dp,
    val upcomingLineBlur: Dp = 3.dp,
    val distantLineBlur: Dp = 5.dp,

    // Shadow Effects
    val enableShadows: Boolean = true,
    val textShadowColor: Color = Color.Black.copy(alpha = 0.3f),
    val textShadowOffset: Offset = Offset(2f, 2f),
    val textShadowRadius: Float = 4f,

    // Glow Effects
    val enableGlow: Boolean = false,
    val glowColor: Color = Color.White,
    val glowRadius: Float = 8f,

    // Opacity
    val playingLineOpacity: Float = 1f,
    val playedLineOpacity: Float = 0.25f,
    val upcomingLineOpacity: Float = 0.6f,
    val distantLineOpacity: Float = 0.3f,

    // Visibility and Falloff (for viewers)
    val visibleLineRange: Int = 3,
    val opacityFalloff: Float = 0.1f,
    val maxOpacityReduction: Float = 0.4f,
    val distanceThreshold: Int = 3
)
