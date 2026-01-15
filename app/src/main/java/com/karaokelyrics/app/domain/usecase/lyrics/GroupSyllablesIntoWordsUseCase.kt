package com.karaokelyrics.app.domain.usecase.lyrics

import com.karaokelyrics.app.domain.model.karaoke.KaraokeSyllable
import com.karaokelyrics.app.domain.util.TextAnalysisUtils
import javax.inject.Inject

/**
 * Use case for grouping syllables into words
 * Single Responsibility: Determine word boundaries in lyrics
 */
class GroupSyllablesIntoWordsUseCase @Inject constructor() {
    
    operator fun invoke(syllables: List<KaraokeSyllable>): List<Word> {
        if (syllables.isEmpty()) return emptyList()
        
        val words = mutableListOf<Word>()
        var currentWordSyllables = mutableListOf<KaraokeSyllable>()
        
        for (syllable in syllables) {
            val text = syllable.content
            
            // Business rules for word boundaries
            when {
                text.isEmpty() -> continue
                TextAnalysisUtils.isPunctuation(text) -> {
                    // Punctuation attaches to previous word
                    if (currentWordSyllables.isNotEmpty()) {
                        currentWordSyllables.add(syllable)
                    }
                }
                text.contains(' ') -> {
                    // Space indicates word boundary
                    val parts = text.split(' ')
                    if (parts.first().isNotEmpty()) {
                        currentWordSyllables.add(syllable.copy(content = parts.first()))
                    }
                    if (currentWordSyllables.isNotEmpty()) {
                        words.add(createWord(currentWordSyllables))
                        currentWordSyllables = mutableListOf()
                    }
                    if (parts.size > 1 && parts[1].isNotEmpty()) {
                        currentWordSyllables.add(syllable.copy(content = parts[1]))
                    }
                }
                shouldStartNewWord(syllable, currentWordSyllables) -> {
                    // Start new word based on language rules
                    if (currentWordSyllables.isNotEmpty()) {
                        words.add(createWord(currentWordSyllables))
                    }
                    currentWordSyllables = mutableListOf(syllable)
                }
                else -> {
                    // Continue current word
                    currentWordSyllables.add(syllable)
                }
            }
        }
        
        // Add final word
        if (currentWordSyllables.isNotEmpty()) {
            words.add(createWord(currentWordSyllables))
        }
        
        return words
    }
    
    private fun shouldStartNewWord(
        syllable: KaraokeSyllable,
        currentWord: List<KaraokeSyllable>
    ): Boolean {
        if (currentWord.isEmpty()) return true
        
        val currentText = currentWord.joinToString("") { it.content }
        val syllableText = syllable.content
        
        // CJK characters are typically individual words
        if (TextAnalysisUtils.isPureCjk(syllableText)) {
            return true
        }
        
        // Large timing gap indicates word boundary
        val lastSyllable = currentWord.last()
        val timingGap = syllable.start - lastSyllable.end
        if (timingGap > WORD_BOUNDARY_TIMING_THRESHOLD) {
            return true
        }
        
        return false
    }
    
    private fun createWord(syllables: List<KaraokeSyllable>): Word {
        return Word(
            text = syllables.joinToString("") { it.content },
            syllables = syllables.toList()
        )
    }
    
    companion object {
        private const val WORD_BOUNDARY_TIMING_THRESHOLD = 100 // milliseconds
    }
}