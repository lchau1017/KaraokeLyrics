package com.karaokelyrics.app.domain.usecase.lyrics

import com.karaokelyrics.app.data.parser.TtmlParser
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.model.karaoke.KaraokeAlignment
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import javax.inject.Inject

/**
 * Use case for parsing TTML with business logic
 * Single Responsibility: Apply business rules to parsed TTML data
 */
class ParseTtmlUseCase @Inject constructor(
    private val ttmlParser: TtmlParser,
    private val classifyLyricsLinesUseCase: ClassifyLyricsLinesUseCase
) {
    
    operator fun invoke(ttmlContent: String): SyncedLyrics {
        // Parse raw TTML data
        val rawLyrics = ttmlParser.parse(ttmlContent)
        
        // Apply business rules
        val processedLines = rawLyrics.lines.map { line ->
            if (line is KaraokeLine) {
                processLine(line)
            } else {
                line
            }
        }
        
        // Classify and sort lines
        val classifiedLines = classifyLyricsLinesUseCase(processedLines)
        
        return SyncedLyrics(classifiedLines.sortedBy { 
            (it as? KaraokeLine)?.start ?: 0 
        })
    }
    
    private fun processLine(line: KaraokeLine): KaraokeLine {
        // Business rule: Trim trailing spaces from last syllable
        val processedSyllables = line.syllables.toMutableList()
        if (processedSyllables.isNotEmpty()) {
            val lastIndex = processedSyllables.lastIndex
            processedSyllables[lastIndex] = processedSyllables[lastIndex].copy(
                content = processedSyllables[lastIndex].content.trimEnd()
            )
        }
        
        // Business rule: Default alignment is Center
        val alignment = line.alignment ?: KaraokeAlignment.Center
        
        return line.copy(
            syllables = processedSyllables,
            alignment = alignment
        )
    }
}