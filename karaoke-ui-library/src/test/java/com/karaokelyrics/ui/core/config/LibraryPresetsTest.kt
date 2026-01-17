package com.karaokelyrics.ui.core.config

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for LibraryPresets.
 * Verifies all preset configurations are valid and consistent.
 */
class LibraryPresetsTest {

    // ==================== Preset Existence Tests ====================

    @Test
    fun `Classic preset exists and is valid`() {
        val preset = LibraryPresets.Classic

        assertThat(preset).isNotNull()
        assertThat(preset.visual).isNotNull()
        assertThat(preset.animation).isNotNull()
        assertThat(preset.effects).isNotNull()
    }

    @Test
    fun `Neon preset exists and is valid`() {
        val preset = LibraryPresets.Neon

        assertThat(preset).isNotNull()
        assertThat(preset.visual.gradientEnabled).isTrue()
    }

    @Test
    fun `Rainbow preset exists and is valid`() {
        val preset = LibraryPresets.Rainbow

        assertThat(preset).isNotNull()
        assertThat(preset.visual.gradientEnabled).isTrue()
        assertThat(preset.visual.playingGradientColors).isNotEmpty()
    }

    @Test
    fun `Fire preset exists and is valid`() {
        val preset = LibraryPresets.Fire

        assertThat(preset).isNotNull()
        assertThat(preset.visual.gradientEnabled).isTrue()
    }

    @Test
    fun `Ocean preset exists and is valid`() {
        val preset = LibraryPresets.Ocean

        assertThat(preset).isNotNull()
        assertThat(preset.visual.gradientEnabled).isTrue()
    }

    @Test
    fun `Retro preset exists and is valid`() {
        val preset = LibraryPresets.Retro

        assertThat(preset).isNotNull()
        assertThat(preset.visual.gradientEnabled).isTrue()
    }

    @Test
    fun `Minimal preset exists and is valid`() {
        val preset = LibraryPresets.Minimal

        assertThat(preset).isNotNull()
        assertThat(preset.visual.gradientEnabled).isFalse()
        assertThat(preset.animation.enableCharacterAnimations).isFalse()
        assertThat(preset.animation.enableLineAnimations).isFalse()
    }

    @Test
    fun `Elegant preset exists and is valid`() {
        val preset = LibraryPresets.Elegant

        assertThat(preset).isNotNull()
        assertThat(preset.visual.gradientEnabled).isTrue()
    }

    @Test
    fun `Party preset exists and is valid`() {
        val preset = LibraryPresets.Party

        assertThat(preset).isNotNull()
        assertThat(preset.animation.enableCharacterAnimations).isTrue()
        assertThat(preset.animation.enableLineAnimations).isTrue()
        assertThat(preset.animation.enablePulse).isTrue()
    }

    @Test
    fun `Matrix preset exists and is valid`() {
        val preset = LibraryPresets.Matrix

        assertThat(preset).isNotNull()
        assertThat(preset.visual.gradientEnabled).isTrue()
    }

    // ==================== allPresets Tests ====================

    @Test
    fun `allPresets contains all 10 presets`() {
        val presets = LibraryPresets.allPresets

        assertThat(presets).hasSize(10)
    }

    @Test
    fun `allPresets has correct names`() {
        val names = LibraryPresets.allPresets.map { it.first }

        assertThat(names).containsExactly(
            "Classic", "Neon", "Rainbow", "Fire", "Ocean",
            "Retro", "Minimal", "Elegant", "Party", "Matrix"
        )
    }

    @Test
    fun `allPresets configs match individual presets`() {
        val presetsMap = LibraryPresets.allPresets.toMap()

        assertThat(presetsMap["Classic"]).isEqualTo(LibraryPresets.Classic)
        assertThat(presetsMap["Neon"]).isEqualTo(LibraryPresets.Neon)
        assertThat(presetsMap["Rainbow"]).isEqualTo(LibraryPresets.Rainbow)
        assertThat(presetsMap["Fire"]).isEqualTo(LibraryPresets.Fire)
        assertThat(presetsMap["Ocean"]).isEqualTo(LibraryPresets.Ocean)
        assertThat(presetsMap["Retro"]).isEqualTo(LibraryPresets.Retro)
        assertThat(presetsMap["Minimal"]).isEqualTo(LibraryPresets.Minimal)
        assertThat(presetsMap["Elegant"]).isEqualTo(LibraryPresets.Elegant)
        assertThat(presetsMap["Party"]).isEqualTo(LibraryPresets.Party)
        assertThat(presetsMap["Matrix"]).isEqualTo(LibraryPresets.Matrix)
    }

    // ==================== Preset Properties Tests ====================

    @Test
    fun `Classic preset has no gradient`() {
        val preset = LibraryPresets.Classic

        assertThat(preset.visual.gradientEnabled).isFalse()
    }

    @Test
    fun `Classic preset has no blur`() {
        val preset = LibraryPresets.Classic

        assertThat(preset.effects.enableBlur).isFalse()
    }

    @Test
    fun `Minimal preset has all effects disabled`() {
        val preset = LibraryPresets.Minimal

        assertThat(preset.visual.gradientEnabled).isFalse()
        assertThat(preset.animation.enableCharacterAnimations).isFalse()
        assertThat(preset.animation.enableLineAnimations).isFalse()
        assertThat(preset.effects.enableBlur).isFalse()
    }

    @Test
    fun `Party preset has maximum effects enabled`() {
        val preset = LibraryPresets.Party

        assertThat(preset.visual.gradientEnabled).isTrue()
        assertThat(preset.animation.enableCharacterAnimations).isTrue()
        assertThat(preset.animation.enableLineAnimations).isTrue()
        assertThat(preset.animation.enablePulse).isTrue()
        assertThat(preset.effects.enableBlur).isTrue()
    }

    @Test
    fun `Rainbow preset has 7 gradient colors`() {
        val preset = LibraryPresets.Rainbow

        assertThat(preset.visual.playingGradientColors).hasSize(7)
    }

    @Test
    fun `Neon preset has blur enabled`() {
        val preset = LibraryPresets.Neon

        assertThat(preset.effects.enableBlur).isTrue()
    }

    @Test
    fun `Fire preset has vertical gradient angle`() {
        val preset = LibraryPresets.Fire

        assertThat(preset.visual.gradientAngle).isEqualTo(90f)
    }

    // ==================== Animation Values Tests ====================

    @Test
    fun `Retro preset has highest character max scale`() {
        val preset = LibraryPresets.Retro

        assertThat(preset.animation.characterMaxScale).isEqualTo(1.4f)
    }

    @Test
    fun `Party preset has highest float offset`() {
        val preset = LibraryPresets.Party

        assertThat(preset.animation.characterFloatOffset).isEqualTo(15f)
    }

    @Test
    fun `Ocean preset has slowest animation duration`() {
        val preset = LibraryPresets.Ocean

        assertThat(preset.animation.characterAnimationDuration).isEqualTo(1200f)
    }

    @Test
    fun `Matrix preset has fastest animation duration`() {
        val preset = LibraryPresets.Matrix

        assertThat(preset.animation.characterAnimationDuration).isEqualTo(300f)
    }

    // ==================== Font Configuration Tests ====================

    @Test
    fun `Matrix preset uses monospace font`() {
        val preset = LibraryPresets.Matrix

        assertThat(preset.visual.fontFamily).isEqualTo(androidx.compose.ui.text.font.FontFamily.Monospace)
    }

    @Test
    fun `Party preset has largest font size`() {
        val preset = LibraryPresets.Party

        assertThat(preset.visual.fontSize.value).isEqualTo(40f)
    }

    @Test
    fun `Matrix preset has smallest font size`() {
        val preset = LibraryPresets.Matrix

        assertThat(preset.visual.fontSize.value).isEqualTo(28f)
    }

    // ==================== Blur Configuration Tests ====================

    @Test
    fun `Party preset has maximum blur intensity`() {
        val preset = LibraryPresets.Party

        assertThat(preset.effects.blurIntensity).isEqualTo(1.0f)
    }

    @Test
    fun `Elegant preset has subtle blur`() {
        val preset = LibraryPresets.Elegant

        assertThat(preset.effects.enableBlur).isTrue()
        assertThat(preset.effects.blurIntensity).isEqualTo(0.3f)
    }

    @Test
    fun `Fire preset has medium blur`() {
        val preset = LibraryPresets.Fire

        assertThat(preset.effects.enableBlur).isTrue()
        assertThat(preset.effects.blurIntensity).isEqualTo(0.5f)
    }

    // ==================== Preset Consistency Tests ====================

    @Test
    fun `all presets have valid visual config`() {
        LibraryPresets.allPresets.forEach { (name, preset) ->
            assertThat(preset.visual.fontSize.value).isGreaterThan(0f)
            assertThat(preset.visual.fontWeight).isNotNull()
        }
    }

    @Test
    fun `all presets have valid animation config`() {
        LibraryPresets.allPresets.forEach { (name, preset) ->
            if (preset.animation.enableCharacterAnimations) {
                assertThat(preset.animation.characterMaxScale).isGreaterThan(1f)
                assertThat(preset.animation.characterAnimationDuration).isGreaterThan(0f)
            }
        }
    }

    @Test
    fun `all presets have valid effects config`() {
        LibraryPresets.allPresets.forEach { (name, preset) ->
            if (preset.effects.enableBlur) {
                assertThat(preset.effects.blurIntensity).isGreaterThan(0f)
            }
        }
    }

    @Test
    fun `presets with gradients have color config`() {
        LibraryPresets.allPresets.forEach { (name, preset) ->
            if (preset.visual.gradientEnabled) {
                assertThat(preset.visual.colors).isNotNull()
            }
        }
    }
}
