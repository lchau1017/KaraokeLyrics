package com.karaokelyrics.app.presentation.ui.helper

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import com.karaokelyrics.app.domain.model.karaoke.KaraokeSyllable
import com.karaokelyrics.app.domain.usecase.GroupSyllablesIntoWordsUseCase
import com.karaokelyrics.app.domain.usecase.DetermineAnimationTypeUseCase
import com.karaokelyrics.app.data.util.TextLayoutCalculationUtil.SyllableLayout
import javax.inject.Inject

/**
 * Presentation layer helper for processing text characteristics and determining animations.
 * This is a UI concern and belongs in the presentation layer, not domain.
 */
class TextCharacteristicsProcessor @Inject constructor(
    private val groupSyllablesIntoWordsUseCase: GroupSyllablesIntoWordsUseCase,
    private val determineAnimationTypeUseCase: DetermineAnimationTypeUseCase
) {

    fun processSyllables(
        syllables: List<KaraokeSyllable>,
        textMeasurer: TextMeasurer,
        style: TextStyle,
        isAccompanimentLine: Boolean,
        enableCharacterAnimations: Boolean = true
    ): List<SyllableLayout> {

        // Use domain logic to group syllables into words
        val words = groupSyllablesIntoWordsUseCase(syllables)
        val spaceWidth = textMeasurer.measure(" ", style).size.width.toFloat()

        return words.flatMapIndexed { wordIndex, word ->
            // Use domain logic to determine animation type
            val animationDecision = determineAnimationTypeUseCase(
                wordSyllables = word,
                enableCharacterAnimations = enableCharacterAnimations,
                isAccompanimentLine = isAccompanimentLine
            )

            word.map { syllable ->
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
                val (charLayouts, charBounds) = if (animationDecision.useAwesomeAnimation) {
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
                    useAwesomeAnimation = animationDecision.useAwesomeAnimation,
                    width = layoutWidth,
                    charLayouts = charLayouts,
                    charOriginalBounds = charBounds,
                    firstBaseline = layoutResult.firstBaseline
                )
            }
        }
    }
}