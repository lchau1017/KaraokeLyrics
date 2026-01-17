package com.karaokelyrics.demo.data

import com.karaokelyrics.ui.core.models.KaraokeLine
import com.karaokelyrics.ui.core.models.KaraokeSyllable

/**
 * Provides demo lyrics for testing and showcasing the library features.
 */
object DemoLyricsProvider {

    fun createDemoLyrics(): List<KaraokeLine> {
        return listOf(
            KaraokeLine(
                syllables = listOf(
                    KaraokeSyllable("Test ", 0, 500),
                    KaraokeSyllable("all ", 500, 1000),
                    KaraokeSyllable("effects", 1000, 2000)
                ),
                start = 0,
                end = 2000
            ),
            KaraokeLine(
                syllables = listOf(
                    KaraokeSyllable("Cus", 2000, 2300),
                    KaraokeSyllable("tom", 2300, 2600),
                    KaraokeSyllable("ize ", 2600, 2900),
                    KaraokeSyllable("every", 2900, 3400),
                    KaraokeSyllable("thing", 3400, 4000)
                ),
                start = 2000,
                end = 4000
            ),
            KaraokeLine(
                syllables = listOf(
                    KaraokeSyllable("Colors, ", 4000, 4500),
                    KaraokeSyllable("fonts, ", 4500, 5000),
                    KaraokeSyllable("and ", 5000, 5300),
                    KaraokeSyllable("animations", 5300, 6500)
                ),
                start = 4000,
                end = 6500
            ),
            KaraokeLine(
                syllables = listOf(
                    KaraokeSyllable("Long ", 9000, 9300),
                    KaraokeSyllable("lines ", 9300, 9600),
                    KaraokeSyllable("will ", 9600, 9900),
                    KaraokeSyllable("auto", 9900, 10200),
                    KaraokeSyllable("mat", 10200, 10500),
                    KaraokeSyllable("i", 10500, 10700),
                    KaraokeSyllable("cal", 10700, 11000),
                    KaraokeSyllable("ly ", 11000, 11300),
                    KaraokeSyllable("wrap ", 11300, 11600),
                    KaraokeSyllable("to ", 11600, 11900),
                    KaraokeSyllable("the ", 11900, 12200),
                    KaraokeSyllable("next ", 12200, 12500),
                    KaraokeSyllable("line", 12500, 13000)
                ),
                start = 9000,
                end = 13000
            )
        )
    }
}