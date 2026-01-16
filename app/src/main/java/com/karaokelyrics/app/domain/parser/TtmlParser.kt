package com.karaokelyrics.app.domain.parser

import com.karaokelyrics.app.domain.model.SyncedLyrics

/**
 * Interface for TTML parsing in domain layer.
 * Implementation should be provided by data layer.
 */
interface TtmlParser {
    fun parse(lines: List<String>): SyncedLyrics
}