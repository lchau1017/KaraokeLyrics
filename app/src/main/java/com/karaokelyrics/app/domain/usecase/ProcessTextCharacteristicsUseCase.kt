package com.karaokelyrics.app.domain.usecase

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.karaokelyrics.app.domain.model.karaoke.KaraokeSyllable
import com.karaokelyrics.app.data.util.TextLayoutCalculationUtil.SyllableLayout
import javax.inject.Inject

/**
 * Domain use case for processing text characteristics and determining animations.
 * Encapsulates business logic for text analysis and animation decisions.
 */
class ProcessTextCharacteristicsUseCase @Inject constructor(
    private val groupSyllablesIntoWordsUseCase: GroupSyllablesIntoWordsUseCase,
    private val determineAnimationTypeUseCase: DetermineAnimationTypeUseCase
) {

    operator fun invoke(
        syllables: List<KaraokeSyllable>,
        textMeasurer: TextMeasurer,
        style: TextStyle,
        isAccompanimentLine: Boolean,
        enableCharacterAnimations: Boolean = true
    ): List<SyllableLayout> {
        if (syllables.isEmpty()) return emptyList()

        // Group syllables into words using domain logic
        val words = groupSyllablesIntoWordsUseCase(syllables)

        // Calculate space width for accurate width measurements
        val spaceWidth = textMeasurer.measure(" ", style).size.width.toFloat()

        return words.flatMapIndexed { wordIndex, word ->
            // Determine animation type for this word
            val animationDecision = determineAnimationTypeUseCase(
                wordSyllables = word,
                isAccompanimentLine = isAccompanimentLine,
                enableCharacterAnimations = enableCharacterAnimations
            )

            word.map { syllable ->
                val layoutResult = textMeasurer.measure(syllable.content, style)

                // Calculate accurate width including trailing spaces
                val layoutWidth = calculateAccurateWidth(
                    syllable = syllable,
                    layoutResult = layoutResult,
                    textMeasurer = textMeasurer,
                    style = style,
                    spaceWidth = spaceWidth
                )

                // Pre-measure character layouts if advanced animation is needed
                val (charLayouts, charBounds) = if (animationDecision.useAwesomeAnimation) {
                    prepareCharacterLayouts(syllable, textMeasurer, style, layoutResult)
                } else {
                    null to null
                }

                SyllableLayout(
                    syllable = syllable,
                    textLayoutResult = layoutResult,
                    wordId = wordIndex,
                    useAwesomeAnimation = animationDecision.useAwesomeAnimation,
                    width = layoutWidth,
                    charLayouts = charLayouts,
                    charOriginalBounds = charBounds,
                    firstBaseline = layoutResult.firstBaseline
                )
            }
        }
    }

    private fun calculateAccurateWidth(
        syllable: KaraokeSyllable,
        layoutResult: androidx.compose.ui.text.TextLayoutResult,
        textMeasurer: TextMeasurer,
        style: TextStyle,
        spaceWidth: Float
    ): Float {
        var layoutWidth = layoutResult.size.width.toFloat()

        // Handle trailing space width correction
        if (syllable.content.endsWith(" ")) {
            val trimmedWidth = textMeasurer.measure(syllable.content.trimEnd(), style).size.width.toFloat()
            if (layoutWidth <= trimmedWidth) {
                val spaceCount = syllable.content.length - syllable.content.trimEnd().length
                layoutWidth = trimmedWidth + (spaceWidth * spaceCount)
            }
        }

        return layoutWidth
    }

    private fun prepareCharacterLayouts(
        syllable: KaraokeSyllable,
        textMeasurer: TextMeasurer,
        style: TextStyle,
        layoutResult: androidx.compose.ui.text.TextLayoutResult
    ): Pair<List<androidx.compose.ui.text.TextLayoutResult>, List<androidx.compose.ui.geometry.Rect>> {
        val charLayouts = syllable.content.map { char ->
            textMeasurer.measure(char.toString(), style)
        }
        val charBounds = syllable.content.indices.map { index ->
            layoutResult.getBoundingBox(index)
        }
        return charLayouts to charBounds
    }
}