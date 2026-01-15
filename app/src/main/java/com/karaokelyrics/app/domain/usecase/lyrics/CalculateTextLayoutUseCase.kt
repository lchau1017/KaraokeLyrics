package com.karaokelyrics.app.domain.usecase.lyrics

import com.karaokelyrics.app.domain.model.TextLayout
import com.karaokelyrics.app.domain.model.karaoke.KaraokeSyllable
import javax.inject.Inject

/**
 * Use case for calculating text layout for lyrics display
 * Single Responsibility: Calculate optimal text layout based on syllables
 */
class CalculateTextLayoutUseCase @Inject constructor(
    private val groupSyllablesIntoWordsUseCase: GroupSyllablesIntoWordsUseCase
) {
    
    operator fun invoke(
        syllables: List<KaraokeSyllable>,
        maxWidth: Float,
        measureWidth: (String) -> Float
    ): TextLayout {
        val words = groupSyllablesIntoWordsUseCase(syllables)
        val lines = calculateWrappedLines(words, maxWidth, measureWidth)
        
        return TextLayout(
            lines = lines,
            totalHeight = lines.size * DEFAULT_LINE_HEIGHT
        )
    }
    
    private fun calculateWrappedLines(
        words: List<Word>,
        maxWidth: Float,
        measureWidth: (String) -> Float
    ): List<TextLayout.Line> {
        val lines = mutableListOf<TextLayout.Line>()
        var currentLine = mutableListOf<Word>()
        var currentWidth = 0f
        
        for (word in words) {
            val wordWidth = measureWidth(word.text)
            val spaceWidth = if (currentLine.isNotEmpty()) measureWidth(" ") else 0f
            
            if (currentWidth + spaceWidth + wordWidth > maxWidth && currentLine.isNotEmpty()) {
                // Start new line
                lines.add(TextLayout.Line(currentLine.toList()))
                currentLine = mutableListOf(word)
                currentWidth = wordWidth
            } else {
                // Add to current line
                currentLine.add(word)
                currentWidth += spaceWidth + wordWidth
            }
        }
        
        // Add remaining words
        if (currentLine.isNotEmpty()) {
            lines.add(TextLayout.Line(currentLine))
        }
        
        return lines
    }
    
    companion object {
        private const val DEFAULT_LINE_HEIGHT = 1.2f
    }
}

data class Word(
    val text: String,
    val syllables: List<KaraokeSyllable>
)