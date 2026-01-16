package com.karaokelyrics.ui.core.config

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Predefined configurations showcasing different effects and animations.
 * Each preset demonstrates specific features of the karaoke library.
 */
object LibraryPresets {

    /**
     * Classic karaoke style - simple and clean
     */
    val Classic = KaraokeLibraryConfig(
        visual = VisualConfig(
            playingTextColor = Color.Yellow,
            playedTextColor = Color.Green,
            upcomingTextColor = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            gradientEnabled = false,
            shadowEnabled = true,
            shadowColor = Color.Black.copy(alpha = 0.3f),
            glowEnabled = false
        ),
        animation = AnimationConfig(
            enableCharacterAnimations = false,
            enableLineAnimations = true,
            lineScaleOnPlay = 1.05f
        ),
        effects = EffectsConfig(
            enableBlur = false
        )
    )

    /**
     * Neon style with gradient and glow effects
     */
    val Neon = KaraokeLibraryConfig(
        visual = VisualConfig(
            playingTextColor = Color.Cyan,
            playedTextColor = Color.Magenta,
            upcomingTextColor = Color.White.copy(alpha = 0.6f),
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            gradientEnabled = true,
            gradientAngle = 45f,
            colors = ColorConfig(
                sung = Color.Magenta,
                unsung = Color.Cyan,
                active = Color.Yellow
            ),
            shadowEnabled = true,
            shadowColor = Color.Black,
            shadowOffset = Offset(3f, 3f),
            glowEnabled = true,
            glowColor = Color.Magenta
        ),
        animation = AnimationConfig(
            enableCharacterAnimations = true,
            characterMaxScale = 1.2f,
            characterFloatOffset = 8f,
            enableLineAnimations = true
        ),
        effects = EffectsConfig(
            enableBlur = true,
            blurIntensity = 0.8f
        )
    )

    /**
     * Rainbow gradient style
     */
    val Rainbow = KaraokeLibraryConfig(
        visual = VisualConfig(
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            gradientEnabled = true,
            gradientAngle = 0f,  // Horizontal gradient
            colors = ColorConfig(
                sung = Color.Red,
                unsung = Color.Blue,
                active = Color.Green
            ),
            shadowEnabled = false,
            glowEnabled = true,
            glowColor = Color.Yellow.copy(alpha = 0.4f),
            playingGradientColors = listOf(
                Color.Red,
                Color(0xFFFFA500), // Orange
                Color.Yellow,
                Color.Green,
                Color.Blue,
                Color(0xFF4B0082), // Indigo
                Color(0xFF8B00FF)  // Violet
            )
        ),
        animation = AnimationConfig(
            enableCharacterAnimations = true,
            characterMaxScale = 1.3f,
            characterRotationDegrees = 5f
        )
    )

    /**
     * Fire effect style with warm colors
     */
    val Fire = KaraokeLibraryConfig(
        visual = VisualConfig(
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            gradientEnabled = true,
            gradientAngle = 90f,  // Vertical gradient (fire goes up)
            colors = ColorConfig(
                sung = Color(0xFFFF4500),  // Orange red
                unsung = Color(0xFFFFD700), // Gold
                active = Color(0xFFFF6347)  // Tomato
            ),
            shadowEnabled = true,
            shadowColor = Color(0xFF8B0000),  // Dark red
            shadowOffset = Offset(2f, 4f),
            glowEnabled = true,
            glowColor = Color(0xFFFFA500)  // Orange glow
        ),
        animation = AnimationConfig(
            enableCharacterAnimations = true,
            characterMaxScale = 1.25f,
            characterFloatOffset = 10f,  // Characters "flicker" like fire
            characterAnimationDuration = 600f
        ),
        effects = EffectsConfig(
            enableBlur = true,
            blurIntensity = 0.5f
        )
    )

    /**
     * Ocean/Water style with cool colors
     */
    val Ocean = KaraokeLibraryConfig(
        visual = VisualConfig(
            fontSize = 33.sp,
            fontWeight = FontWeight.Medium,
            gradientEnabled = true,
            gradientAngle = 135f,
            colors = ColorConfig(
                sung = Color(0xFF006994),  // Deep blue
                unsung = Color(0xFF00CED1), // Dark turquoise
                active = Color(0xFF00FFFF)  // Aqua
            ),
            shadowEnabled = true,
            shadowColor = Color(0xFF000080),  // Navy
            shadowOffset = Offset(1f, 2f),
            glowEnabled = true,
            glowColor = Color(0xFF87CEEB)  // Sky blue glow
        ),
        animation = AnimationConfig(
            enableCharacterAnimations = true,
            characterMaxScale = 1.15f,
            characterFloatOffset = 12f,  // Wave-like motion
            characterAnimationDuration = 1200f,  // Slower, wave-like
            characterRotationDegrees = 2f
        )
    )

    /**
     * Retro 80s style
     */
    val Retro = KaraokeLibraryConfig(
        visual = VisualConfig(
            fontSize = 38.sp,
            fontWeight = FontWeight.Black,
            gradientEnabled = true,
            gradientAngle = 45f,
            colors = ColorConfig(
                sung = Color(0xFFFF1493),  // Deep pink
                unsung = Color(0xFF00FFFF), // Cyan
                active = Color(0xFFFFFF00)  // Yellow
            ),
            shadowEnabled = true,
            shadowColor = Color.Black,
            shadowOffset = Offset(4f, 4f),
            glowEnabled = true,
            glowColor = Color(0xFFFF1493)
        ),
        animation = AnimationConfig(
            enableCharacterAnimations = true,
            characterMaxScale = 1.4f,
            characterRotationDegrees = 8f,
            enableLineAnimations = true,
            lineScaleOnPlay = 1.1f
        ),
        effects = EffectsConfig(
            enableBlur = false  // Sharp, retro look
        )
    )

    /**
     * Minimal style - clean and simple
     */
    val Minimal = KaraokeLibraryConfig(
        visual = VisualConfig(
            playingTextColor = Color.Black,
            playedTextColor = Color.Gray,
            upcomingTextColor = Color.LightGray,
            fontSize = 30.sp,
            fontWeight = FontWeight.Normal,
            gradientEnabled = false,
            shadowEnabled = false,
            glowEnabled = false,
            backgroundColor = Color.White
        ),
        animation = AnimationConfig(
            enableCharacterAnimations = false,
            enableLineAnimations = false
        ),
        effects = EffectsConfig(
            enableBlur = false
        )
    )

    /**
     * Elegant style with subtle effects
     */
    val Elegant = KaraokeLibraryConfig(
        visual = VisualConfig(
            playingTextColor = Color(0xFFFFD700),  // Gold
            playedTextColor = Color(0xFFC0C0C0),   // Silver
            upcomingTextColor = Color(0xFFF5F5DC),  // Beige
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            gradientEnabled = true,
            gradientAngle = 180f,  // Top to bottom
            colors = ColorConfig(
                sung = Color(0xFFC0C0C0),
                unsung = Color(0xFFF5F5DC),
                active = Color(0xFFFFD700)
            ),
            shadowEnabled = true,
            shadowColor = Color.Black.copy(alpha = 0.2f),
            shadowOffset = Offset(1f, 1f),
            glowEnabled = false
        ),
        animation = AnimationConfig(
            enableCharacterAnimations = true,
            characterMaxScale = 1.08f,  // Subtle scale
            characterFloatOffset = 3f,   // Gentle movement
            characterAnimationDuration = 1000f,
            enableLineAnimations = true,
            lineScaleOnPlay = 1.02f
        ),
        effects = EffectsConfig(
            enableBlur = true,
            blurIntensity = 0.3f  // Very subtle blur
        )
    )

    /**
     * Party mode with all effects maxed out
     */
    val Party = KaraokeLibraryConfig(
        visual = VisualConfig(
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            gradientEnabled = true,
            gradientAngle = 0f,
            colors = ColorConfig(
                sung = Color.Green,
                unsung = Color.Magenta,
                active = Color.Yellow
            ),
            shadowEnabled = true,
            shadowColor = Color.Red,
            shadowOffset = Offset(5f, 5f),
            glowEnabled = true,
            glowColor = Color.Cyan,
            playingGradientColors = listOf(
                Color.Red, Color.Yellow, Color.Green,
                Color.Cyan, Color.Blue, Color.Magenta
            )
        ),
        animation = AnimationConfig(
            enableCharacterAnimations = true,
            characterMaxScale = 1.5f,
            characterFloatOffset = 15f,
            characterRotationDegrees = 10f,
            characterAnimationDuration = 500f,
            enableLineAnimations = true,
            lineScaleOnPlay = 1.2f,
            lineAnimationDuration = 400f
        ),
        effects = EffectsConfig(
            enableBlur = true,
            blurIntensity = 1.0f,
            upcomingLineBlur = 5.dp
        )
    )

    /**
     * Matrix/Cyber style
     */
    val Matrix = KaraokeLibraryConfig(
        visual = VisualConfig(
            playingTextColor = Color(0xFF00FF00),  // Bright green
            playedTextColor = Color(0xFF008000),   // Dark green
            upcomingTextColor = Color(0xFF00FF00).copy(alpha = 0.3f),
            fontSize = 28.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            gradientEnabled = true,
            gradientAngle = 270f,  // Bottom to top
            colors = ColorConfig(
                sung = Color(0xFF008000),
                unsung = Color(0xFF00FF00).copy(alpha = 0.5f),
                active = Color(0xFF00FF00)
            ),
            shadowEnabled = false,
            glowEnabled = true,
            glowColor = Color(0xFF00FF00).copy(alpha = 0.6f),
            backgroundColor = Color.Black
        ),
        animation = AnimationConfig(
            enableCharacterAnimations = true,
            characterMaxScale = 1.1f,
            characterFloatOffset = 5f,
            characterAnimationDuration = 300f,  // Fast, digital feel
            enableLineAnimations = false
        ),
        effects = EffectsConfig(
            enableBlur = true,
            blurIntensity = 0.4f
        )
    )

    /**
     * Get all presets as a list
     */
    val allPresets = listOf(
        "Classic" to Classic,
        "Neon" to Neon,
        "Rainbow" to Rainbow,
        "Fire" to Fire,
        "Ocean" to Ocean,
        "Retro" to Retro,
        "Minimal" to Minimal,
        "Elegant" to Elegant,
        "Party" to Party,
        "Matrix" to Matrix
    )
}