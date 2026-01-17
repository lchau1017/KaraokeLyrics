package com.karaokelyrics.demo.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for DemoLyricsProvider.
 * Verifies the demo lyrics data is correctly structured.
 */
class DemoLyricsProviderTest {

    // ==================== Basic Structure Tests ====================

    @Test
    fun `createDemoLyrics returns non-empty list`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        assertThat(lyrics).isNotEmpty()
    }

    @Test
    fun `createDemoLyrics returns 9 lines`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        assertThat(lyrics).hasSize(9)
    }

    @Test
    fun `all lines have syllables`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        lyrics.forEach { line ->
            assertThat(line.syllables).isNotEmpty()
        }
    }

    // ==================== Timing Consistency Tests ====================

    @Test
    fun `line start matches first syllable start`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        lyrics.forEach { line ->
            val firstSyllable = line.syllables.first()
            assertThat(line.start).isEqualTo(firstSyllable.start)
        }
    }

    @Test
    fun `line end matches last syllable end`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        lyrics.forEach { line ->
            val lastSyllable = line.syllables.last()
            assertThat(line.end).isEqualTo(lastSyllable.end)
        }
    }

    @Test
    fun `syllables within line are sequential`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        lyrics.forEach { line ->
            for (i in 0 until line.syllables.size - 1) {
                val current = line.syllables[i]
                val next = line.syllables[i + 1]
                assertThat(current.end).isAtMost(next.start)
            }
        }
    }

    @Test
    fun `syllable start is before end`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        lyrics.flatMap { it.syllables }.forEach { syllable ->
            assertThat(syllable.start).isLessThan(syllable.end)
        }
    }

    @Test
    fun `lines are in chronological order`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        for (i in 0 until lyrics.size - 1) {
            val current = lyrics[i]
            val next = lyrics[i + 1]
            assertThat(current.start).isLessThan(next.start)
        }
    }

    // ==================== Content Tests ====================

    @Test
    fun `syllables have non-empty content`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        lyrics.flatMap { it.syllables }.forEach { syllable ->
            assertThat(syllable.content).isNotEmpty()
        }
    }

    @Test
    fun `first line starts with When`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        val firstLine = lyrics.first()
        val content = firstLine.syllables.joinToString("") { it.content }
        assertThat(content.trim()).startsWith("When")
    }

    @Test
    fun `last line content is Forever and always`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        val lastLine = lyrics.last()
        val content = lastLine.syllables.joinToString("") { it.content }
        assertThat(content.trim()).isEqualTo("Forever and always")
    }

    // ==================== Duration Tests ====================

    @Test
    fun `total duration is 20 seconds`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        val lastLine = lyrics.last()
        assertThat(lastLine.end).isEqualTo(20000)
    }

    @Test
    fun `first line starts at 0ms`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        val firstLine = lyrics.first()
        assertThat(firstLine.start).isEqualTo(0)
    }

    @Test
    fun `lines have reasonable durations`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        lyrics.forEach { line ->
            val duration = line.end - line.start
            // Each line should be between 1-3 seconds
            assertThat(duration).isAtLeast(1000)
            assertThat(duration).isAtMost(3000)
        }
    }

    // ==================== Structure Validation Tests ====================

    @Test
    fun `contains verse section lines 0-2`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        // First verse is lines 0-2 (0ms to 6000ms)
        assertThat(lyrics[0].start).isEqualTo(0)
        assertThat(lyrics[2].end).isEqualTo(6000)
    }

    @Test
    fun `contains chorus section lines 3-5`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        // Chorus is lines 3-5 (6500ms to 12500ms)
        assertThat(lyrics[3].start).isEqualTo(6500)
        assertThat(lyrics[5].end).isEqualTo(12500)
    }

    @Test
    fun `contains bridge section lines 6-7`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        // Bridge is lines 6-7 (13000ms to 17500ms)
        assertThat(lyrics[6].start).isEqualTo(13000)
        assertThat(lyrics[7].end).isEqualTo(17500)
    }

    @Test
    fun `contains outro section line 8`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        // Outro is line 8 (18000ms to 20000ms)
        assertThat(lyrics[8].start).isEqualTo(18000)
        assertThat(lyrics[8].end).isEqualTo(20000)
    }

    // ==================== Gap Tests ====================

    @Test
    fun `gaps between sections are reasonable`() {
        val lyrics = DemoLyricsProvider.createDemoLyrics()

        // Gap between verse and chorus (line 2 end to line 3 start)
        val verseToChorusGap = lyrics[3].start - lyrics[2].end
        assertThat(verseToChorusGap).isEqualTo(500)

        // Gap between chorus and bridge (line 5 end to line 6 start)
        val chorusToBridgeGap = lyrics[6].start - lyrics[5].end
        assertThat(chorusToBridgeGap).isEqualTo(500)

        // Gap between bridge and outro (line 7 end to line 8 start)
        val bridgeToOutroGap = lyrics[8].start - lyrics[7].end
        assertThat(bridgeToOutroGap).isEqualTo(500)
    }
}
