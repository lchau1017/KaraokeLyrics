package com.kyrics.testdata

import com.kyrics.models.ISyncedLine
import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable

/**
 * Test data fixtures for unit tests.
 * Provides consistent test data across all tests.
 */
object TestData {

    /**
     * Simple implementation of ISyncedLine for testing
     */
    data class SimpleSyncedLine(override val start: Int, override val end: Int, private val content: String) : ISyncedLine {
        override fun getContent(): String = content
    }

    /**
     * Creates a list of simple synced lines for testing.
     * Each line is 2 seconds long with 500ms gap between lines.
     *
     * Timeline:
     * Line 0: 0ms - 2000ms ("Hello world")
     * Line 1: 2500ms - 4500ms ("This is a test")
     * Line 2: 5000ms - 7000ms ("Karaoke lyrics")
     * Line 3: 7500ms - 9500ms ("Are fun to sing")
     * Line 4: 10000ms - 12000ms ("The end")
     */
    fun createSimpleLines(): List<ISyncedLine> = listOf(
        SimpleSyncedLine(start = 0, end = 2000, content = "Hello world"),
        SimpleSyncedLine(start = 2500, end = 4500, content = "This is a test"),
        SimpleSyncedLine(start = 5000, end = 7000, content = "Karaoke lyrics"),
        SimpleSyncedLine(start = 7500, end = 9500, content = "Are fun to sing"),
        SimpleSyncedLine(start = 10000, end = 12000, content = "The end")
    )

    /**
     * Creates a KyricsLine with syllable-level timing.
     *
     * "Hel-lo World" with timing:
     * - "Hel" : 0ms - 300ms
     * - "lo " : 300ms - 600ms
     * - "World" : 600ms - 1000ms
     */
    fun createKyricsLineWithSyllables(): KyricsLine = KyricsLine(
        syllables = listOf(
            KyricsSyllable(content = "Hel", start = 0, end = 300),
            KyricsSyllable(content = "lo ", start = 300, end = 600),
            KyricsSyllable(content = "World", start = 600, end = 1000)
        ),
        start = 0,
        end = 1000
    )

    /**
     * Creates a list of KyricsLines with syllable timing for testing.
     */
    fun createKyricsLines(): List<KyricsLine> = listOf(
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("Hel", 0, 300),
                KyricsSyllable("lo ", 300, 600),
                KyricsSyllable("World", 600, 1000)
            ),
            start = 0,
            end = 1000
        ),
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("Test", 1500, 1800),
                KyricsSyllable("ing", 1800, 2000)
            ),
            start = 1500,
            end = 2000
        ),
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("Ka", 2500, 2700),
                KyricsSyllable("ra", 2700, 2900),
                KyricsSyllable("o", 2900, 3000),
                KyricsSyllable("ke", 3000, 3200)
            ),
            start = 2500,
            end = 3200
        )
    )

    /**
     * Creates an accompaniment line (background vocals).
     */
    fun createAccompanimentLine(): KyricsLine = KyricsLine(
        syllables = listOf(
            KyricsSyllable("(ooh)", 0, 500)
        ),
        start = 0,
        end = 500,
        metadata = mapOf("type" to "accompaniment")
    )

    /**
     * Test time points for various scenarios
     */
    object TimePoints {
        // For createSimpleLines()
        const val BEFORE_ALL_LINES = -500 // Before any line starts
        const val LINE_0_PLAYING = 1000 // Middle of line 0
        const val BETWEEN_LINE_0_AND_1 = 2250 // Gap between lines
        const val LINE_1_PLAYING = 3500 // Middle of line 1
        const val LINE_2_START = 5000 // Exactly at line 2 start
        const val LINE_2_END = 7000 // Exactly at line 2 end
        const val LINE_4_PLAYING = 11000 // Last line playing
        const val AFTER_ALL_LINES = 15000 // After all lines
    }
}
