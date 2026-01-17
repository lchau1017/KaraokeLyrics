package com.karaokelyrics.demo

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for DemoSettings data class.
 * Verifies defaults and copy behavior.
 */
class DemoSettingsTest {

    // ==================== Default Values Tests ====================

    @Test
    fun `default settings has correct fontSize`() {
        val settings = DemoSettings()

        assertThat(settings.fontSize).isEqualTo(22f)
    }

    @Test
    fun `default settings has bold fontWeight`() {
        val settings = DemoSettings()

        assertThat(settings.fontWeight).isEqualTo(FontWeight.Bold)
    }

    @Test
    fun `default settings has default fontFamily`() {
        val settings = DemoSettings()

        assertThat(settings.fontFamily).isEqualTo(FontFamily.Default)
    }

    @Test
    fun `default settings has center textAlign`() {
        val settings = DemoSettings()

        assertThat(settings.textAlign).isEqualTo(TextAlign.Center)
    }

    @Test
    fun `default settings has gray sungColor`() {
        val settings = DemoSettings()

        assertThat(settings.sungColor).isEqualTo(Color(0xFF9E9E9E))
    }

    @Test
    fun `default settings has dark gray unsungColor`() {
        val settings = DemoSettings()

        assertThat(settings.unsungColor).isEqualTo(Color(0xFF616161))
    }

    @Test
    fun `default settings has yellow activeColor`() {
        val settings = DemoSettings()

        assertThat(settings.activeColor).isEqualTo(Color(0xFFFFEB3B))
    }

    @Test
    fun `default settings has dark backgroundColor`() {
        val settings = DemoSettings()

        assertThat(settings.backgroundColor).isEqualTo(Color(0xFF121212))
    }

    // ==================== Visual Effects Defaults Tests ====================

    @Test
    fun `default settings has gradient disabled`() {
        val settings = DemoSettings()

        assertThat(settings.gradientEnabled).isFalse()
    }

    @Test
    fun `default settings has gradientAngle 45 degrees`() {
        val settings = DemoSettings()

        assertThat(settings.gradientAngle).isEqualTo(45f)
    }

    @Test
    fun `default settings has blur disabled`() {
        val settings = DemoSettings()

        assertThat(settings.blurEnabled).isFalse()
    }

    @Test
    fun `default settings has blurIntensity 1`() {
        val settings = DemoSettings()

        assertThat(settings.blurIntensity).isEqualTo(1f)
    }

    // ==================== Animation Defaults Tests ====================

    @Test
    fun `default settings has character animation disabled`() {
        val settings = DemoSettings()

        assertThat(settings.charAnimEnabled).isFalse()
    }

    @Test
    fun `default settings has charMaxScale 1_2`() {
        val settings = DemoSettings()

        assertThat(settings.charMaxScale).isEqualTo(1.2f)
    }

    @Test
    fun `default settings has charFloatOffset 8`() {
        val settings = DemoSettings()

        assertThat(settings.charFloatOffset).isEqualTo(8f)
    }

    @Test
    fun `default settings has charRotationDegrees 5`() {
        val settings = DemoSettings()

        assertThat(settings.charRotationDegrees).isEqualTo(5f)
    }

    @Test
    fun `default settings has line animation disabled`() {
        val settings = DemoSettings()

        assertThat(settings.lineAnimEnabled).isFalse()
    }

    @Test
    fun `default settings has lineScaleOnPlay 1_05`() {
        val settings = DemoSettings()

        assertThat(settings.lineScaleOnPlay).isEqualTo(1.05f)
    }

    @Test
    fun `default settings has pulse disabled`() {
        val settings = DemoSettings()

        assertThat(settings.pulseEnabled).isFalse()
    }

    @Test
    fun `default settings has pulseMinScale 0_95`() {
        val settings = DemoSettings()

        assertThat(settings.pulseMinScale).isEqualTo(0.95f)
    }

    @Test
    fun `default settings has pulseMaxScale 1_05`() {
        val settings = DemoSettings()

        assertThat(settings.pulseMaxScale).isEqualTo(1.05f)
    }

    // ==================== Layout Defaults Tests ====================

    @Test
    fun `default settings has lineSpacing 80`() {
        val settings = DemoSettings()

        assertThat(settings.lineSpacing).isEqualTo(80f)
    }

    @Test
    fun `default settings has viewerTypeIndex 0`() {
        val settings = DemoSettings()

        assertThat(settings.viewerTypeIndex).isEqualTo(0)
    }

    // ==================== Companion Object Tests ====================

    @Test
    fun `Default companion object matches default constructor`() {
        val defaultInstance = DemoSettings.Default
        val constructedInstance = DemoSettings()

        assertThat(defaultInstance).isEqualTo(constructedInstance)
    }

    // ==================== Copy Tests ====================

    @Test
    fun `copy preserves unchanged values`() {
        val original = DemoSettings(
            fontSize = 30f,
            fontWeight = FontWeight.Bold,
            sungColor = Color.Red
        )

        val copied = original.copy(fontSize = 40f)

        assertThat(copied.fontSize).isEqualTo(40f)
        assertThat(copied.fontWeight).isEqualTo(FontWeight.Bold)
        assertThat(copied.sungColor).isEqualTo(Color.Red)
    }

    @Test
    fun `copy can update multiple values`() {
        val original = DemoSettings()

        val copied = original.copy(
            fontSize = 50f,
            gradientEnabled = true,
            charAnimEnabled = true,
            lineAnimEnabled = true
        )

        assertThat(copied.fontSize).isEqualTo(50f)
        assertThat(copied.gradientEnabled).isTrue()
        assertThat(copied.charAnimEnabled).isTrue()
        assertThat(copied.lineAnimEnabled).isTrue()
    }

    // ==================== Immutability Tests ====================

    @Test
    fun `settings is immutable - copy creates new instance`() {
        val original = DemoSettings()
        val copied = original.copy(fontSize = 100f)

        assertThat(original.fontSize).isEqualTo(22f)
        assertThat(copied.fontSize).isEqualTo(100f)
        assertThat(original).isNotSameInstanceAs(copied)
    }

    // ==================== Equality Tests ====================

    @Test
    fun `same values are equal`() {
        val settings1 = DemoSettings(fontSize = 30f)
        val settings2 = DemoSettings(fontSize = 30f)

        assertThat(settings1).isEqualTo(settings2)
    }

    @Test
    fun `different values are not equal`() {
        val settings1 = DemoSettings(fontSize = 30f)
        val settings2 = DemoSettings(fontSize = 40f)

        assertThat(settings1).isNotEqualTo(settings2)
    }

    // ==================== Full Configuration Tests ====================

    @Test
    fun `can create fully customized settings`() {
        val customSettings = DemoSettings(
            fontSize = 40f,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Start,
            sungColor = Color.Green,
            unsungColor = Color.Blue,
            activeColor = Color.Cyan,
            backgroundColor = Color.Black,
            gradientEnabled = true,
            gradientAngle = 90f,
            blurEnabled = true,
            blurIntensity = 2f,
            charAnimEnabled = true,
            charMaxScale = 1.5f,
            charFloatOffset = 15f,
            charRotationDegrees = 10f,
            lineAnimEnabled = true,
            lineScaleOnPlay = 1.2f,
            pulseEnabled = true,
            pulseMinScale = 0.9f,
            pulseMaxScale = 1.1f,
            lineSpacing = 100f,
            viewerTypeIndex = 5
        )

        assertThat(customSettings.fontSize).isEqualTo(40f)
        assertThat(customSettings.fontWeight).isEqualTo(FontWeight.Black)
        assertThat(customSettings.fontFamily).isEqualTo(FontFamily.Monospace)
        assertThat(customSettings.textAlign).isEqualTo(TextAlign.Start)
        assertThat(customSettings.gradientEnabled).isTrue()
        assertThat(customSettings.blurEnabled).isTrue()
        assertThat(customSettings.charAnimEnabled).isTrue()
        assertThat(customSettings.lineAnimEnabled).isTrue()
        assertThat(customSettings.pulseEnabled).isTrue()
        assertThat(customSettings.viewerTypeIndex).isEqualTo(5)
    }

    // ==================== Viewer Type Index Tests ====================

    @Test
    fun `viewerTypeIndex can represent all 12 viewer types`() {
        // There are 12 viewer types (0-11)
        for (index in 0..11) {
            val settings = DemoSettings(viewerTypeIndex = index)
            assertThat(settings.viewerTypeIndex).isEqualTo(index)
        }
    }
}
