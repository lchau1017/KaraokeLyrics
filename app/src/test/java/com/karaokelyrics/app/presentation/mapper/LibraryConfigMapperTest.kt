package com.karaokelyrics.app.presentation.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.common.truth.Truth.assertThat
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.UserSettings
import com.kyrics.config.KyricsConfig
import com.kyrics.config.KyricsPresets
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for LibraryConfigMapper.
 * Verifies correct mapping from app UserSettings to library KyricsConfig.
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
    }

    // ==================== Config Creation Tests ====================

    @Test
    fun `mapToLibraryConfig creates config for each font size`() {
        FontSize.values().forEach { fontSize ->
            val settings = UserSettings(fontSize = fontSize)
            val result = mapper.mapToLibraryConfig(settings)
            assertThat(result).isNotNull()
        }
    }

    @Test
    fun `mapToLibraryConfig creates config for dark mode`() {
        val darkSettings = UserSettings(
            isDarkMode = true,
            darkLyricsColorArgb = Color.Yellow.toArgb(),
            darkBackgroundColorArgb = Color.Black.toArgb()
        )

        val result = mapper.mapToLibraryConfig(darkSettings)

        assertThat(result).isNotNull()
    }

    @Test
    fun `mapToLibraryConfig creates config for light mode`() {
        val lightSettings = UserSettings(
            isDarkMode = false,
            lightLyricsColorArgb = Color.Blue.toArgb(),
            lightBackgroundColorArgb = Color.White.toArgb()
        )

        val result = mapper.mapToLibraryConfig(lightSettings)

        assertThat(result).isNotNull()
    }

    // ==================== Feature Toggle Tests ====================

    @Test
    fun `mapToLibraryConfig creates config with blur enabled`() {
        val settings = UserSettings(enableBlurEffect = true)

        val result = mapper.mapToLibraryConfig(settings)

        assertThat(result).isNotNull()
    }

    @Test
    fun `mapToLibraryConfig creates config with blur disabled`() {
        val settings = UserSettings(enableBlurEffect = false)

        val result = mapper.mapToLibraryConfig(settings)

        assertThat(result).isNotNull()
    }

    // ==================== Preset Config Tests ====================

    @Test
    fun `getPresetConfig returns Default for unknown preset`() {
        val result = mapper.getPresetConfig("unknown_preset")

        assertThat(result).isEqualTo(KyricsConfig.Default)
    }

    @Test
    fun `getPresetConfig returns Classic for classic preset`() {
        val result = mapper.getPresetConfig("classic")

        assertThat(result).isEqualTo(KyricsPresets.Classic)
    }

    @Test
    fun `getPresetConfig returns Neon for neon preset`() {
        val result = mapper.getPresetConfig("neon")

        assertThat(result).isEqualTo(KyricsPresets.Neon)
    }

    @Test
    fun `getPresetConfig is case insensitive`() {
        val lowerResult = mapper.getPresetConfig("classic")
        val upperResult = mapper.getPresetConfig("CLASSIC")
        val mixedResult = mapper.getPresetConfig("Classic")

        assertThat(lowerResult).isEqualTo(KyricsPresets.Classic)
        assertThat(upperResult).isEqualTo(KyricsPresets.Classic)
        assertThat(mixedResult).isEqualTo(KyricsPresets.Classic)
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

        assertThat(result).isNotNull()
    }

    @Test
    fun `mapToLibraryConfig handles all disabled settings`() {
        val settings = UserSettings(
            enableAnimations = false,
            enableBlurEffect = false,
            enableCharacterAnimations = false
        )

        val result = mapper.mapToLibraryConfig(settings)

        assertThat(result).isNotNull()
    }

    // ==================== Different configs for different settings ====================

    @Test
    fun `mapToLibraryConfig produces different configs for different colors`() {
        val settings1 = UserSettings(
            isDarkMode = true,
            darkLyricsColorArgb = Color.Red.toArgb()
        )
        val settings2 = UserSettings(
            isDarkMode = true,
            darkLyricsColorArgb = Color.Blue.toArgb()
        )

        val result1 = mapper.mapToLibraryConfig(settings1)
        val result2 = mapper.mapToLibraryConfig(settings2)

        assertThat(result1).isNotEqualTo(result2)
    }
}
