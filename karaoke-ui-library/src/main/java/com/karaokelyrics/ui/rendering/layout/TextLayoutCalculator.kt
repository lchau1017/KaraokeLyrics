package com.karaokelyrics.ui.rendering.layout

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.karaokelyrics.ui.core.models.KaraokeSyllable

/**
 * Text layout calculation utility for the karaoke library.
 * Contains pure layout algorithms with no business logic.
 */
object TextLayoutCalculator {

    @Stable
    data class SyllableLayout(
        val syllable: KaraokeSyllable,
        val textLayoutResult: TextLayoutResult,
        val wordId: Int,
        val useCharacterAnimation: Boolean,
        val width: Float = textLayoutResult.size.width.toFloat(),
        val position: Offset = Offset.Zero,
        val wordPivot: Offset = Offset.Zero,
        val wordAnimInfo: WordAnimationInfo? = null,
        val charOffsetInWord: Int = 0,
        val charLayouts: List<TextLayoutResult>? = null,
        val charOriginalBounds: List<Rect>? = null,
        val firstBaseline: Float = 0f,
    )

    @Stable
    data class WordAnimationInfo(
        val wordStartTime: Long,
        val wordEndTime: Long,
        val wordContent: String,
        val wordDuration: Long = wordEndTime - wordStartTime
    )

    @Stable
    data class WrappedLine(
        val syllables: List<SyllableLayout>,
        val totalWidth: Float
    )

    @Stable
    data class LineLayout(
        val syllableLayouts: List<List<SyllableLayout>>,
        val totalHeight: Float,
        val lineHeight: Float,
        val rows: Int,
        val totalDuration: Int = 0
    )

    /**
     * Calculates wrapped lines using a greedy line-wrapping algorithm.
     */
    fun calculateWrappedLines(
        syllableLayouts: List<SyllableLayout>,
        availableWidthPx: Float,
        textMeasurer: TextMeasurer,
        style: TextStyle
    ): List<WrappedLine> {
        val lines = mutableListOf<WrappedLine>()
        val currentLine = mutableListOf<SyllableLayout>()
        var currentLineWidth = 0f

        // Group syllables by words for proper wrapping
        val wordGroups = groupSyllablesByWord(syllableLayouts)

        // Layout by words to maintain word integrity
        wordGroups.forEach { wordSyllables ->
            val wordWidth = wordSyllables.sumOf { it.width.toDouble() }.toFloat()

            // If current line can fit the word
            if (currentLineWidth + wordWidth <= availableWidthPx) {
                currentLine.addAll(wordSyllables)
                currentLineWidth += wordWidth
            } else {
                // Can't fit, wrap line if not empty
                if (currentLine.isNotEmpty()) {
                    val trimmedLine = trimLineTrailingSpaces(currentLine, textMeasurer, style)
                    if (trimmedLine.syllables.isNotEmpty()) {
                        lines.add(trimmedLine)
                    }
                    currentLine.clear()
                    currentLineWidth = 0f
                }

                // Check if new line can fit the word
                if (wordWidth <= availableWidthPx) {
                    currentLine.addAll(wordSyllables)
                    currentLineWidth += wordWidth
                } else {
                    // Special case: very long word, break by syllables
                    wordSyllables.forEach { syllable ->
                        if (currentLineWidth + syllable.width > availableWidthPx && currentLine.isNotEmpty()) {
                            val trimmedLine = trimLineTrailingSpaces(currentLine, textMeasurer, style)
                            if (trimmedLine.syllables.isNotEmpty()) {
                                lines.add(trimmedLine)
                            }
                            currentLine.clear()
                            currentLineWidth = 0f
                        }
                        currentLine.add(syllable)
                        currentLineWidth += syllable.width
                    }
                }
            }
        }

        // Add any remaining line
        if (currentLine.isNotEmpty()) {
            val trimmedLine = trimLineTrailingSpaces(currentLine, textMeasurer, style)
            if (trimmedLine.syllables.isNotEmpty()) {
                lines.add(trimmedLine)
            }
        }

        return lines
    }

    /**
     * Calculates static positions for each syllable in wrapped lines.
     */
    fun calculateStaticLineLayout(
        wrappedLines: List<WrappedLine>,
        isLineRightAligned: Boolean,
        canvasWidth: Float,
        lineHeight: Float,
        isRtl: Boolean
    ): List<List<SyllableLayout>> {
        return wrappedLines.mapIndexed { lineIndex, wrappedLine ->
            val yPosition = lineIndex * lineHeight

            val lineStartX = if (isLineRightAligned) {
                canvasWidth - wrappedLine.totalWidth
            } else {
                (canvasWidth - wrappedLine.totalWidth) / 2f
            }

            var currentX = lineStartX
            wrappedLine.syllables.map { syllableLayout ->
                val xPosition = if (isRtl && !isLineRightAligned) {
                    canvasWidth - currentX - syllableLayout.width
                } else {
                    currentX
                }

                val positioned = syllableLayout.copy(
                    position = Offset(xPosition, yPosition)
                )
                currentX += syllableLayout.width
                positioned
            }
        }
    }

    /**
     * Groups syllables by word ID.
     */
    private fun groupSyllablesByWord(syllables: List<SyllableLayout>): List<List<SyllableLayout>> {
        if (syllables.isEmpty()) return emptyList()

        val groups = mutableListOf<MutableList<SyllableLayout>>()
        var currentWordId = syllables.first().wordId
        var currentGroup = mutableListOf<SyllableLayout>()

        syllables.forEach { syllable ->
            if (syllable.wordId != currentWordId) {
                if (currentGroup.isNotEmpty()) {
                    groups.add(currentGroup)
                }
                currentGroup = mutableListOf()
                currentWordId = syllable.wordId
            }
            currentGroup.add(syllable)
        }

        if (currentGroup.isNotEmpty()) {
            groups.add(currentGroup)
        }

        return groups
    }

    /**
     * Trims trailing spaces from a line.
     */
    private fun trimLineTrailingSpaces(
        line: List<SyllableLayout>,
        textMeasurer: TextMeasurer,
        style: TextStyle
    ): WrappedLine {
        if (line.isEmpty()) return WrappedLine(emptyList(), 0f)

        val trimmedLine = line.toMutableList()
        val lastIndex = trimmedLine.lastIndex
        val lastSyllable = trimmedLine[lastIndex].syllable
        val trimmedContent = lastSyllable.content.trimEnd()

        if (trimmedContent != lastSyllable.content) {
            val trimmedSyllable = lastSyllable.copy(content = trimmedContent)
            val trimmedLayoutResult = textMeasurer.measure(
                buildAnnotatedString { append(trimmedContent) },
                style
            )
            trimmedLine[lastIndex] = trimmedLine[lastIndex].copy(
                syllable = trimmedSyllable,
                textLayoutResult = trimmedLayoutResult,
                width = trimmedLayoutResult.size.width.toFloat()
            )
        }

        val totalWidth = trimmedLine.sumOf { it.width.toDouble() }.toFloat()
        return WrappedLine(trimmedLine, totalWidth)
    }
}