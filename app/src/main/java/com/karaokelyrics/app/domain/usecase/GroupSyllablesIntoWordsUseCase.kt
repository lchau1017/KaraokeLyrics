package com.karaokelyrics.app.domain.usecase

import com.karaokelyrics.app.domain.model.karaoke.KaraokeSyllable
import javax.inject.Inject

/**
 * Domain use case for grouping syllables into words.
 * Encapsulates the business logic for determining word boundaries.
 */
class GroupSyllablesIntoWordsUseCase @Inject constructor() {

    /**
     * Groups syllables into words based on trailing whitespace.
     * A word ends when a syllable contains trailing whitespace.
     */
    operator fun invoke(syllables: List<KaraokeSyllable>): List<List<KaraokeSyllable>> {
        if (syllables.isEmpty()) return emptyList()

        val words = mutableListOf<List<KaraokeSyllable>>()
        var currentWord = mutableListOf<KaraokeSyllable>()

        syllables.forEach { syllable ->
            currentWord.add(syllable)

            // Word ends if syllable has trailing whitespace
            if (hasTrailingWhitespace(syllable.content)) {
                words.add(currentWord.toList())
                currentWord = mutableListOf()
            }
        }

        // Add the last word if it exists
        if (currentWord.isNotEmpty()) {
            words.add(currentWord.toList())
        }

        return words
    }

    private fun hasTrailingWhitespace(content: String): Boolean {
        return content.trimEnd().length < content.length
    }
}