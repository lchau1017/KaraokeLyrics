package com.karaokelyrics.app.domain.usecase

import com.karaokelyrics.app.domain.model.KyricsSyllable
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
    operator fun invoke(syllables: List<KyricsSyllable>): List<List<KyricsSyllable>> {
        if (syllables.isEmpty()) return emptyList()

        val words = mutableListOf<List<KyricsSyllable>>()
        var currentWord = mutableListOf<KyricsSyllable>()

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

    private fun hasTrailingWhitespace(content: String): Boolean = content.trimEnd().length < content.length
}
