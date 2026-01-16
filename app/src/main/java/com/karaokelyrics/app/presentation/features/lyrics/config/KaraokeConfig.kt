package com.karaokelyrics.app.presentation.features.lyrics.config

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Configuration for karaoke animations and effects.
 * Single source of truth for all animation parameters.
 */
data class KaraokeConfig(
    // Animation Timing
    val characterAnimationDuration: Float = 800f,
    val simpleAnimationDuration: Float = 700f,
    val scrollAnimationDuration: Int = 400,
    val animationBuffer: Int = 500,

    // Visual Effects
    val activeLineScale: Float = 1.05f,
    val normalLineScale: Float = 1.0f,
    val characterMaxScale: Float = 1.15f,
    val characterFloatOffset: Float = 6f,
    val simpleFloatOffset: Float = 4f,

    // Blur Effects
    val unplayedBlurRadius: Float = 20f,
    val characterBlurRadius: Float = 12f,
    val distantLineBlurRadius: Float = 2.5f,

    // Opacity Settings
    val activeLineOpacity: Float = 1f,
    val recentlyPlayedOpacity: Float = 0.8f,
    val playedLineOpacity: Float = 0.25f,
    val upcomingLineOpacity: Float = 0.6f,
    val distantLineOpacity: Float = 0.25f,

    // Layout Settings
    val linePadding: Dp = 24.dp,
    val lineSpacing: Dp = 12.dp,
    val scrollOffset: Dp = 100.dp,

    // Timing Windows (ms)
    val recentlyPlayedWindow: Long = 500,
    val fadeOutWindow: Long = 2000,
    val cleanupInterval: Int = 5000
) {
    companion object {
        val Default = KaraokeConfig()

        // Preset configurations
        val Subtle = KaraokeConfig(
            characterFloatOffset = 3f,
            characterMaxScale = 1.08f,
            unplayedBlurRadius = 10f
        )

        val Dramatic = KaraokeConfig(
            characterFloatOffset = 10f,
            characterMaxScale = 1.25f,
            unplayedBlurRadius = 30f,
            activeLineScale = 1.1f
        )

        val NoEffects = KaraokeConfig(
            characterFloatOffset = 0f,
            characterMaxScale = 1f,
            unplayedBlurRadius = 0f,
            activeLineScale = 1f,
            characterBlurRadius = 0f
        )
    }
}