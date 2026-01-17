package com.karaokelyrics.ui.rendering

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for RenderingCalculations.
 * Tests all pure calculation functions used in karaoke rendering.
 */
class RenderingCalculationsTest {

    // ==================== calculateCharacterAnimation Tests ====================

    @Test
    fun `calculateCharacterAnimation returns default when before start time`() {
        val result = RenderingCalculations.calculateCharacterAnimation(
            characterStartTime = 1000,
            characterEndTime = 2000,
            currentTime = 500
        )

        assertThat(result).isEqualTo(RenderingCalculations.CharacterAnimationState.Default)
    }

    @Test
    fun `calculateCharacterAnimation returns default when after end time`() {
        val result = RenderingCalculations.calculateCharacterAnimation(
            characterStartTime = 1000,
            characterEndTime = 2000,
            currentTime = 3000
        )

        assertThat(result).isEqualTo(RenderingCalculations.CharacterAnimationState.Default)
    }

    @Test
    fun `calculateCharacterAnimation returns animated state when playing`() {
        val result = RenderingCalculations.calculateCharacterAnimation(
            characterStartTime = 1000,
            characterEndTime = 2000,
            currentTime = 1500
        )

        assertThat(result.scale).isGreaterThan(1f)
        assertThat(result.rotation).isNotEqualTo(0f)
    }

    @Test
    fun `calculateCharacterAnimation respects maxScale parameter`() {
        val result = RenderingCalculations.calculateCharacterAnimation(
            characterStartTime = 0,
            characterEndTime = 1000,
            currentTime = 400,
            maxScale = 1.5f
        )

        // Scale should be between 1 and maxScale (with pulse)
        assertThat(result.scale).isGreaterThan(1f)
        assertThat(result.scale).isLessThan(1.6f) // Allow some tolerance for pulse
    }

    @Test
    fun `calculateCharacterAnimation at exact start time produces animation`() {
        val result = RenderingCalculations.calculateCharacterAnimation(
            characterStartTime = 1000,
            characterEndTime = 2000,
            currentTime = 1000
        )

        // At start, scale should be close to 1 (just starting animation)
        assertThat(result.scale).isAtLeast(1f)
    }

    @Test
    fun `calculateCharacterAnimation at exact end time produces animation`() {
        val result = RenderingCalculations.calculateCharacterAnimation(
            characterStartTime = 1000,
            characterEndTime = 2000,
            currentTime = 2000
        )

        // At end, should still have animation state (not default)
        assertThat(result.scale).isGreaterThan(1f)
    }

    // ==================== calculatePulseScale Tests ====================

    @Test
    fun `calculatePulseScale returns value between min and max`() {
        for (time in 0..2000 step 100) {
            val result = RenderingCalculations.calculatePulseScale(
                currentTimeMs = time,
                minScale = 0.95f,
                maxScale = 1.05f
            )

            assertThat(result).isAtLeast(0.95f)
            assertThat(result).isAtMost(1.05f)
        }
    }

    @Test
    fun `calculatePulseScale oscillates over duration`() {
        val results = (0..1000 step 250).map { time ->
            RenderingCalculations.calculatePulseScale(currentTimeMs = time)
        }

        // Should not all be the same
        assertThat(results.distinct().size).isGreaterThan(1)
    }

    @Test
    fun `calculatePulseScale with custom duration affects cycle`() {
        val shortDuration = RenderingCalculations.calculatePulseScale(
            currentTimeMs = 500,
            duration = 500
        )
        val longDuration = RenderingCalculations.calculatePulseScale(
            currentTimeMs = 500,
            duration = 2000
        )

        // At different points in their respective cycles
        assertThat(shortDuration).isNotEqualTo(longDuration)
    }

    // ==================== calculateCharacterColor Tests ====================

    @Test
    fun `calculateCharacterColor returns played color after end time`() {
        val baseColor = Color.White
        val playingColor = Color.Yellow
        val playedColor = Color.Gray

        val result = RenderingCalculations.calculateCharacterColor(
            currentTimeMs = 2000,
            charStartTime = 1000,
            charEndTime = 1500,
            baseColor = baseColor,
            playingColor = playingColor,
            playedColor = playedColor
        )

        assertThat(result).isEqualTo(playedColor)
    }

    @Test
    fun `calculateCharacterColor returns base color before start time`() {
        val baseColor = Color.White
        val playingColor = Color.Yellow
        val playedColor = Color.Gray

        val result = RenderingCalculations.calculateCharacterColor(
            currentTimeMs = 500,
            charStartTime = 1000,
            charEndTime = 1500,
            baseColor = baseColor,
            playingColor = playingColor,
            playedColor = playedColor
        )

        assertThat(result).isEqualTo(baseColor)
    }

    @Test
    fun `calculateCharacterColor interpolates during playback`() {
        val baseColor = Color.White
        val playingColor = Color.Yellow
        val playedColor = Color.Gray

        val result = RenderingCalculations.calculateCharacterColor(
            currentTimeMs = 1250, // Midway
            charStartTime = 1000,
            charEndTime = 1500,
            baseColor = baseColor,
            playingColor = playingColor,
            playedColor = playedColor
        )

        // Should be between base and playing
        assertThat(result).isNotEqualTo(baseColor)
        assertThat(result).isNotEqualTo(playingColor)
        assertThat(result).isNotEqualTo(playedColor)
    }

    // ==================== calculateProgress Tests ====================

    @Test
    fun `calculateProgress returns 0 before start`() {
        val result = RenderingCalculations.calculateProgress(
            currentTime = 500,
            startTime = 1000,
            endTime = 2000
        )

        assertThat(result).isEqualTo(0f)
    }

    @Test
    fun `calculateProgress returns 0 at start`() {
        val result = RenderingCalculations.calculateProgress(
            currentTime = 1000,
            startTime = 1000,
            endTime = 2000
        )

        assertThat(result).isEqualTo(0f)
    }

    @Test
    fun `calculateProgress returns 0_5 at midpoint`() {
        val result = RenderingCalculations.calculateProgress(
            currentTime = 1500,
            startTime = 1000,
            endTime = 2000
        )

        assertThat(result).isWithin(0.01f).of(0.5f)
    }

    @Test
    fun `calculateProgress returns 1 at end`() {
        val result = RenderingCalculations.calculateProgress(
            currentTime = 2000,
            startTime = 1000,
            endTime = 2000
        )

        assertThat(result).isEqualTo(1f)
    }

    @Test
    fun `calculateProgress clamps to 1 after end`() {
        val result = RenderingCalculations.calculateProgress(
            currentTime = 3000,
            startTime = 1000,
            endTime = 2000
        )

        assertThat(result).isEqualTo(1f)
    }

    @Test
    fun `calculateProgress handles zero duration gracefully`() {
        val result = RenderingCalculations.calculateProgress(
            currentTime = 1000,
            startTime = 1000,
            endTime = 1000
        )

        assertThat(result).isEqualTo(0f)
    }

    @Test
    fun `calculateProgress handles inverted times gracefully`() {
        val result = RenderingCalculations.calculateProgress(
            currentTime = 1500,
            startTime = 2000,
            endTime = 1000
        )

        assertThat(result).isEqualTo(0f)
    }

    // ==================== lerp Tests ====================

    @Test
    fun `lerp returns start when fraction is 0`() {
        val result = RenderingCalculations.lerp(10f, 20f, 0f)
        assertThat(result).isEqualTo(10f)
    }

    @Test
    fun `lerp returns end when fraction is 1`() {
        val result = RenderingCalculations.lerp(10f, 20f, 1f)
        assertThat(result).isEqualTo(20f)
    }

    @Test
    fun `lerp returns midpoint when fraction is 0_5`() {
        val result = RenderingCalculations.lerp(10f, 20f, 0.5f)
        assertThat(result).isEqualTo(15f)
    }

    @Test
    fun `lerp handles negative values`() {
        val result = RenderingCalculations.lerp(-10f, 10f, 0.5f)
        assertThat(result).isEqualTo(0f)
    }

    @Test
    fun `lerp handles reverse direction`() {
        val result = RenderingCalculations.lerp(20f, 10f, 0.5f)
        assertThat(result).isEqualTo(15f)
    }

    // ==================== lerpColor Tests ====================

    @Test
    fun `lerpColor returns start color when fraction is 0`() {
        val result = RenderingCalculations.lerpColor(Color.Red, Color.Blue, 0f)
        assertThat(result.red).isEqualTo(Color.Red.red)
        assertThat(result.green).isEqualTo(Color.Red.green)
        assertThat(result.blue).isEqualTo(Color.Red.blue)
    }

    @Test
    fun `lerpColor returns end color when fraction is 1`() {
        val result = RenderingCalculations.lerpColor(Color.Red, Color.Blue, 1f)
        assertThat(result.red).isEqualTo(Color.Blue.red)
        assertThat(result.green).isEqualTo(Color.Blue.green)
        assertThat(result.blue).isEqualTo(Color.Blue.blue)
    }

    @Test
    fun `lerpColor interpolates all channels`() {
        val start = Color(red = 0f, green = 0f, blue = 0f, alpha = 0f)
        val end = Color(red = 1f, green = 1f, blue = 1f, alpha = 1f)

        val result = RenderingCalculations.lerpColor(start, end, 0.5f)

        assertThat(result.red).isWithin(0.01f).of(0.5f)
        assertThat(result.green).isWithin(0.01f).of(0.5f)
        assertThat(result.blue).isWithin(0.01f).of(0.5f)
        assertThat(result.alpha).isWithin(0.01f).of(0.5f)
    }

    @Test
    fun `lerpColor preserves alpha interpolation`() {
        val transparent = Color.Red.copy(alpha = 0f)
        val opaque = Color.Red.copy(alpha = 1f)

        val result = RenderingCalculations.lerpColor(transparent, opaque, 0.5f)

        assertThat(result.alpha).isWithin(0.01f).of(0.5f)
    }

    // ==================== CharacterAnimationState Tests ====================

    @Test
    fun `CharacterAnimationState Default has expected values`() {
        val default = RenderingCalculations.CharacterAnimationState.Default

        assertThat(default.scale).isEqualTo(1f)
        assertThat(default.offset).isEqualTo(Offset.Zero)
        assertThat(default.rotation).isEqualTo(0f)
    }

    @Test
    fun `CharacterAnimationState equality works correctly`() {
        val state1 = RenderingCalculations.CharacterAnimationState(1.5f, Offset(1f, 2f), 3f)
        val state2 = RenderingCalculations.CharacterAnimationState(1.5f, Offset(1f, 2f), 3f)
        val state3 = RenderingCalculations.CharacterAnimationState(1.6f, Offset(1f, 2f), 3f)

        assertThat(state1).isEqualTo(state2)
        assertThat(state1).isNotEqualTo(state3)
    }
}
