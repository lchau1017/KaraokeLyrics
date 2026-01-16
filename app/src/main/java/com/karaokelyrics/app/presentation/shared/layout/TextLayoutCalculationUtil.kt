package com.karaokelyrics.app.presentation.shared.layout

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.karaokelyrics.app.domain.model.karaoke.KaraokeSyllable

/**
 * Data layer utility containing pure text layout calculation algorithms.
 * These functions contain no business logic - only mathematical calculations.
 */
object TextLayoutCalculationUtil {

    @Stable
    data class SyllableLayout(
        val syllable: KaraokeSyllable,
        val textLayoutResult: TextLayoutResult,
        val wordId: Int,
        val useAwesomeAnimation: Boolean,
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
        val totalDuration: Int = 0 // Added for progress calculations
    )

    /**
     * Calculates wrapped lines using a greedy line-wrapping algorithm.
     * Pure algorithm with no business logic dependencies.
     */
    fun calculateGreedyWrappedLines(
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
                    val trimmedDisplayLine = trimLineTrailingSpaces(currentLine, textMeasurer, style)
                    if (trimmedDisplayLine.syllables.isNotEmpty()) {
                        lines.add(trimmedDisplayLine)
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
                            if (trimmedLine.syllables.isNotEmpty()) lines.add(trimmedLine)
                            currentLine.clear()
                            currentLineWidth = 0f
                        }
                        currentLine.add(syllable)
                        currentLineWidth += syllable.width
                    }
                }
            }
        }

        // Handle last line
        if (currentLine.isNotEmpty()) {
            val trimmedDisplayLine = trimLineTrailingSpaces(currentLine, textMeasurer, style)
            if (trimmedDisplayLine.syllables.isNotEmpty()) {
                lines.add(trimmedDisplayLine)
            }
        }
        return lines
    }

    /**
     * Calculates final positioning for syllables in wrapped lines.
     * Pure positioning algorithm with no business logic.
     */
    fun calculateStaticLineLayout(
        wrappedLines: List<WrappedLine>,
        isLineRightAligned: Boolean,
        canvasWidth: Float,
        lineHeight: Float,
        isRtl: Boolean
    ): List<List<SyllableLayout>> {
        val layoutsByWord = mutableMapOf<Int, MutableList<SyllableLayout>>()

        val positionedLines = wrappedLines.mapIndexed { lineIndex, wrappedLine ->
            val maxBaselineInLine = wrappedLine.syllables.maxOfOrNull { it.firstBaseline } ?: 0f
            val rowTopY = lineIndex * lineHeight

            val startX = if (isLineRightAligned) {
                canvasWidth - wrappedLine.totalWidth
            } else {
                0f
            }

            var currentX = if (isRtl) startX + wrappedLine.totalWidth else startX

            wrappedLine.syllables.map { initialLayout ->
                val positionX = if (isRtl) {
                    currentX - initialLayout.width
                } else {
                    currentX
                }
                val verticalOffset = maxBaselineInLine - initialLayout.firstBaseline
                val positionY = rowTopY + verticalOffset
                val positionedLayout = initialLayout.copy(position = Offset(positionX, positionY))
                layoutsByWord.getOrPut(positionedLayout.wordId) { mutableListOf() }
                    .add(positionedLayout)

                if (isRtl) {
                    currentX -= positionedLayout.width
                } else {
                    currentX += positionedLayout.width
                }

                positionedLayout
            }
        }

        // Calculate word animation info and character offsets
        val animInfoByWord = mutableMapOf<Int, WordAnimationInfo>()
        val charOffsetsBySyllable = mutableMapOf<SyllableLayout, Int>()

        layoutsByWord.forEach { (wordId, layouts) ->
            if (layouts.first().useAwesomeAnimation) {
                animInfoByWord[wordId] = WordAnimationInfo(
                    wordStartTime = layouts.minOf { it.syllable.start }.toLong(),
                    wordEndTime = layouts.maxOf { it.syllable.end }.toLong(),
                    wordContent = layouts.joinToString("") { it.syllable.content })
                var runningCharOffset = 0
                layouts.forEach { layout ->
                    charOffsetsBySyllable[layout] = runningCharOffset
                    runningCharOffset += layout.syllable.content.length
                }
            }
        }

        return positionedLines.map { line ->
            line.map { positionedLayout ->
                val wordLayouts = layoutsByWord.getValue(positionedLayout.wordId)
                val minX = wordLayouts.minOf { it.position.x }
                val maxX = wordLayouts.maxOf { it.position.x + it.width }
                val bottomY = wordLayouts.maxOf { it.position.y + it.textLayoutResult.size.height }

                positionedLayout.copy(
                    wordPivot = Offset(x = (minX + maxX) / 2f, y = bottomY),
                    wordAnimInfo = animInfoByWord[positionedLayout.wordId],
                    charOffsetInWord = charOffsetsBySyllable[positionedLayout] ?: 0
                )
            }
        }
    }

    private fun groupSyllablesByWord(syllableLayouts: List<SyllableLayout>): List<List<SyllableLayout>> {
        if (syllableLayouts.isEmpty()) return emptyList()

        val wordGroups = mutableListOf<List<SyllableLayout>>()
        var currentWordGroup = mutableListOf<SyllableLayout>()
        var currentWordId = syllableLayouts.first().wordId

        syllableLayouts.forEach { layout ->
            if (layout.wordId != currentWordId) {
                wordGroups.add(currentWordGroup)
                currentWordGroup = mutableListOf()
                currentWordId = layout.wordId
            }
            currentWordGroup.add(layout)
        }
        wordGroups.add(currentWordGroup)

        return wordGroups
    }

    private fun trimLineTrailingSpaces(
        displayLineSyllables: List<SyllableLayout>,
        textMeasurer: TextMeasurer,
        style: TextStyle
    ): WrappedLine {
        if (displayLineSyllables.isEmpty()) {
            return WrappedLine(emptyList(), 0f)
        }

        val processedSyllables = displayLineSyllables.toMutableList()
        var lastIndex = processedSyllables.lastIndex

        // Remove trailing blank syllables
        while (lastIndex >= 0 && processedSyllables[lastIndex].syllable.content.isBlank()) {
            processedSyllables.removeAt(lastIndex)
            lastIndex--
        }

        if (processedSyllables.isEmpty()) {
            return WrappedLine(emptyList(), 0f)
        }

        // Trim trailing spaces from last syllable
        val lastLayout = processedSyllables.last()
        val originalContent = lastLayout.syllable.content
        val trimmedContent = originalContent.trimEnd()

        if (trimmedContent.length < originalContent.length) {
            if (trimmedContent.isNotEmpty()) {
                val trimmedLayoutResult = textMeasurer.measure(trimmedContent, style)
                val trimmedLayout = lastLayout.copy(
                    syllable = lastLayout.syllable.copy(content = trimmedContent),
                    textLayoutResult = trimmedLayoutResult,
                    width = trimmedLayoutResult.size.width.toFloat()
                )
                processedSyllables[processedSyllables.lastIndex] = trimmedLayout
            } else {
                processedSyllables.removeAt(processedSyllables.lastIndex)
            }
        }

        val totalWidth = processedSyllables.sumOf { it.width.toDouble() }.toFloat()
        return WrappedLine(processedSyllables, totalWidth)
    }
}