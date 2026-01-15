package com.karaokelyrics.app.domain.usecase.lyrics

import com.karaokelyrics.app.domain.model.ISyncedLine
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import javax.inject.Inject

/**
 * Use case for classifying lyrics lines (main vs accompaniment)
 * Single Responsibility: Determine line types based on business rules
 */
class ClassifyLyricsLinesUseCase @Inject constructor() {
    
    operator fun invoke(lines: List<ISyncedLine>): List<ISyncedLine> {
        return lines.map { line ->
            if (line is KaraokeLine) {
                classifyLine(line, lines)
            } else {
                line
            }
        }
    }
    
    private fun classifyLine(line: KaraokeLine, allLines: List<ISyncedLine>): KaraokeLine {
        // Business rule: Lines with specific metadata are accompaniment
        val isAccompaniment = determineIfAccompaniment(line, allLines)
        
        return line.copy(isAccompaniment = isAccompaniment)
    }
    
    private fun determineIfAccompaniment(
        line: KaraokeLine,
        allLines: List<ISyncedLine>
    ): Boolean {
        // Business rule: Background vocals are accompaniment
        if (line.metadata?.get("role") == "x-bg") {
            return true
        }
        
        // Business rule: Overlapping lines where one is shorter are accompaniment
        val overlappingLines = allLines.filterIsInstance<KaraokeLine>().filter { other ->
            other != line && linesOverlap(line, other)
        }
        
        if (overlappingLines.isNotEmpty()) {
            val lineDuration = line.end - line.start
            val hasLongerOverlap = overlappingLines.any { other ->
                (other.end - other.start) > lineDuration * 1.5
            }
            if (hasLongerOverlap) {
                return true
            }
        }
        
        return false
    }
    
    private fun linesOverlap(line1: KaraokeLine, line2: KaraokeLine): Boolean {
        return line1.start < line2.end && line2.start < line1.end
    }
}

// Extension to support metadata in KaraokeLine
val KaraokeLine.metadata: Map<String, String>?
    get() = null // This would be populated from parser