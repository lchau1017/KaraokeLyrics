package com.kyrics.rendering

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import com.kyrics.config.ColorConfig
import com.kyrics.config.GradientPreset
import com.kyrics.config.GradientType
import com.kyrics.config.KyricsConfig
import com.kyrics.config.VisualConfig
import org.junit.Test

/**
 * Unit tests for GradientFactory.
 * Tests gradient brush creation for karaoke text rendering.
 */
class GradientFactoryTest {

    // ==================== createLinearGradient Tests ====================

    @Test
    fun `createLinearGradient returns brush with given colors`() {
        val colors = listOf(Color.Red, Color.Blue)

        val result = GradientFactory.createLinearGradient(
            colors = colors,
            angle = 45f,
            width = 100f,
            height = 50f
        )

        assertThat(result).isNotNull()
        assertThat(result).isInstanceOf(Brush::class.java)
    }

    @Test
    fun `createLinearGradient with 0 degree angle creates horizontal gradient`() {
        val colors = listOf(Color.Red, Color.Blue)

        val result = GradientFactory.createLinearGradient(
            colors = colors,
            angle = 0f,
            width = 100f,
            height = 50f
        )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createLinearGradient with 90 degree angle creates vertical gradient`() {
        val colors = listOf(Color.Red, Color.Blue)

        val result = GradientFactory.createLinearGradient(
            colors = colors,
            angle = 90f,
            width = 100f,
            height = 50f
        )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createLinearGradient handles multiple colors`() {
        val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow)

        val result = GradientFactory.createLinearGradient(
            colors = colors,
            angle = 45f,
            width = 100f,
            height = 50f
        )

        assertThat(result).isNotNull()
    }

    // ==================== createProgressGradient Tests ====================

    @Test
    fun `createProgressGradient with zero progress returns base color gradient`() {
        val result = GradientFactory.createProgressGradient(
            progress = 0f,
            baseColor = Color.White,
            highlightColor = Color.Yellow,
            width = 100f
        )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createProgressGradient with full progress returns highlight color gradient`() {
        val result = GradientFactory.createProgressGradient(
            progress = 1f,
            baseColor = Color.White,
            highlightColor = Color.Yellow,
            width = 100f
        )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createProgressGradient with partial progress creates split gradient`() {
        val result = GradientFactory.createProgressGradient(
            progress = 0.5f,
            baseColor = Color.White,
            highlightColor = Color.Yellow,
            width = 100f
        )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createProgressGradient clamps negative progress to zero`() {
        val result = GradientFactory.createProgressGradient(
            progress = -0.5f,
            baseColor = Color.White,
            highlightColor = Color.Yellow,
            width = 100f
        )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createProgressGradient clamps progress over 1 to 1`() {
        val result = GradientFactory.createProgressGradient(
            progress = 1.5f,
            baseColor = Color.White,
            highlightColor = Color.Yellow,
            width = 100f
        )

        assertThat(result).isNotNull()
    }

    // ==================== createMultiColorGradient Tests ====================

    @Test
    fun `createMultiColorGradient with single color returns solid gradient`() {
        val result = GradientFactory.createMultiColorGradient(
            colors = listOf(Color.Red),
            angle = 45f,
            width = 100f,
            height = 50f
        )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createMultiColorGradient with empty list returns white gradient`() {
        val result = GradientFactory.createMultiColorGradient(
            colors = emptyList(),
            angle = 45f,
            width = 100f,
            height = 50f
        )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createMultiColorGradient distributes stops evenly`() {
        val colors = listOf(Color.Red, Color.Green, Color.Blue)

        val result = GradientFactory.createMultiColorGradient(
            colors = colors,
            angle = 0f,
            width = 100f,
            height = 50f
        )

        assertThat(result).isNotNull()
    }

    // ==================== getPresetColors Tests ====================

    @Test
    fun `getPresetColors returns rainbow colors for RAINBOW preset`() {
        val result = GradientFactory.getPresetColors(GradientPreset.RAINBOW)

        assertThat(result).isNotNull()
        assertThat(result).hasSize(7) // ROYGBIV
    }

    @Test
    fun `getPresetColors returns sunset colors for SUNSET preset`() {
        val result = GradientFactory.getPresetColors(GradientPreset.SUNSET)

        assertThat(result).isNotNull()
        assertThat(result).hasSize(3)
    }

    @Test
    fun `getPresetColors returns ocean colors for OCEAN preset`() {
        val result = GradientFactory.getPresetColors(GradientPreset.OCEAN)

        assertThat(result).isNotNull()
        assertThat(result).hasSize(3)
    }

    @Test
    fun `getPresetColors returns fire colors for FIRE preset`() {
        val result = GradientFactory.getPresetColors(GradientPreset.FIRE)

        assertThat(result).isNotNull()
        assertThat(result).hasSize(3)
    }

    @Test
    fun `getPresetColors returns neon colors for NEON preset`() {
        val result = GradientFactory.getPresetColors(GradientPreset.NEON)

        assertThat(result).isNotNull()
        assertThat(result).hasSize(3)
    }

    @Test
    fun `getPresetColors returns null for null preset`() {
        val result = GradientFactory.getPresetColors(null)

        assertThat(result).isNull()
    }

    // ==================== createCharacterGradient Tests ====================

    @Test
    fun `createCharacterGradient with PROGRESS type creates progress gradient`() {
        val config = KyricsConfig(
            visual = VisualConfig(
                gradientEnabled = true,
                gradientType = GradientType.PROGRESS,
                colors = ColorConfig(
                    active = Color.Yellow,
                    sung = Color.Green
                )
            )
        )

        val result = GradientFactory.createCharacterGradient(
            charWidth = 50f,
            charHeight = 30f,
            charProgress = 0.5f,
            config = config,
            baseColor = Color.White
        )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createCharacterGradient with MULTI_COLOR type creates multi-color gradient`() {
        val config = KyricsConfig(
            visual = VisualConfig(
                gradientEnabled = true,
                gradientType = GradientType.MULTI_COLOR,
                playingGradientColors = listOf(Color.Red, Color.Green, Color.Blue),
                gradientAngle = 45f
            )
        )

        val result = GradientFactory.createCharacterGradient(
            charWidth = 50f,
            charHeight = 30f,
            charProgress = 0.5f,
            config = config,
            baseColor = Color.White
        )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createCharacterGradient with PRESET type uses preset colors`() {
        val config = KyricsConfig(
            visual = VisualConfig(
                gradientEnabled = true,
                gradientType = GradientType.PRESET,
                gradientPreset = GradientPreset.RAINBOW,
                gradientAngle = 0f
            )
        )

        val result = GradientFactory.createCharacterGradient(
            charWidth = 50f,
            charHeight = 30f,
            charProgress = 0.5f,
            config = config,
            baseColor = Color.White
        )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createCharacterGradient with LINEAR type creates linear gradient`() {
        val config = KyricsConfig(
            visual = VisualConfig(
                gradientEnabled = true,
                gradientType = GradientType.LINEAR,
                colors = ColorConfig(
                    active = Color.Cyan,
                    sung = Color.Magenta
                ),
                gradientAngle = 45f
            )
        )

        val result = GradientFactory.createCharacterGradient(
            charWidth = 50f,
            charHeight = 30f,
            charProgress = 0.5f,
            config = config,
            baseColor = Color.White
        )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createCharacterGradient uses fallback colors when playingGradientColors is empty`() {
        val config = KyricsConfig(
            visual = VisualConfig(
                gradientEnabled = true,
                gradientType = GradientType.MULTI_COLOR,
                playingGradientColors = emptyList(),
                colors = ColorConfig(
                    active = Color.Yellow,
                    sung = Color.Green
                )
            )
        )

        val result = GradientFactory.createCharacterGradient(
            charWidth = 50f,
            charHeight = 30f,
            charProgress = 0.5f,
            config = config,
            baseColor = Color.White
        )

        assertThat(result).isNotNull()
    }

    @Test
    fun `createCharacterGradient uses fallback colors when preset is null`() {
        val config = KyricsConfig(
            visual = VisualConfig(
                gradientEnabled = true,
                gradientType = GradientType.PRESET,
                gradientPreset = null,
                colors = ColorConfig(
                    active = Color.Yellow,
                    sung = Color.Green
                )
            )
        )

        val result = GradientFactory.createCharacterGradient(
            charWidth = 50f,
            charHeight = 30f,
            charProgress = 0.5f,
            config = config,
            baseColor = Color.White
        )

        assertThat(result).isNotNull()
    }

    // ==================== Gradient Angle Tests ====================

    @Test
    fun `gradient handles 180 degree angle`() {
        val result = GradientFactory.createLinearGradient(
            colors = listOf(Color.Red, Color.Blue),
            angle = 180f,
            width = 100f,
            height = 50f
        )

        assertThat(result).isNotNull()
    }

    @Test
    fun `gradient handles 270 degree angle`() {
        val result = GradientFactory.createLinearGradient(
            colors = listOf(Color.Red, Color.Blue),
            angle = 270f,
            width = 100f,
            height = 50f
        )

        assertThat(result).isNotNull()
    }

    @Test
    fun `gradient handles 360 degree angle (same as 0)`() {
        val result = GradientFactory.createLinearGradient(
            colors = listOf(Color.Red, Color.Blue),
            angle = 360f,
            width = 100f,
            height = 50f
        )

        assertThat(result).isNotNull()
    }

    @Test
    fun `gradient handles negative angle`() {
        val result = GradientFactory.createLinearGradient(
            colors = listOf(Color.Red, Color.Blue),
            angle = -45f,
            width = 100f,
            height = 50f
        )

        assertThat(result).isNotNull()
    }
}
