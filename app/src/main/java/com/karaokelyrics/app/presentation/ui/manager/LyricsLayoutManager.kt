package com.karaokelyrics.app.presentation.ui.manager

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.karaokelyrics.app.domain.model.AnimationType
import com.karaokelyrics.app.domain.model.karaoke.KaraokeAlignment
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.domain.model.karaoke.KaraokeSyllable
import com.karaokelyrics.app.domain.usecase.lyrics.DetermineAnimationTypeUseCase
import com.karaokelyrics.app.domain.usecase.lyrics.GroupSyllablesIntoWordsUseCase
import com.karaokelyrics.app.presentation.ui.utils.LineLayout
import com.karaokelyrics.app.presentation.ui.utils.SyllableLayout
import com.karaokelyrics.app.presentation.ui.utils.WordAnimationInfo
import com.karaokelyrics.app.presentation.ui.utils.WrappedLine
import javax.inject.Inject

/**
 * Manager class for lyrics layout calculations
 * Uses domain use cases for business logic
 */
class LyricsLayoutManager @Inject constructor(
    private val groupSyllablesIntoWordsUseCase: GroupSyllablesIntoWordsUseCase,
    private val determineAnimationTypeUseCase: DetermineAnimationTypeUseCase
) {
    
    fun calculateLineLayout(
        line: KaraokeLine,
        availableWidthPx: Float,
        textMeasurer: TextMeasurer,
        style: TextStyle,
        fontSize: Int,
        isAccompanimentLine: Boolean
    ): LineLayout {
        val spaceWidth = textMeasurer.measure(" ", style).size.width.toFloat()
        val syllableLayouts = measureSyllablesAndDetermineAnimation(
            line.syllables, textMeasurer, style, isAccompanimentLine, spaceWidth
        )

        val wrappedLines = calculateWrappedLines(
            syllableLayouts, availableWidthPx, textMeasurer, style
        )

        val lineHeight = style.lineHeight.value.toInt()
        val totalHeight = wrappedLines.size * lineHeight.toFloat()

        return LineLayout(
            line = line,
            wrappedLines = wrappedLines,
            syllableLayouts = wrappedLines.map { it.syllables },
            totalHeight = totalHeight
        )
    }

    private fun measureSyllablesAndDetermineAnimation(
        syllables: List<KaraokeSyllable>,
        textMeasurer: TextMeasurer,
        style: TextStyle,
        isAccompanimentLine: Boolean,
        spaceWidth: Float
    ): List<SyllableLayout> {
        // Use domain use case to group syllables into words
        val words = groupSyllablesIntoWordsUseCase(syllables)
        val fastCharAnimationThresholdMs = 200f

        return words.flatMapIndexed { wordIndex, word ->
            val wordSyllables = word.syllables
            val wordContent = word.text
            val wordDuration = if (wordSyllables.isNotEmpty()) {
                wordSyllables.last().end - wordSyllables.first().start
            } else 0
            
            val perCharDuration = if (wordContent.isNotEmpty() && wordDuration > 0) {
                wordDuration.toFloat() / wordContent.length
            } else {
                0f
            }

            // Use domain use case to determine animation type
            val animationType = if (isAccompanimentLine) {
                AnimationType.SIMPLE
            } else {
                determineAnimationTypeUseCase(wordSyllables)
            }

            val useAwesomeAnimation = animationType == AnimationType.CHARACTER_BY_CHARACTER &&
                perCharDuration > fastCharAnimationThresholdMs && wordDuration >= 1000

            wordSyllables.map { syllable ->
                val layoutResult = textMeasurer.measure(syllable.content, style)

                // Handle trailing space width
                var layoutWidth = layoutResult.size.width.toFloat()
                if (syllable.content.endsWith(" ")) {
                    val trimmedWidth = textMeasurer.measure(syllable.content.trimEnd(), style).size.width.toFloat()
                    if (layoutWidth <= trimmedWidth) {
                        val spaceCount = syllable.content.length - syllable.content.trimEnd().length
                        layoutWidth = trimmedWidth + (spaceWidth * spaceCount)
                    }
                }

                // Pre-measure each character if needed for advanced animation
                val (charLayouts, charBounds) = if (useAwesomeAnimation) {
                    val layouts = syllable.content.map { char ->
                        textMeasurer.measure(char.toString(), style)
                    }
                    val bounds = syllable.content.indices.map { index ->
                        layoutResult.getBoundingBox(index)
                    }
                    layouts to bounds
                } else {
                    null to null
                }

                SyllableLayout(
                    syllable = syllable,
                    textLayoutResult = layoutResult,
                    wordId = wordIndex,
                    useAwesomeAnimation = useAwesomeAnimation,
                    width = layoutWidth,
                    charLayouts = charLayouts,
                    charOriginalBounds = charBounds,
                    firstBaseline = layoutResult.firstBaseline
                )
            }
        }
    }

    private fun calculateWrappedLines(
        syllableLayouts: List<SyllableLayout>,
        availableWidthPx: Float,
        textMeasurer: TextMeasurer,
        style: TextStyle
    ): List<WrappedLine> {
        val lines = mutableListOf<WrappedLine>()
        val currentLine = mutableListOf<SyllableLayout>()
        var currentLineWidth = 0f

        // Group syllables by word ID
        val wordGroups = mutableListOf<List<SyllableLayout>>()
        var currentWordGroup = mutableListOf<SyllableLayout>()
        var lastWordId = -1

        for (layout in syllableLayouts) {
            if (layout.wordId != lastWordId) {
                if (currentWordGroup.isNotEmpty()) {
                    wordGroups.add(currentWordGroup.toList())
                }
                currentWordGroup = mutableListOf()
                lastWordId = layout.wordId
            }
            currentWordGroup.add(layout)
        }
        if (currentWordGroup.isNotEmpty()) {
            wordGroups.add(currentWordGroup.toList())
        }

        val spaceWidth = textMeasurer.measure(" ", style).size.width.toFloat()

        for (wordGroup in wordGroups) {
            val totalWordWidth = wordGroup.sumOf { it.width.toDouble() }.toFloat()

            if (currentLine.isNotEmpty() && currentLineWidth + spaceWidth + totalWordWidth > availableWidthPx) {
                lines.add(WrappedLine(currentLine.toList(), currentLineWidth))
                currentLine.clear()
                currentLineWidth = 0f
            }

            if (currentLine.isNotEmpty()) {
                currentLineWidth += spaceWidth
            }

            var xPosition = currentLineWidth

            for (layout in wordGroup) {
                val updatedLayout = layout.copy(
                    position = Offset(xPosition, 0f)
                )
                currentLine.add(updatedLayout)
                xPosition += layout.width
            }

            currentLineWidth += totalWordWidth
        }

        if (currentLine.isNotEmpty()) {
            lines.add(WrappedLine(currentLine.toList(), currentLineWidth))
        }

        return lines
    }

    fun applyAlignment(
        wrappedLine: WrappedLine,
        availableWidth: Float,
        alignment: KaraokeAlignment
    ): List<SyllableLayout> {
        val alignmentOffset = when (alignment) {
            KaraokeAlignment.Start -> 0f
            KaraokeAlignment.Center -> (availableWidth - wrappedLine.totalWidth) / 2
            KaraokeAlignment.End -> availableWidth - wrappedLine.totalWidth
            KaraokeAlignment.Unspecified -> (availableWidth - wrappedLine.totalWidth) / 2
        }

        // Group syllables by word ID for animation info
        val wordGroups = mutableListOf<List<SyllableLayout>>()
        var currentGroup = mutableListOf<SyllableLayout>()
        var lastWordId = -1

        for (layout in wrappedLine.syllables) {
            if (layout.wordId != lastWordId && currentGroup.isNotEmpty()) {
                wordGroups.add(currentGroup.toList())
                currentGroup = mutableListOf()
            }
            currentGroup.add(layout)
            lastWordId = layout.wordId
        }
        if (currentGroup.isNotEmpty()) {
            wordGroups.add(currentGroup.toList())
        }

        val aligned = mutableListOf<SyllableLayout>()

        for (group in wordGroups) {
            val groupStartX = group.first().position.x + alignmentOffset
            val groupEndX = group.last().position.x + group.last().width + alignmentOffset
            val groupCenterX = (groupStartX + groupEndX) / 2
            val groupHeight = group.maxOfOrNull { it.textLayoutResult.size.height } ?: 0
            val wordPivot = Offset(groupCenterX, groupHeight / 2f)

            val wordContent = group.joinToString("") { it.syllable.content }
            val wordStartTime = group.first().syllable.start.toLong()
            val wordEndTime = group.last().syllable.end.toLong()
            val wordAnimInfo = WordAnimationInfo(wordStartTime, wordEndTime, wordContent)

            var charOffsetInWord = 0
            for (layout in group) {
                val updatedLayout = layout.copy(
                    position = layout.position.copy(x = layout.position.x + alignmentOffset),
                    wordPivot = wordPivot,
                    wordAnimInfo = wordAnimInfo,
                    charOffsetInWord = charOffsetInWord
                )
                aligned.add(updatedLayout)
                charOffsetInWord += layout.syllable.content.length
            }
        }

        return aligned
    }
}