package com.karaokelyrics.demo.data

import com.karaokelyrics.ui.core.models.KaraokeLine
import com.karaokelyrics.ui.core.models.KaraokeSyllable

/**
 * Provides demo lyrics for testing and showcasing the library features.
 */
object DemoLyricsProvider {

    fun createDemoLyrics(): List<KaraokeLine> {
        return listOf(
            // First verse
            KaraokeLine(
                syllables = listOf(
                    KaraokeSyllable("When ", 0, 200),
                    KaraokeSyllable("the ", 200, 400),
                    KaraokeSyllable("sun ", 400, 800),
                    KaraokeSyllable("goes ", 800, 1200),
                    KaraokeSyllable("down", 1200, 2000)
                ),
                start = 0,
                end = 2000
            ),
            KaraokeLine(
                syllables = listOf(
                    KaraokeSyllable("And ", 2000, 2200),
                    KaraokeSyllable("the ", 2200, 2400),
                    KaraokeSyllable("stars ", 2400, 2800),
                    KaraokeSyllable("come ", 2800, 3200),
                    KaraokeSyllable("out", 3200, 4000)
                ),
                start = 2000,
                end = 4000
            ),
            KaraokeLine(
                syllables = listOf(
                    KaraokeSyllable("I'll ", 4000, 4200),
                    KaraokeSyllable("be ", 4200, 4400),
                    KaraokeSyllable("dream", 4400, 4800),
                    KaraokeSyllable("ing ", 4800, 5200),
                    KaraokeSyllable("of ", 5200, 5400),
                    KaraokeSyllable("you", 5400, 6000)
                ),
                start = 4000,
                end = 6000
            ),

            // Chorus
            KaraokeLine(
                syllables = listOf(
                    KaraokeSyllable("Dance ", 6500, 6900),
                    KaraokeSyllable("with ", 6900, 7100),
                    KaraokeSyllable("me ", 7100, 7500),
                    KaraokeSyllable("to", 7500, 7700),
                    KaraokeSyllable("night", 7700, 8500)
                ),
                start = 6500,
                end = 8500
            ),
            KaraokeLine(
                syllables = listOf(
                    KaraokeSyllable("Un", 8500, 8700),
                    KaraokeSyllable("der ", 8700, 8900),
                    KaraokeSyllable("the ", 8900, 9100),
                    KaraokeSyllable("moon", 9100, 9500),
                    KaraokeSyllable("light", 9500, 10500)
                ),
                start = 8500,
                end = 10500
            ),
            KaraokeLine(
                syllables = listOf(
                    KaraokeSyllable("Hold ", 10500, 10900),
                    KaraokeSyllable("me ", 10900, 11300),
                    KaraokeSyllable("close", 11300, 11700),
                    KaraokeSyllable("ly", 11700, 12500)
                ),
                start = 10500,
                end = 12500
            ),

            // Bridge
            KaraokeLine(
                syllables = listOf(
                    KaraokeSyllable("Eve", 13000, 13200),
                    KaraokeSyllable("ry ", 13200, 13400),
                    KaraokeSyllable("mo", 13400, 13600),
                    KaraokeSyllable("ment ", 13600, 14000),
                    KaraokeSyllable("feels ", 14000, 14400),
                    KaraokeSyllable("so ", 14400, 14600),
                    KaraokeSyllable("right", 14600, 15500)
                ),
                start = 13000,
                end = 15500
            ),
            KaraokeLine(
                syllables = listOf(
                    KaraokeSyllable("With ", 15500, 15700),
                    KaraokeSyllable("you ", 15700, 16100),
                    KaraokeSyllable("by ", 16100, 16300),
                    KaraokeSyllable("my ", 16300, 16500),
                    KaraokeSyllable("side", 16500, 17500)
                ),
                start = 15500,
                end = 17500
            ),

            // Outro
            KaraokeLine(
                syllables = listOf(
                    KaraokeSyllable("For", 18000, 18200),
                    KaraokeSyllable("ev", 18200, 18400),
                    KaraokeSyllable("er ", 18400, 18800),
                    KaraokeSyllable("and ", 18800, 19000),
                    KaraokeSyllable("al", 19000, 19200),
                    KaraokeSyllable("ways", 19200, 20000)
                ),
                start = 18000,
                end = 20000
            )
        )
    }
}