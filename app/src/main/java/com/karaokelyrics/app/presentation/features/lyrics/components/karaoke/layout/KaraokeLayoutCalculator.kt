package com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.karaokelyrics.app.presentation.shared.layout.TextLayoutCalculationUtil
import com.karaokelyrics.app.presentation.shared.layout.TextLayoutCalculationUtil.LineLayout
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.presentation.shared.animation.AnimationDecisionCalculator
import com.karaokelyrics.app.domain.usecase.GroupSyllablesIntoWordsUseCase
import com.karaokelyrics.app.presentation.shared.helper.TextCharacteristicsProcessor

/**
 * Calculates layout for karaoke lines.
 * Single Responsibility: Layout calculation only.
 */
object KaraokeLayoutCalculator {

    fun calculateLayout(
        line: KaraokeLine,
        textMeasurer: TextMeasurer,
        textStyle: TextStyle,
        availableWidthPx: Float,
        lineHeight: Float,
        enableCharacterAnimations: Boolean,
        isRtl: Boolean
    ): LineLayout {
        // Create use cases locally - they're stateless
        val groupSyllablesUseCase = GroupSyllablesIntoWordsUseCase()
        val determineAnimationUseCase = AnimationDecisionCalculator()
        val textProcessor = TextCharacteristicsProcessor(
            groupSyllablesUseCase,
            determineAnimationUseCase
        )

        // Process syllables
        val syllableLayouts = textProcessor.processSyllables(
            syllables = line.syllables,
            textMeasurer = textMeasurer,
            style = textStyle,
            enableCharacterAnimations = enableCharacterAnimations,
            isAccompanimentLine = line.isAccompaniment
        )

        // Wrap into lines
        val wrappedLines = TextLayoutCalculationUtil.calculateGreedyWrappedLines(
            syllableLayouts = syllableLayouts,
            availableWidthPx = availableWidthPx,
            textMeasurer = textMeasurer,
            style = textStyle
        )

        // Calculate final positions from metadata
        val alignmentStr = line.metadata["alignment"] ?: "Center"
        val isRightAligned = when (alignmentStr) {
            "Start" -> isRtl
            "End" -> !isRtl
            else -> false
        }

        val positionedLayouts = TextLayoutCalculationUtil.calculateStaticLineLayout(
            wrappedLines = wrappedLines,
            isLineRightAligned = isRightAligned,
            canvasWidth = availableWidthPx,
            lineHeight = lineHeight,
            isRtl = isRtl
        )

        // Create LineLayout structure
        val totalHeight = positionedLayouts.size * lineHeight

        return LineLayout(
            syllableLayouts = positionedLayouts,
            totalHeight = totalHeight,
            lineHeight = lineHeight,
            rows = positionedLayouts.size
        )
    }
}

@Composable
fun rememberKaraokeLayout(
    line: KaraokeLine,
    textStyle: TextStyle,
    availableWidthPx: Float,
    lineHeight: Float,
    textMeasurer: TextMeasurer,
    enableCharacterAnimations: Boolean,
    isRtl: Boolean
): LineLayout {
    return remember(line, textStyle, availableWidthPx, lineHeight, enableCharacterAnimations, isRtl) {
        KaraokeLayoutCalculator.calculateLayout(
            line = line,
            textMeasurer = textMeasurer,
            textStyle = textStyle,
            availableWidthPx = availableWidthPx,
            lineHeight = lineHeight,
            enableCharacterAnimations = enableCharacterAnimations,
            isRtl = isRtl
        )
    }
}