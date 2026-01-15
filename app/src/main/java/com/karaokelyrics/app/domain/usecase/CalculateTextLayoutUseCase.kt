package com.karaokelyrics.app.domain.usecase

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.data.util.TextLayoutCalculationUtil.LineLayout
import javax.inject.Inject

/**
 * Domain use case for calculating text layout for karaoke lines.
 * Orchestrates the complete text layout process following Clean Architecture principles.
 */
class CalculateTextLayoutUseCase @Inject constructor(
    private val processTextCharacteristicsUseCase: ProcessTextCharacteristicsUseCase
) {

    operator fun invoke(
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
        val syllableLayouts = processTextCharacteristicsUseCase(
            syllables = line.syllables,
            textMeasurer = textMeasurer,
            style = textStyle,
            isAccompanimentLine = line.isAccompaniment,
            enableCharacterAnimations = enableCharacterAnimations
        )

        // Step 2: Calculate line wrapping using data layer utilities
        val wrappedLines = com.karaokelyrics.app.data.util.TextLayoutCalculationUtil.calculateGreedyWrappedLines(
            syllableLayouts = syllableLayouts,
            availableWidthPx = availableWidthPx,
            textMeasurer = textMeasurer,
            style = textStyle
        )

        // Step 3: Calculate final positioning using data layer utilities
        val isLineRightAligned = line.alignment.name.contains("End", ignoreCase = true)

        val finalSyllableLayouts = com.karaokelyrics.app.data.util.TextLayoutCalculationUtil.calculateStaticLineLayout(
            wrappedLines = wrappedLines,
            isLineRightAligned = isLineRightAligned,
            canvasWidth = canvasWidth,
            lineHeight = lineHeight,
            isRtl = isRtl
        )

        // Step 4: Calculate total height
        val totalHeight = wrappedLines.size * lineHeight

        return LineLayout(
            line = line,
            wrappedLines = wrappedLines,
            syllableLayouts = finalSyllableLayouts,
            totalHeight = totalHeight
        )
    }

}