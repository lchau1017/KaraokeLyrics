package com.karaokelyrics.app.presentation.features.lyrics.calculator.impl

import androidx.compose.ui.graphics.Color
import com.karaokelyrics.app.presentation.features.lyrics.calculator.LineType
import com.karaokelyrics.app.presentation.features.lyrics.calculator.UserRenderPreferences
import com.karaokelyrics.app.presentation.features.lyrics.calculator.VisualCalculator
import com.karaokelyrics.app.presentation.features.lyrics.model.TimingContext
import com.karaokelyrics.app.presentation.features.lyrics.model.TimingState
import com.karaokelyrics.app.presentation.features.lyrics.model.VisualConfig
import javax.inject.Inject

/**
 * Default implementation of visual calculations.
 */
class DefaultVisualCalculator @Inject constructor() : VisualCalculator {

    override fun calculateVisual(
        timing: TimingContext,
        preferences: UserRenderPreferences,
        lineType: LineType
    ): VisualConfig {
        // Select text style based on line type
        val textStyle = when (lineType) {
            LineType.ACCOMPANIMENT -> preferences.accompanimentTextStyle
            else -> preferences.textStyle
        }

        // Calculate opacity based on timing state
        val opacity = calculateOpacity(timing)

        // Calculate scale for emphasis
        val scale = when (timing.state) {
            TimingState.ACTIVE -> 1.05f
            else -> 1.0f
        }

        // Calculate blur for depth effect
        val blur = if (preferences.enableBlur) {
            calculateBlur(timing)
        } else 0f

        // Character animation settings
        val enableCharAnim = preferences.enableCharacterAnimations && 
                             timing.state == TimingState.ACTIVE

        return VisualConfig(
            textStyle = textStyle,
            textColor = preferences.textColor,
            opacity = opacity,
            scale = scale,
            blur = blur,
            enableCharacterAnimations = enableCharAnim,
            characterFloatOffset = if (enableCharAnim) 6f else 0f,
            characterWaveDelay = if (enableCharAnim) 50f else 0f,
            activeCharacterColor = preferences.textColor,
            inactiveCharacterColor = preferences.textColor.copy(alpha = 0.3f)
        )
    }

    private fun calculateOpacity(timing: TimingContext): Float {
        return when (timing.state) {
            TimingState.ACTIVE -> 1.0f
            TimingState.RECENT -> 0.8f
            TimingState.UPCOMING -> when (timing.distanceFromActive) {
                1 -> 0.6f
                2 -> 0.4f
                3 -> 0.3f
                else -> 0.2f
            }
            TimingState.PAST -> 0.3f
        }
    }

    private fun calculateBlur(timing: TimingContext): Float {
        return when (timing.state) {
            TimingState.UPCOMING -> when (timing.distanceFromActive) {
                in 4..6 -> 10f
                in 7..Int.MAX_VALUE -> 20f
                else -> 0f
            }
            else -> 0f
        }
    }
}