package com.karaokelyrics.app.domain.model

/**
 * Domain model representing a single timed lyrics line.
 * Pure Kotlin - no library dependencies.
 */
data class LyricsLine(
    val start: Int,
    val end: Int,
    val syllables: List<LyricsSyllable>
)

/**
 * Domain model representing a single timed syllable within a lyrics line.
 */
data class LyricsSyllable(
    val start: Int,
    val end: Int,
    val content: String
)
