package com.karaokelyrics.app.presentation.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.common.truth.Truth.assertThat
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for LibraryConfigMapper.
 * Verifies correct mapping from app UserSettings to library KaraokeLibraryConfig.
 */
class LibraryConfigMapperTest {

    private lateinit var mapper: LibraryConfigMapper

    @Before
    fun setup() {
        mapper = LibraryConfigMapper()
    }

    // ==================== Default Settings Tests ====================

    @Test
    fun `mapToLibraryConfig with default settings returns valid config`() {
        val defaultSettings = UserSettings()

        val result = mapper.mapToLibraryConfig(defaultSettings)

        assertThat(result).isNotNull()
        assertThat(result.visual).isNotNull()
        assertThat(result.animation).isNotNull()
        assertThat(result.layout).isNotNull()
        assertThat(result.effects).isNotNull()
        assertThat(result.behavior).isNotNull()
    }

    // ==================== Visual Config Tests ====================

    @Test
    fun `mapToLibraryConfig maps fontSize correctly`() {
        val smallSettings = UserSettings(fontSize = FontSize.SMALL)
        val largeSettings = UserSettings(fontSize = FontSize.LARGE)
        val extraLargeSettings = UserSettings(fontSize = FontSize.EXTRA_LARGE)

        val smallResult = mapper.mapToLibraryConfig(smallSettings)
        val largeResult = mapper.mapToLibraryConfig(largeSettings)
        val extraLargeResult = mapper.mapToLibraryConfig(extraLargeSettings)

        assertThat(smallResult.visual.fontSize.value).isEqualTo(FontSize.SMALL.sp.toFloat())
        assertThat(largeResult.visual.fontSize.value).isEqualTo(FontSize.LARGE.sp.toFloat())
        assertThat(extraLargeResult.visual.fontSize.value).isEqualTo(FontSize.EXTRA_LARGE.sp.toFloat())
    }

    @Test
    fun `mapToLibraryConfig maps dark mode colors when isDarkMode is true`() {
        val darkSettings = UserSettings(
            isDarkMode = true,
            darkLyricsColorArgb = Color.Yellow.toArgb(),
            darkBackgroundColorArgb = Color.Black.toArgb()
        )

        val result = mapper.mapToLibraryConfig(darkSettings)

        assertThat(result.visual.playingTextColor.toArgb()).isEqualTo(Color.Yellow.toArgb())
        assertThat(result.visual.backgroundColor.toArgb()).isEqualTo(Color.Black.toArgb())
    }

    @Test
    fun `mapToLibraryConfig maps light mode colors when isDarkMode is false`() {
        val lightSettings = UserSettings(
            isDarkMode = false,
            lightLyricsColorArgb = Color.Blue.toArgb(),
            lightBackgroundColorArgb = Color.White.toArgb()
        )

        val result = mapper.mapToLibraryConfig(lightSettings)

        assertThat(result.visual.playingTextColor.toArgb()).isEqualTo(Color.Blue.toArgb())
        assertThat(result.visual.backgroundColor.toArgb()).isEqualTo(Color.White.toArgb())
    }

    // ==================== Animation Config Tests ====================

    @Test
    fun `mapToLibraryConfig maps enableAnimations correctly`() {
        val enabledSettings = UserSettings(enableAnimations = true)
        val disabledSettings = UserSettings(enableAnimations = false)

        val enabledResult = mapper.mapToLibraryConfig(enabledSettings)
        val disabledResult = mapper.mapToLibraryConfig(disabledSettings)

        assertThat(enabledResult.animation.enableLineAnimations).isTrue()
        assertThat(disabledResult.animation.enableLineAnimations).isFalse()
    }

    @Test
    fun `mapToLibraryConfig maps enableCharacterAnimations correctly`() {
        val enabledSettings = UserSettings(enableCharacterAnimations = true)
        val disabledSettings = UserSettings(enableCharacterAnimations = false)

        val enabledResult = mapper.mapToLibraryConfig(enabledSettings)
        val disabledResult = mapper.mapToLibraryConfig(disabledSettings)

        assertThat(enabledResult.animation.enableCharacterAnimations).isTrue()
        assertThat(disabledResult.animation.enableCharacterAnimations).isFalse()
    }

    // ==================== Effects Config Tests ====================

    @Test
    fun `mapToLibraryConfig maps enableBlurEffect correctly`() {
        val enabledSettings = UserSettings(enableBlurEffect = true)
        val disabledSettings = UserSettings(enableBlurEffect = false)

        val enabledResult = mapper.mapToLibraryConfig(enabledSettings)
        val disabledResult = mapper.mapToLibraryConfig(disabledSettings)

        assertThat(enabledResult.effects.enableBlur).isTrue()
        assertThat(disabledResult.effects.enableBlur).isFalse()
    }

    @Test
    fun `mapToLibraryConfig default blur is disabled`() {
        val defaultSettings = UserSettings()

        val result = mapper.mapToLibraryConfig(defaultSettings)

        assertThat(result.effects.enableBlur).isFalse()
    }

    @Test
    fun `mapToLibraryConfig sets shadow enabled by default`() {
        val settings = UserSettings()

        val result = mapper.mapToLibraryConfig(settings)

        assertThat(result.effects.enableShadows).isTrue()
    }

    // ==================== Preset Config Tests ====================

    @Test
    fun `getPresetConfig returns Default for unknown preset`() {
        val result = mapper.getPresetConfig("unknown_preset")

        assertThat(result).isEqualTo(KaraokeLibraryConfig.Default)
    }

    @Test
    fun `getPresetConfig returns Minimal for minimal preset`() {
        val result = mapper.getPresetConfig("minimal")

        assertThat(result).isEqualTo(KaraokeLibraryConfig.Minimal)
    }

    @Test
    fun `getPresetConfig returns Dramatic for dramatic preset`() {
        val result = mapper.getPresetConfig("dramatic")

        assertThat(result).isEqualTo(KaraokeLibraryConfig.Dramatic)
    }

    @Test
    fun `getPresetConfig is case insensitive`() {
        val lowerResult = mapper.getPresetConfig("minimal")
        val upperResult = mapper.getPresetConfig("MINIMAL")
        val mixedResult = mapper.getPresetConfig("Minimal")

        assertThat(lowerResult).isEqualTo(KaraokeLibraryConfig.Minimal)
        assertThat(upperResult).isEqualTo(KaraokeLibraryConfig.Minimal)
        assertThat(mixedResult).isEqualTo(KaraokeLibraryConfig.Minimal)
    }

    // ==================== Combined Settings Tests ====================

    @Test
    fun `mapToLibraryConfig handles all settings combined`() {
        val settings = UserSettings(
            isDarkMode = true,
            darkLyricsColorArgb = Color.Cyan.toArgb(),
            darkBackgroundColorArgb = Color.DarkGray.toArgb(),
            fontSize = FontSize.LARGE,
            enableAnimations = true,
            enableBlurEffect = true,
            enableCharacterAnimations = true
        )

        val result = mapper.mapToLibraryConfig(settings)

        // Visual
        assertThat(result.visual.playingTextColor.toArgb()).isEqualTo(Color.Cyan.toArgb())
        assertThat(result.visual.backgroundColor.toArgb()).isEqualTo(Color.DarkGray.toArgb())
        assertThat(result.visual.fontSize.value).isEqualTo(FontSize.LARGE.sp.toFloat())

        // Animation
        assertThat(result.animation.enableLineAnimations).isTrue()
        assertThat(result.animation.enableCharacterAnimations).isTrue()

        // Effects
        assertThat(result.effects.enableBlur).isTrue()
    }

    @Test
    fun `mapToLibraryConfig handles all disabled settings`() {
        val settings = UserSettings(
            enableAnimations = false,
            enableBlurEffect = false,
            enableCharacterAnimations = false
        )

        val result = mapper.mapToLibraryConfig(settings)

        assertThat(result.animation.enableLineAnimations).isFalse()
        assertThat(result.animation.enableCharacterAnimations).isFalse()
        assertThat(result.effects.enableBlur).isFalse()
    }

    // ==================== Opacity Tests ====================

    @Test
    fun `mapToLibraryConfig sets correct opacity values`() {
        val settings = UserSettings()

        val result = mapper.mapToLibraryConfig(settings)

        assertThat(result.effects.playingLineOpacity).isEqualTo(1f)
        assertThat(result.effects.playedLineOpacity).isGreaterThan(0f)
        assertThat(result.effects.upcomingLineOpacity).isGreaterThan(0f)
        assertThat(result.effects.distantLineOpacity).isGreaterThan(0f)
    }

    // ==================== Color Config Tests ====================

    @Test
    fun `mapToLibraryConfig creates color config with proper alpha variations`() {
        val primaryColor = Color.Green
        val settings = UserSettings(
            isDarkMode = true,
            darkLyricsColorArgb = primaryColor.toArgb()
        )

        val result = mapper.mapToLibraryConfig(settings)

        // Playing should be primary color
        assertThat(result.visual.playingTextColor.toArgb()).isEqualTo(primaryColor.toArgb())

        // Played should have reduced alpha
        assertThat(result.visual.playedTextColor.alpha).isLessThan(1f)

        // Upcoming should have reduced alpha
        assertThat(result.visual.upcomingTextColor.alpha).isLessThan(1f)
    }
}
