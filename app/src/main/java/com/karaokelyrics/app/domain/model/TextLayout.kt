package com.karaokelyrics.app.domain.model

import com.karaokelyrics.app.domain.usecase.lyrics.Word

/**
 * Domain model for text layout calculation result
 */
data class TextLayout(
    val lines: List<Line>,
    val totalHeight: Float
) {
    data class Line(
        val words: List<Word>
    ) {
        val text: String
            get() = words.joinToString(" ") { it.text }
    }
}