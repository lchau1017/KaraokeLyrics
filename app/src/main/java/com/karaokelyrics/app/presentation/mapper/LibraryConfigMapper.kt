package com.karaokelyrics.app.presentation.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karaokelyrics.app.domain.model.UserSettings
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
            // Map colors
            playingTextColor = parseColor(userSettings.playingTextColor, Color.White),
            playedTextColor = parseColor(userSettings.playedTextColor, Color.Gray),
            upcomingTextColor = parseColor(userSettings.upcomingTextColor, Color.White.copy(alpha = 0.8f)),
            accompanimentTextColor = parseColor(userSettings.accompanimentTextColor, Color(0xFFFFE082)),

            // Map font settings
            fontSize = userSettings.fontSize.sp,
            accompanimentFontSize = (userSettings.fontSize * 0.6f).sp,
            fontWeight = if (userSettings.boldText) FontWeight.Bold else FontWeight.Normal,
            letterSpacing = userSettings.letterSpacing.sp,

            // Map alignment
            textAlign = mapTextAlignment(userSettings.textAlignment),

            // Map background
            backgroundColor = parseColor(userSettings.backgroundColor, Color.Transparent),

            // Map gradient settings
            enableGradients = userSettings.enableGradients,
            playingGradientColors = if (userSettings.enableGradients && userSettings.gradientColors.size >= 2) {
                userSettings.gradientColors.map { parseColor(it, Color.White) }
            } else {
                listOf(Color(0xFF00BCD4), Color(0xFFE91E63))
            },
            gradientAngle = userSettings.gradientAngle
        )
    }

    private fun mapAnimationConfig(userSettings: UserSettings): AnimationConfig {
        return AnimationConfig(
            // Character animations
            enableCharacterAnimations = userSettings.enableCharacterAnimations,
            characterAnimationDuration = userSettings.characterAnimationDuration,
            characterMaxScale = userSettings.characterMaxScale,
            characterFloatOffset = userSettings.characterFloatOffset,
            characterRotationDegrees = userSettings.characterRotationDegrees,

            // Line animations
            enableLineAnimations = userSettings.enableLineAnimations,
            lineScaleOnPlay = userSettings.lineScaleOnPlay,
            lineAnimationDuration = userSettings.lineAnimationDuration,

            // Transition animations
            fadeInDuration = userSettings.fadeInDuration,
            fadeOutDuration = userSettings.fadeOutDuration
        )
    }

    private fun mapLayoutConfig(userSettings: UserSettings): LayoutConfig {
        return LayoutConfig(
            // Spacing
            linePadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = userSettings.linePaddingHorizontal.dp,
                vertical = userSettings.linePaddingVertical.dp
            ),
            lineSpacing = userSettings.lineSpacing.dp,
            wordSpacing = userSettings.wordSpacing.dp,
            characterSpacing = userSettings.characterSpacing.dp,

            // Line height
            lineHeightMultiplier = userSettings.lineHeightMultiplier,
            accompanimentLineHeightMultiplier = userSettings.accompanimentLineHeightMultiplier,

            // Container
            containerPadding = androidx.compose.foundation.layout.PaddingValues(
                userSettings.containerPadding.dp
            ),
            maxLineWidth = if (userSettings.maxLineWidth > 0) {
                userSettings.maxLineWidth.dp
            } else null,

            // Text direction
            forceTextDirection = when (userSettings.forceTextDirection) {
                "rtl" -> androidx.compose.ui.unit.LayoutDirection.Rtl
                "ltr" -> androidx.compose.ui.unit.LayoutDirection.Ltr
                else -> null
            }
        )
    }

    private fun mapEffectsConfig(userSettings: UserSettings): EffectsConfig {
        return EffectsConfig(
            // Blur effects
            enableBlur = userSettings.enableBlur,
            blurIntensity = userSettings.blurIntensity,
            playedLineBlur = userSettings.playedLineBlur.dp,
            upcomingLineBlur = userSettings.upcomingLineBlur.dp,
            distantLineBlur = userSettings.distantLineBlur.dp,

            // Shadow effects
            enableShadows = userSettings.enableShadows,
            textShadowColor = parseColor(userSettings.textShadowColor, Color.Black.copy(alpha = 0.3f)),
            textShadowOffset = androidx.compose.ui.geometry.Offset(
                userSettings.textShadowOffsetX,
                userSettings.textShadowOffsetY
            ),
            textShadowRadius = userSettings.textShadowRadius,

            // Glow effects
            enableGlow = userSettings.enableGlow,
            glowColor = parseColor(userSettings.glowColor, Color.White),
            glowRadius = userSettings.glowRadius,

            // Opacity
            playingLineOpacity = userSettings.playingLineOpacity,
            playedLineOpacity = userSettings.playedLineOpacity,
            upcomingLineOpacity = userSettings.upcomingLineOpacity,
            distantLineOpacity = userSettings.distantLineOpacity
        )
    }

    private fun mapBehaviorConfig(userSettings: UserSettings): BehaviorConfig {
        return BehaviorConfig(
            // Scrolling
            scrollBehavior = mapScrollBehavior(userSettings.scrollBehavior),
            scrollAnimationDuration = userSettings.scrollAnimationDuration,
            scrollOffset = userSettings.scrollOffset.dp,

            // Interaction
            enableLineClick = userSettings.enableLineClick,
            enableLineLongPress = userSettings.enableLineLongPress,
            enableSwipeGestures = userSettings.enableSwipeGestures,

            // Performance
            preloadLines = userSettings.preloadLines,
            recycleDistance = userSettings.recycleDistance
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