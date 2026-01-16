package com.karaokelyrics.app.presentation.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.ui.core.config.*
import javax.inject.Inject

/**
 * Maps app user settings to library configuration.
 * This maintains the decoupling between the app's settings model and the library's configuration.
 */
class LibraryConfigMapper @Inject constructor() {

    /**
     * Convert user settings to library configuration.
     */
    fun mapToLibraryConfig(userSettings: UserSettings): KaraokeLibraryConfig {
        return KaraokeLibraryConfig(
            visual = mapVisualConfig(userSettings),
            animation = mapAnimationConfig(userSettings),
            layout = mapLayoutConfig(userSettings),
            effects = mapEffectsConfig(userSettings),
            behavior = mapBehaviorConfig(userSettings)
        )
    }

    private fun mapVisualConfig(userSettings: UserSettings): VisualConfig {
        return VisualConfig(
            // Map colors - use defaults since UserSettings only has base colors
            playingTextColor = Color(userSettings.lyricsColorArgb),
            playedTextColor = Color(userSettings.lyricsColorArgb).copy(alpha = 0.9f),
            upcomingTextColor = Color.White.copy(alpha = 0.3f),
            accompanimentTextColor = Color(0xFFFFE082),

            // Map font settings
            fontSize = userSettings.fontSize.sp.sp,
            accompanimentFontSize = (userSettings.fontSize.sp * 0.6f).sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.sp,

            // Map alignment - default center
            textAlign = TextAlign.Center,

            // Map background
            backgroundColor = Color(userSettings.backgroundColorArgb),

            // Map gradient settings - use defaults
            enableGradients = true,
            playingGradientColors = listOf(Color(0xFF00BCD4), Color(0xFFE91E63)),
            gradientAngle = 45f
        )
    }

    private fun mapAnimationConfig(userSettings: UserSettings): AnimationConfig {
        return AnimationConfig(
            // Character animations
            enableCharacterAnimations = userSettings.enableCharacterAnimations,
            characterAnimationDuration = 800f,
            characterMaxScale = 1.15f,
            characterFloatOffset = 6f,
            characterRotationDegrees = 3f,

            // Line animations
            enableLineAnimations = userSettings.enableAnimations,
            lineScaleOnPlay = 1.05f,
            lineAnimationDuration = 700f,

            // Transition animations
            fadeInDuration = 300f,
            fadeOutDuration = 500f
        )
    }

    private fun mapLayoutConfig(userSettings: UserSettings): LayoutConfig {
        return LayoutConfig(
            // Spacing - use defaults
            linePadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 24.dp,
                vertical = 12.dp
            ),
            lineSpacing = 12.dp,
            wordSpacing = 4.dp,
            characterSpacing = 0.dp,

            // Line height - use defaults
            lineHeightMultiplier = 1.2f,
            accompanimentLineHeightMultiplier = 1.0f,

            // Container - use defaults
            containerPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            maxLineWidth = null,

            // Text direction - auto detect
            forceTextDirection = null
        )
    }

    private fun mapEffectsConfig(userSettings: UserSettings): EffectsConfig {
        return EffectsConfig(
            // Blur effects - only apply to upcoming/unplayed lines
            enableBlur = userSettings.enableBlurEffect,
            blurIntensity = 1.0f,  // Moderate intensity for readability
            playedLineBlur = 0.dp,  // No blur for played lines
            upcomingLineBlur = 3.dp,  // Light blur for upcoming lines
            distantLineBlur = 6.dp,  // Medium blur for distant lines

            // Shadow effects - use defaults
            enableShadows = true,
            textShadowColor = Color.Black.copy(alpha = 0.3f),
            textShadowOffset = androidx.compose.ui.geometry.Offset(2f, 2f),
            textShadowRadius = 4f,

            // Glow effects - disabled by default
            enableGlow = false,
            glowColor = Color.White,
            glowRadius = 8f,

            // Opacity - clear for playing/played, slightly reduced for upcoming
            playingLineOpacity = 1f,      // Full opacity for current line
            playedLineOpacity = 0.8f,      // Good visibility for played lines
            upcomingLineOpacity = 0.7f,    // Good visibility with light blur for upcoming
            distantLineOpacity = 0.5f     // Moderate transparency for distant lines
        )
    }

    private fun mapBehaviorConfig(userSettings: UserSettings): BehaviorConfig {
        return BehaviorConfig(
            // Scrolling - auto scroll to top
            scrollBehavior = ScrollBehavior.SMOOTH_TOP,
            scrollAnimationDuration = 500,
            scrollOffset = 50.dp,

            // Interaction - enable basic interaction
            enableLineClick = true,
            enableLineLongPress = false,
            enableSwipeGestures = false,

            // Performance - use defaults
            preloadLines = 5,
            recycleDistance = 10
        )
    }

    private fun mapTextAlignment(alignment: String): TextAlign {
        return when (alignment.lowercase()) {
            "left", "start" -> TextAlign.Start
            "right", "end" -> TextAlign.End
            "center" -> TextAlign.Center
            "justify" -> TextAlign.Justify
            else -> TextAlign.Center
        }
    }

    private fun mapScrollBehavior(behavior: String): ScrollBehavior {
        return when (behavior.lowercase()) {
            "none" -> ScrollBehavior.NONE
            "smooth_center" -> ScrollBehavior.SMOOTH_CENTER
            "smooth_top" -> ScrollBehavior.SMOOTH_TOP
            "instant_center" -> ScrollBehavior.INSTANT_CENTER
            "paged" -> ScrollBehavior.PAGED
            else -> ScrollBehavior.SMOOTH_CENTER
        }
    }

    private fun parseColor(colorString: String?, defaultColor: Color): Color {
        if (colorString == null) return defaultColor

        return try {
            when {
                colorString.startsWith("#") -> {
                    // Parse hex color
                    val hex = colorString.removePrefix("#")
                    when (hex.length) {
                        6 -> Color(android.graphics.Color.parseColor("#$hex"))
                        8 -> Color(android.graphics.Color.parseColor("#$hex"))
                        else -> defaultColor
                    }
                }
                colorString.startsWith("0x") || colorString.startsWith("0X") -> {
                    // Parse as long
                    Color(colorString.substring(2).toLong(16).toInt())
                }
                else -> defaultColor
            }
        } catch (e: Exception) {
            defaultColor
        }
    }

    /**
     * Create a preset configuration based on user preference.
     */
    fun getPresetConfig(presetName: String): KaraokeLibraryConfig {
        return when (presetName.lowercase()) {
            "minimal" -> KaraokeLibraryConfig.Minimal
            "dramatic" -> KaraokeLibraryConfig.Dramatic
            else -> KaraokeLibraryConfig.Default
        }
    }
}