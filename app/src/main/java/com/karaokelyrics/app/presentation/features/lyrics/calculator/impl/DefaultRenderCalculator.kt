package com.karaokelyrics.app.presentation.features.lyrics.calculator.impl

import com.karaokelyrics.app.domain.model.ISyncedLine
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.presentation.features.lyrics.calculator.*
import com.karaokelyrics.app.presentation.features.lyrics.model.*
import javax.inject.Inject

/**
 * Default implementation of RenderCalculator.
 * Composes multiple calculators following Single Responsibility Principle.
 */
class DefaultRenderCalculator @Inject constructor(
    private val timingCalculator: TimingCalculator,
    private val visualCalculator: VisualCalculator,
    private val interactionCalculator: InteractionCalculator,
    private val instructionCalculator: InstructionCalculator
) : RenderCalculator {

    override fun calculateRenderModel(
        line: ISyncedLine,
        index: Int,
        context: RenderContext
    ): LyricsRenderModel {
        // Step 1: Calculate timing
        val timing = timingCalculator.calculateTiming(
            line = line,
            currentTimeMs = context.currentTimeMs,
            timingOffset = context.userPreferences.timingOffset
        )

        // Step 2: Determine line type
        val lineType = when (line) {
            is KaraokeLine -> when {
                line.isAccompaniment -> LineType.ACCOMPANIMENT
                else -> LineType.NORMAL
            }
            else -> LineType.NORMAL
        }

        // Step 3: Calculate visual configuration
        val visual = visualCalculator.calculateVisual(
            timing = timing,
            preferences = context.userPreferences,
            lineType = lineType
        )

        // Step 4: Calculate interaction hints
        val interaction = interactionCalculator.calculateInteraction(
            timing = timing,
            index = index
        )

        // Step 5: Calculate render instructions
        val instructions = instructionCalculator.calculateInstructions(
            timing = timing,
            visual = visual,
            deviceCapabilities = context.deviceCapabilities
        )

        return LyricsRenderModel(
            line = line,
            index = index,
            timing = timing,
            visual = visual,
            interaction = interaction,
            renderInstructions = instructions
        )
    }
}