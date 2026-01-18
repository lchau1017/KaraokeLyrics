package com.kyrics.demo.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

/**
 * Sealed interface representing all user intents in the Demo screen.
 */
sealed interface DemoIntent {
    // Playback controls
    data object TogglePlayPause : DemoIntent
    data object Reset : DemoIntent
    data class Seek(val timeMs: Long) : DemoIntent
    data class UpdateTime(val timeMs: Long) : DemoIntent

    // Line selection
    data class SelectLine(val index: Int) : DemoIntent

    // Viewer type
    data class SelectViewerType(val index: Int) : DemoIntent

    // Color picker
    data class ShowColorPicker(val target: ColorPickerTarget) : DemoIntent
    data object DismissColorPicker : DemoIntent
    data class UpdateColor(val target: ColorPickerTarget, val color: Color) : DemoIntent

    // Font settings
    data class UpdateFontSize(val size: Float) : DemoIntent
    data class UpdateFontWeight(val weight: FontWeight) : DemoIntent
    data class UpdateFontFamily(val family: FontFamily) : DemoIntent
    data class UpdateTextAlign(val align: TextAlign) : DemoIntent

    // Layout settings
    data class UpdateLineSpacing(val spacing: Float) : DemoIntent

    // Visual effects
    data class ToggleGradient(val enabled: Boolean) : DemoIntent
    data class UpdateGradientAngle(val angle: Float) : DemoIntent
    data class ToggleBlur(val enabled: Boolean) : DemoIntent
    data class UpdateBlurIntensity(val intensity: Float) : DemoIntent

    // Character animations
    data class ToggleCharAnimation(val enabled: Boolean) : DemoIntent
    data class UpdateCharMaxScale(val scale: Float) : DemoIntent
    data class UpdateCharFloatOffset(val offset: Float) : DemoIntent
    data class UpdateCharRotation(val degrees: Float) : DemoIntent

    // Line animations
    data class ToggleLineAnimation(val enabled: Boolean) : DemoIntent
    data class UpdateLineScaleOnPlay(val scale: Float) : DemoIntent

    // Pulse effect
    data class TogglePulse(val enabled: Boolean) : DemoIntent
    data class UpdatePulseMinScale(val scale: Float) : DemoIntent
    data class UpdatePulseMaxScale(val scale: Float) : DemoIntent

    // Presets
    data class LoadPreset(val presetName: String) : DemoIntent
}
