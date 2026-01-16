package com.karaokelyrics.app.data.parser

/**
 * Interface for lyrics parsers.
 * Follows Open/Closed Principle - new formats can be added without modifying existing code.
 */
interface LyricsParser {
    /**
     * Parse lyrics content into a structured format.
     *
     * @param content The raw lyrics content to parse
     * @return Parsed lyrics data
     * @throws ParseException if parsing fails
     */
    suspend fun parse(content: String): ParsedLyricsData
}

/**
 * Raw parsed data from a lyrics file.
 * This is an intermediate representation before domain model creation.
 */
data class ParsedLyricsData(
    val lines: List<ParsedLine>
)

/**
 * A parsed line of lyrics.
 */
data class ParsedLine(
    val syllables: List<ParsedSyllable>,
    val startMs: Int,
    val endMs: Int,
    val isBackgroundVocal: Boolean = false
)

/**
 * A parsed syllable.
 */
data class ParsedSyllable(
    val content: String,
    val startMs: Int,
    val endMs: Int
)

/**
 * Exception thrown when parsing fails.
 */
class ParseException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)