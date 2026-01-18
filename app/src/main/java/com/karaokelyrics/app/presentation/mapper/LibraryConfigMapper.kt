package com.karaokelyrics.app.presentation.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karaokelyrics.app.domain.model.UserSettings
import com.kyrics.config.*
import javax.inject.Inject

/**
 * Maps app user settings to library configuration.
 * This maintains the decoupling between the app's settings model and the library's configuration.
 */
class LibraryConfigMapper @Inject constructor() {

    /**
     * Convert user settings to library configuration.
     */
    fun mapToLibraryConfig(userSettings: UserSettings): KyricsConfig = KyricsConfig(
        visual = mapVisualConfig(userSettings),
        animation = mapAnimationConfig(userSettings),
        layout = mapLayoutConfig(userSettings),
        effects = mapEffectsConfig(userSettings),
        behavior = mapBehaviorConfig(userSettings)
    )

    private fun mapVisualConfig(userSettings: UserSettings): VisualConfig {
        val primaryColor = Color(userSettings.lyricsColorArgb)

        return VisualConfig(
            // Map colors properly from user settings
            playingTextColor = primaryColor, // Active singing color
            playedTextColor = primaryColor.copy(alpha = 0.7f), // Slightly faded for sung text
            upcomingTextColor = primaryColor.copy(alpha = 0.4f), // More faded for upcoming text
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

            // Disable effects by default - let user enable them if needed
            gradientEnabled = false,
            colors = ColorConfig(
                sung = primaryColor.copy(alpha = 0.7f),
                unsung = primaryColor.copy(alpha = 0.4f),
                active = primaryColor
            ),
            gradientAngle = 45f
        )
    }

    private fun mapAnimationConfig(userSettings: UserSettings): AnimationConfig = AnimationConfig(
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

    private fun mapLayoutConfig(userSettings: UserSettings): LayoutConfig = LayoutConfig(
        // Viewer configuration - use smooth scroll for app
        viewerConfig = ViewerConfig(
            type = ViewerType.SMOOTH_SCROLL,
            scrollPosition = 0.33f // Position active line at top third
        ),

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

        // Container - optimized for full-screen app
        containerPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        maxLineWidth = null,

        // Text direction - auto detect
        forceTextDirection = null
    )

    private fun mapEffectsConfig(userSettings: UserSettings): EffectsConfig = EffectsConfig(
        // Blur effects - only apply to upcoming/unplayed lines
        enableBlur = userSettings.enableBlurEffect,
        blurIntensity = 1.0f,
        playedLineBlur = 0.dp,
        upcomingLineBlur = 3.dp,
        distantLineBlur = 6.dp,

        // Shadow effects - subtle shadow for readability
        enableShadows = true,
        textShadowColor = Color.Black.copy(alpha = 0.3f),
        textShadowOffset = androidx.compose.ui.geometry.Offset(2f, 2f),
        textShadowRadius = 4f,

        // Opacity - clear for playing/played, slightly reduced for upcoming
        playingLineOpacity = 1f,
        playedLineOpacity = 0.9f,
        upcomingLineOpacity = 0.5f,
        distantLineOpacity = 0.3f
    )

    private fun mapBehaviorConfig(userSettings: UserSettings): BehaviorConfig = BehaviorConfig(
        // Scrolling - auto scroll to top
        scrollBehavior = ScrollBehavior.SMOOTH_TOP,
        scrollAnimationDuration = 500,
        scrollOffset = 50.dp,

        // Interaction - enable basic interaction
        enableLineClick = true
    )

    /**
     * Create a preset configuration based on user preference.
     */
    fun getPresetConfig(presetName: String): KyricsConfig = when (presetName.lowercase()) {
        "minimal" -> KyricsConfig.Minimal
        "dramatic" -> KyricsConfig.Dramatic
        else -> KyricsConfig.Default
    }
}
