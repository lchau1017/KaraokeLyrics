package com.karaokelyrics.app.presentation.ui.helper

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.domain.model.karaoke.KaraokeAlignment
import com.karaokelyrics.app.data.util.TextLayoutCalculationUtil
import com.karaokelyrics.app.data.util.TextLayoutCalculationUtil.LineLayout
import javax.inject.Inject

/**
 * Presentation layer helper for calculating text layout for karaoke lines.
 * This is a UI concern and belongs in the presentation layer, not domain.
 */
class TextLayoutCalculator @Inject constructor(
    private val textCharacteristicsProcessor: TextCharacteristicsProcessor
) {

    fun calculateLayout(
        line: KaraokeLine,
        textMeasurer: TextMeasurer,
        textStyle: TextStyle,
        availableWidthPx: Float,
        lineHeight: Float,
        canvasWidth: Float,
        enableCharacterAnimations: Boolean = true,
        isRtl: Boolean = false
    ): LineLayout {

        // Step 1: Process text characteristics and determine animations
        val syllableLayouts = textCharacteristicsProcessor.processSyllables(
            syllables = line.syllables,
            textMeasurer = textMeasurer,
            style = textStyle,
            isAccompanimentLine = line.isAccompaniment,
            enableCharacterAnimations = enableCharacterAnimations
        )

        // Step 2: Calculate wrapped lines using greedy algorithm
        val wrappedLines = TextLayoutCalculationUtil.calculateGreedyWrappedLines(
            syllableLayouts = syllableLayouts,
            availableWidthPx = availableWidthPx,
            textMeasurer = textMeasurer,
            style = textStyle
        )

        // Step 3: Calculate static positions for all syllables
        val isLineRightAligned = when (line.alignment) {
            KaraokeAlignment.Start -> true
            else -> false
        }

        val finalLayouts = TextLayoutCalculationUtil.calculateStaticLineLayout(
            wrappedLines = wrappedLines,
            isLineRightAligned = isLineRightAligned,
            canvasWidth = canvasWidth,
            lineHeight = lineHeight,
            isRtl = isRtl
        )

        return LineLayout(
            line = line,
            wrappedLines = wrappedLines,
            syllableLayouts = finalLayouts,
            totalHeight = finalLayouts.size * lineHeight
        )
    }
}