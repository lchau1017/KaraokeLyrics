package com.karaokelyrics.app.presentation.features.lyrics.calculator.impl

import com.karaokelyrics.app.presentation.features.lyrics.calculator.DeviceCapabilities
import com.karaokelyrics.app.presentation.features.lyrics.calculator.InstructionCalculator
import com.karaokelyrics.app.presentation.features.lyrics.model.RenderInstructions
import com.karaokelyrics.app.presentation.features.lyrics.model.TimingContext
import com.karaokelyrics.app.presentation.features.lyrics.model.TimingState
import com.karaokelyrics.app.presentation.features.lyrics.model.VisualConfig
import javax.inject.Inject

/**
 * Default implementation for render instruction calculations.
 */
class DefaultInstructionCalculator @Inject constructor() : InstructionCalculator {

    override fun calculateInstructions(
        timing: TimingContext,
        visual: VisualConfig,
        deviceCapabilities: DeviceCapabilities
    ): RenderInstructions {
        // Determine if animations should run
        val shouldAnimate = visual.enableCharacterAnimations && 
                           timing.state == TimingState.ACTIVE &&
                           !deviceCapabilities.isLowEndDevice

        // Calculate animation duration based on line length
        val animationDuration = when (timing.state) {
            TimingState.ACTIVE -> (timing.lineEndMs - timing.lineStartMs)
            else -> 300
        }

        // Calculate z-index for layering
        val zIndex = when (timing.state) {
            TimingState.ACTIVE -> 1f
            TimingState.RECENT -> 0.9f
            TimingState.UPCOMING -> 0.5f + (0.3f / (timing.distanceFromActive + 1))
            TimingState.PAST -> 0.1f
        }

        // Calculate offsets for entrance/exit animations
        val horizontalOffset = when (timing.state) {
            TimingState.UPCOMING -> if (timing.distanceFromActive > 3) 50f else 0f
            else -> 0f
        }

        val verticalOffset = 0f // Can be used for bounce effects

        return RenderInstructions(
            shouldAnimate = shouldAnimate,
            animationDuration = animationDuration,
            zIndex = zIndex,
            horizontalOffset = horizontalOffset,
            verticalOffset = verticalOffset
        )
    }
}