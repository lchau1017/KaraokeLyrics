package com.karaokelyrics.app.presentation.features.lyrics.calculator.impl

import com.karaokelyrics.app.presentation.features.lyrics.calculator.InteractionCalculator
import com.karaokelyrics.app.presentation.features.lyrics.model.ClickAction
import com.karaokelyrics.app.presentation.features.lyrics.model.InteractionHints
import com.karaokelyrics.app.presentation.features.lyrics.model.TimingContext
import com.karaokelyrics.app.presentation.features.lyrics.model.TimingState
import javax.inject.Inject

/**
 * Default implementation for interaction calculations.
 */
class DefaultInteractionCalculator @Inject constructor() : InteractionCalculator {

    override fun calculateInteraction(
        timing: TimingContext,
        index: Int
    ): InteractionHints {
        return InteractionHints(
            isClickable = timing.state == TimingState.UPCOMING,
            isFocused = timing.state == TimingState.ACTIVE,
            isHighlighted = timing.state == TimingState.ACTIVE,
            clickAction = if (timing.state == TimingState.UPCOMING) {
                ClickAction.SEEK
            } else {
                ClickAction.NONE
            }
        )
    }
}