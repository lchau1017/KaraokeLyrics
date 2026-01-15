package com.karaokelyrics.app.presentation.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Configuration for karaoke display
 */
data class KaraokeConfig(
    // Text Styles
    val normalLineTextStyle: TextStyle = TextStyle(
        fontSize = 34.sp,
        fontWeight = FontWeight.Bold,
        textMotion = TextMotion.Animated,
    ),
    val accompanimentLineTextStyle: TextStyle = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        textMotion = TextMotion.Animated,
    ),

    // Colors
    val activeTextColor: Color = SpotifyGreen,
    val inactiveTextColor: Color = SpotifyWhite.copy(alpha = 0.3f),
    val accompanimentActiveColor: Color = SpotifyGreen.copy(alpha = 0.7f),
    val accompanimentInactiveColor: Color = SpotifyWhite.copy(alpha = 0.2f),

    // Effects
    val enableBlurEffect: Boolean = true,
    val enableCharacterAnimations: Boolean = true,
    val enableGradientEffect: Boolean = true,

    // Layout
    val verticalPadding: Dp = 200.dp,
    val horizontalPadding: Dp = 24.dp,
    val lineSpacing: Dp = 12.dp,

    // Animation Timings
    val scrollAnimationDuration: Int = 400,
    val opacityAnimationDuration: Int = 300,
    val scaleAnimationDuration: Int = 300,

    // Opacity Settings
    val opacitySettings: OpacitySettings = OpacitySettings()
) {
    data class OpacitySettings(
        val currentLine: Float = 1f,
        val recentlyPlayed500ms: Float = 0.8f,
        val recentlyPlayed1s: Float = 0.6f,
        val recentlyPlayed2s: Float = 0.4f,
        val oldPlayedLines: Float = 0.25f,
        val upcoming1Line: Float = 0.6f,
        val upcoming2Lines: Float = 0.45f,
        val upcoming3Lines: Float = 0.35f,
        val upcomingFarLines: Float = 0.25f,
        val noActiveLines: Float = 0.3f
    )

    companion object {
        val Default = KaraokeConfig()

        val HighContrast = KaraokeConfig(
            inactiveTextColor = SpotifyWhite.copy(alpha = 0.1f),
            accompanimentInactiveColor = SpotifyWhite.copy(alpha = 0.05f),
            opacitySettings = OpacitySettings(
                oldPlayedLines = 0.1f,
                upcomingFarLines = 0.1f
            )
        )

        val Minimal = KaraokeConfig(
            enableBlurEffect = false,
            enableCharacterAnimations = false,
            normalLineTextStyle = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                textMotion = TextMotion.Animated,
            )
        )
    }
}