package com.karaokelyrics.app.presentation.ui.manager

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.domain.usecase.CalculateTextLayoutUseCase
import com.karaokelyrics.app.presentation.ui.utils.LineLayout
import javax.inject.Inject

/**
 * Presentation layer manager that coordinates domain use cases for lyrics layout.
 * This follows Clean Architecture principles by delegating business logic to domain layer.
 */
class LyricsLayoutManager @Inject constructor(
    private val calculateTextLayoutUseCase: CalculateTextLayoutUseCase
) {

    /**
     * Calculates the complete layout for a karaoke line using domain use cases.
     * This is the main entry point for all text layout operations.
     */
    fun calculateLineLayout(
        line: KaraokeLine,
        textMeasurer: TextMeasurer,
        textStyle: TextStyle,
        availableWidthPx: Float,
        lineHeight: Float,
        canvasWidth: Float,
        enableCharacterAnimations: Boolean = true,
        isRtl: Boolean = false
    ): LineLayout {
        return calculateTextLayoutUseCase(
            line = line,
            textMeasurer = textMeasurer,
            textStyle = textStyle,
            availableWidthPx = availableWidthPx,
            lineHeight = lineHeight,
            canvasWidth = canvasWidth,
            enableCharacterAnimations = enableCharacterAnimations,
            isRtl = isRtl
        )
    }

    /**
     * Creates a simplified single-line layout for basic rendering.
     * Useful for simple cases where multi-line wrapping is not needed.
     */
    fun calculateSimpleLayout(
        line: KaraokeLine,
        textMeasurer: TextMeasurer,
        textStyle: TextStyle,
        canvasWidth: Float,
        enableCharacterAnimations: Boolean = true
    ): LineLayout {
        // For simple layout, use available width equal to canvas width
        // and a default line height based on text style
        val defaultLineHeight = textStyle.fontSize.value * 1.2f

        return calculateTextLayoutUseCase(
            line = line,
            textMeasurer = textMeasurer,
            textStyle = textStyle,
            availableWidthPx = canvasWidth,
            lineHeight = defaultLineHeight,
            canvasWidth = canvasWidth,
            enableCharacterAnimations = enableCharacterAnimations,
            isRtl = false
        )
    }
}