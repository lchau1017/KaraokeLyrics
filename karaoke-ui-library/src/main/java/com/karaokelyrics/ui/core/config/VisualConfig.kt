package com.karaokelyrics.ui.core.config

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Types of gradients available for text effects
 */
enum class GradientType {
    LINEAR,      // Simple linear gradient
    PROGRESS,    // Progress-based gradient for karaoke
    MULTI_COLOR, // Multi-color gradient
    SHIMMER,     // Shimmer effect gradient
    PRESET       // Use preset gradient patterns
}

/**
 * Preset gradient patterns
 */
enum class GradientPreset {
    RAINBOW,
    SUNSET,
    OCEAN,
    FIRE,
    NEON
}

/**
 * Visual styling configuration for the karaoke display.
 * Controls all visual aspects like colors, fonts, and alignment.
 */
data class VisualConfig(
    // Text Colors
    val playingTextColor: Color = Color.White,
    val playedTextColor: Color = Color.Gray,
    val upcomingTextColor: Color = Color.White.copy(alpha = 0.8f),
    val accompanimentTextColor: Color = Color(0xFFFFE082),

    // Font Configuration
    val fontSize: TextUnit = 34.sp,
    val accompanimentFontSize: TextUnit = 20.sp,
    val fontFamily: FontFamily? = null,
    val fontWeight: FontWeight = FontWeight.Bold,
    val letterSpacing: TextUnit = 0.sp,

    // Text Alignment
    val textAlign: TextAlign = TextAlign.Center,

    // Background
    val backgroundColor: Color = Color.Transparent,
    val lineBackgroundColor: Color = Color.Transparent,

    // Gradient Configuration
    val enableGradients: Boolean = true,
    val playingGradientColors: List<Color> = listOf(
        Color(0xFF00BCD4), // Cyan
        Color(0xFFE91E63)  // Pink
    ),
    val gradientAngle: Float = 45f,
    val gradientEnabled: Boolean = false,
    val gradientType: GradientType = GradientType.LINEAR,
    val gradientPreset: GradientPreset? = null,

    // Shadow Configuration
    val shadowEnabled: Boolean = false,
    val shadowColor: Color = Color.Black.copy(alpha = 0.3f),
    val shadowOffset: androidx.compose.ui.geometry.Offset = androidx.compose.ui.geometry.Offset(2f, 2f),

    // Glow Configuration
    val glowEnabled: Boolean = false,
    val glowColor: Color = Color.Yellow.copy(alpha = 0.5f),

    // Colors for gradient
    val colors: ColorConfig = ColorConfig()
)