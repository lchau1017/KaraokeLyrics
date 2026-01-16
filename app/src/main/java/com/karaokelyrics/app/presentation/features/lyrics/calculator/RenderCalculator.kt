package com.karaokelyrics.app.presentation.features.lyrics.calculator

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.karaokelyrics.app.domain.model.ISyncedLine
import com.karaokelyrics.app.presentation.features.lyrics.model.*

/**
 * Interface for render calculations.
 * Following Dependency Inversion Principle - high-level modules
 * should not depend on low-level modules.
 */
interface RenderCalculator {
    fun calculateRenderModel(
        line: ISyncedLine,
        index: Int,
        context: RenderContext
    ): LyricsRenderModel
}

/**
 * Context containing all information needed for calculations.
 * This is passed to calculators instead of individual parameters.
 */
data class RenderContext(
    val currentTimeMs: Int,
    val allLines: List<ISyncedLine>,
    val focusedIndices: Set<Int>,
    val userPreferences: UserRenderPreferences,
    val deviceCapabilities: DeviceCapabilities
)

/**
 * User preferences for rendering.
 */
data class UserRenderPreferences(
    val textStyle: TextStyle,
    val textColor: Color,
    val accompanimentTextStyle: TextStyle,
    val enableAnimations: Boolean,
    val enableBlur: Boolean,
    val enableCharacterAnimations: Boolean,
    val timingOffset: Int
)

/**
 * Device capabilities that affect rendering.
 */
data class DeviceCapabilities(
    val supportsBlur: Boolean,
    val maxTextureSize: Int,
    val isLowEndDevice: Boolean
)

/**
 * Strategy interface for different calculation aspects.
 * Each calculator is responsible for one aspect (SRP).
 */
interface TimingCalculator {
    fun calculateTiming(
        line: ISyncedLine,
        currentTimeMs: Int,
        timingOffset: Int
    ): TimingContext
}

interface VisualCalculator {
    fun calculateVisual(
        timing: TimingContext,
        preferences: UserRenderPreferences,
        lineType: LineType
    ): VisualConfig
}

interface InteractionCalculator {
    fun calculateInteraction(
        timing: TimingContext,
        index: Int
    ): InteractionHints
}

interface InstructionCalculator {
    fun calculateInstructions(
        timing: TimingContext,
        visual: VisualConfig,
        deviceCapabilities: DeviceCapabilities
    ): RenderInstructions
}

/**
 * Line type classification.
 */
enum class LineType {
    NORMAL,
    ACCOMPANIMENT,
    INSTRUMENTAL
}