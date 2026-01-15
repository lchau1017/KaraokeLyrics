package com.karaokelyrics.app.presentation.ui.utils

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.karaokelyrics.app.domain.model.karaoke.KaraokeAlignment
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.domain.model.karaoke.KaraokeSyllable
import kotlin.math.pow

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
    val line: KaraokeLine,
    val wrappedLines: List<WrappedLine>,
    val syllableLayouts: List<List<SyllableLayout>>,
    val totalHeight: Float
)

fun String.shouldUseSimpleAnimation(): Boolean {
    val cleanedStr = this.filter { !it.isWhitespace() && !it.toString().isPunctuation() }
    if (cleanedStr.isEmpty()) return false
    return cleanedStr.isPureCjk() || cleanedStr.any { it.isArabic() || it.isDevanagari() }
}

fun groupIntoWords(syllables: List<KaraokeSyllable>): List<List<KaraokeSyllable>> {
    if (syllables.isEmpty()) return emptyList()
    val words = mutableListOf<List<KaraokeSyllable>>()
    var currentWord = mutableListOf<KaraokeSyllable>()
    syllables.forEach { syllable ->
        currentWord.add(syllable)
        if (syllable.content.trimEnd().length < syllable.content.length) {
            words.add(currentWord.toList())
            currentWord = mutableListOf()
        }
    }
    if (currentWord.isNotEmpty()) {
        words.add(currentWord.toList())
    }
    return words
}

fun measureSyllablesAndDetermineAnimation(
    syllables: List<KaraokeSyllable>,
    textMeasurer: TextMeasurer,
    style: TextStyle,
    isAccompanimentLine: Boolean,
    spaceWidth: Float,
    enableCharacterAnimations: Boolean = true
): List<SyllableLayout> {
    val words = groupIntoWords(syllables)
    val fastCharAnimationThresholdMs = 200f

    return words.flatMapIndexed { wordIndex, word ->
        val wordContent = word.joinToString("") { it.content }
        val wordDuration = if (word.isNotEmpty()) word.last().end - word.first().start else 0
        val perCharDuration = if (wordContent.isNotEmpty() && wordDuration > 0) {
            wordDuration.toFloat() / wordContent.length
        } else {
            0f
        }

        // More lenient conditions for character animations
        val useAwesomeAnimation = enableCharacterAnimations &&
            perCharDuration > 100f && wordDuration >= 500  // Relaxed from 200ms/1000ms
                    && !wordContent.shouldUseSimpleAnimation()
                    && !isAccompanimentLine

        word.map { syllable ->
            val layoutResult = textMeasurer.measure(syllable.content, style)

            // Fix: Handle trailing space width
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

fun calculateGreedyWrappedLines(
    syllableLayouts: List<SyllableLayout>,
    availableWidthPx: Float,
    textMeasurer: TextMeasurer,
    style: TextStyle
): List<WrappedLine> {
    val lines = mutableListOf<WrappedLine>()
    val currentLine = mutableListOf<SyllableLayout>()
    var currentLineWidth = 0f

    val wordGroups = mutableListOf<List<SyllableLayout>>()
    if (syllableLayouts.isNotEmpty()) {
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
    }

    // Layout by words
    wordGroups.forEach { wordSyllables ->
        val wordWidth = wordSyllables.sumOf { it.width.toDouble() }.toFloat()

        // If current line can fit the word
        if (currentLineWidth + wordWidth <= availableWidthPx) {
            currentLine.addAll(wordSyllables)
            currentLineWidth += wordWidth
        } else {
            // Can't fit, wrap line if not empty
            if (currentLine.isNotEmpty()) {
                val trimmedDisplayLine = trimDisplayLineTrailingSpaces(currentLine, textMeasurer, style)
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
                        val trimmedLine = trimDisplayLineTrailingSpaces(currentLine, textMeasurer, style)
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
        val trimmedDisplayLine = trimDisplayLineTrailingSpaces(currentLine, textMeasurer, style)
        if (trimmedDisplayLine.syllables.isNotEmpty()) {
            lines.add(trimmedDisplayLine)
        }
    }
    return lines
}

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

fun trimDisplayLineTrailingSpaces(
    displayLineSyllables: List<SyllableLayout>,
    textMeasurer: TextMeasurer,
    style: TextStyle
): WrappedLine {
    if (displayLineSyllables.isEmpty()) {
        return WrappedLine(emptyList(), 0f)
    }

    val processedSyllables = displayLineSyllables.toMutableList()
    var lastIndex = processedSyllables.lastIndex

    while (lastIndex >= 0 && processedSyllables[lastIndex].syllable.content.isBlank()) {
        processedSyllables.removeAt(lastIndex)
        lastIndex--
    }

    if (processedSyllables.isEmpty()) {
        return WrappedLine(emptyList(), 0f)
    }

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