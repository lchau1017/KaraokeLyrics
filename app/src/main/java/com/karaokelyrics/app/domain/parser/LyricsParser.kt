package com.karaokelyrics.app.domain.parser

import com.karaokelyrics.app.domain.model.LyricsLine

/**
 * Domain-owned interface for parsing raw lyrics file content.
 * Implementation lives in the data layer.
 */
interface LyricsParser {
    fun parse(lines: List<String>): List<LyricsLine>
}
