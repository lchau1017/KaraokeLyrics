package com.karaokelyrics.app.presentation.ui.utils

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.karaokelyrics.app.domain.model.karaoke.KaraokeAlignment
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.presentation.ui.manager.LyricsLayoutManager
import kotlin.math.pow

/**
 * Standalone layout functions for backwards compatibility
 * These create a temporary LyricsLayoutManager instance
 */

private val defaultLayoutManager by lazy {
    // Create a default instance with basic implementations
    // In production, this should be injected
    val groupUseCase = com.karaokelyrics.app.domain.usecase.lyrics.GroupSyllablesIntoWordsUseCase()
    val animationUseCase = com.karaokelyrics.app.domain.usecase.lyrics.DetermineAnimationTypeUseCase()
    LyricsLayoutManager(groupUseCase, animationUseCase)
}

fun calculateLineLayout(
    line: KaraokeLine,
    availableWidthPx: Float,
    textMeasurer: TextMeasurer,
    style: TextStyle,
    fontSize: Int,
    isAccompanimentLine: Boolean
): LineLayout {
    return defaultLayoutManager.calculateLineLayout(
        line = line,
        availableWidthPx = availableWidthPx,
        textMeasurer = textMeasurer,
        style = style,
        fontSize = fontSize,
        isAccompanimentLine = isAccompanimentLine
    )
}

fun applyAlignmentToWrappedLine(
    wrappedLine: WrappedLine,
    availableWidth: Float,
    alignment: KaraokeAlignment,
    spaceWidth: Float = 0f
): List<SyllableLayout> {
    return defaultLayoutManager.applyAlignment(
        wrappedLine = wrappedLine,
        availableWidth = availableWidth,
        alignment = alignment
    )
}

// Easing function remains as utility
fun awesomeEasing(t: Float): Float {
    return 1 - (1 - t).pow(3)
}