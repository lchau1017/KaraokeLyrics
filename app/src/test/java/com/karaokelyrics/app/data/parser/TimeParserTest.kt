package com.karaokelyrics.app.data.parser

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for [parseTime] function.
 * Tests various TTML time format conversions to milliseconds.
 */
class TimeParserTest {

    // ==================== Milliseconds Format Tests ====================

    @Test
    fun `parseTime handles milliseconds format`() {
        assertThat(parseTime("100ms")).isEqualTo(100)
        assertThat(parseTime("0ms")).isEqualTo(0)
        assertThat(parseTime("1500ms")).isEqualTo(1500)
    }

    @Test
    fun `parseTime handles milliseconds with invalid number`() {
        assertThat(parseTime("abcms")).isEqualTo(0)
        assertThat(parseTime("ms")).isEqualTo(0)
    }

    // ==================== Seconds Format Tests ====================

    @Test
    fun `parseTime handles seconds format`() {
        assertThat(parseTime("1s")).isEqualTo(1000)
        assertThat(parseTime("1.5s")).isEqualTo(1500)
        assertThat(parseTime("0.5s")).isEqualTo(500)
        assertThat(parseTime("10.25s")).isEqualTo(10_250)
    }

    @Test
    fun `parseTime handles seconds with invalid number`() {
        assertThat(parseTime("abcs")).isEqualTo(0)
        assertThat(parseTime("s")).isEqualTo(0)
    }

    // ==================== Clock Time MM:SS Format Tests ====================

    @Test
    fun `parseTime handles MM SS format without fraction`() {
        assertThat(parseTime("00:00")).isEqualTo(0)
        assertThat(parseTime("00:01")).isEqualTo(1000)
        assertThat(parseTime("01:00")).isEqualTo(60_000)
        assertThat(parseTime("01:30")).isEqualTo(90_000)
    }

    @Test
    fun `parseTime handles MM SS format with fraction`() {
        assertThat(parseTime("00:00.5")).isEqualTo(500)
        assertThat(parseTime("00:00.50")).isEqualTo(500)
        assertThat(parseTime("00:00.500")).isEqualTo(500)
        assertThat(parseTime("01:30.500")).isEqualTo(90_500)
        assertThat(parseTime("01:30.123")).isEqualTo(90_123)
    }

    @Test
    fun `parseTime handles single digit fraction`() {
        assertThat(parseTime("00:01.6")).isEqualTo(1600)
    }

    @Test
    fun `parseTime handles two digit fraction`() {
        assertThat(parseTime("00:01.60")).isEqualTo(1600)
    }

    @Test
    fun `parseTime handles three digit fraction`() {
        assertThat(parseTime("00:01.600")).isEqualTo(1600)
    }

    @Test
    fun `parseTime handles more than three digit fraction`() {
        assertThat(parseTime("00:01.6789")).isEqualTo(1678)
    }

    // ==================== Clock Time HH:MM:SS Format Tests ====================

    @Test
    fun `parseTime handles HH MM SS format without fraction`() {
        assertThat(parseTime("00:00:00")).isEqualTo(0)
        assertThat(parseTime("00:00:01")).isEqualTo(1000)
        assertThat(parseTime("00:01:00")).isEqualTo(60_000)
        assertThat(parseTime("01:00:00")).isEqualTo(3_600_000)
        assertThat(parseTime("01:02:30")).isEqualTo(3_750_000)
    }

    @Test
    fun `parseTime handles HH MM SS format with fraction`() {
        assertThat(parseTime("00:00:00.500")).isEqualTo(500)
        assertThat(parseTime("01:02:30.100")).isEqualTo(3_750_100)
        assertThat(parseTime("01:02:30.999")).isEqualTo(3_750_999)
    }

    // ==================== Plain Integer Format Tests ====================

    @Test
    fun `parseTime handles plain integer`() {
        assertThat(parseTime("1000")).isEqualTo(1000)
        assertThat(parseTime("0")).isEqualTo(0)
        assertThat(parseTime("500")).isEqualTo(500)
    }

    @Test
    fun `parseTime handles plain integer with invalid number`() {
        assertThat(parseTime("abc")).isEqualTo(0)
        assertThat(parseTime("")).isEqualTo(0)
    }

    // ==================== Edge Cases Tests ====================

    @Test
    fun `parseTime handles empty string`() {
        assertThat(parseTime("")).isEqualTo(0)
    }

    @Test
    fun `parseTime handles whitespace`() {
        assertThat(parseTime(" ")).isEqualTo(0)
    }

    @Test
    fun `parseTime handles invalid colon format`() {
        assertThat(parseTime(":")).isEqualTo(0)
        assertThat(parseTime("::")).isEqualTo(0)
        assertThat(parseTime(":::")).isEqualTo(0)
        assertThat(parseTime("1:2:3:4")).isEqualTo(0)
    }
}
