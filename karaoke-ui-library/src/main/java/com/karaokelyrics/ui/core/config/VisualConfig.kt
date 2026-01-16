package com.karaokelyrics.ui.core.config

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

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
    val gradientAngle: Float = 45f
)