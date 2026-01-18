package com.kyrics.demo.data.datasource

import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides demo lyrics for testing and showcasing the library features.
 */
@Singleton
class DemoLyricsDataSource @Inject constructor() {

    @Suppress("LongMethod")
    fun getDemoLyrics(): List<KyricsLine> = listOf(
        // First verse
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("When ", 0, 200),
                KyricsSyllable("the ", 200, 400),
                KyricsSyllable("sun ", 400, 800),
                KyricsSyllable("goes ", 800, 1200),
                KyricsSyllable("down", 1200, 2000)
            ),
            start = 0,
            end = 2000
        ),
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("And ", 2000, 2200),
                KyricsSyllable("the ", 2200, 2400),
                KyricsSyllable("stars ", 2400, 2800),
                KyricsSyllable("come ", 2800, 3200),
                KyricsSyllable("out", 3200, 4000)
            ),
            start = 2000,
            end = 4000
        ),
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("I'll ", 4000, 4200),
                KyricsSyllable("be ", 4200, 4400),
                KyricsSyllable("dream", 4400, 4800),
                KyricsSyllable("ing ", 4800, 5200),
                KyricsSyllable("of ", 5200, 5400),
                KyricsSyllable("you", 5400, 6000)
            ),
            start = 4000,
            end = 6000
        ),

        // Chorus
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("Dance ", 6500, 6900),
                KyricsSyllable("with ", 6900, 7100),
                KyricsSyllable("me ", 7100, 7500),
                KyricsSyllable("to", 7500, 7700),
                KyricsSyllable("night", 7700, 8500)
            ),
            start = 6500,
            end = 8500
        ),
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("Un", 8_500, 8_700),
                KyricsSyllable("der ", 8_700, 8_900),
                KyricsSyllable("the ", 8_900, 9_100),
                KyricsSyllable("moon", 9_100, 9_500),
                KyricsSyllable("light", 9_500, 10_500)
            ),
            start = 8_500,
            end = 10_500
        ),
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("Hold ", 10_500, 10_900),
                KyricsSyllable("me ", 10_900, 11_300),
                KyricsSyllable("close", 11_300, 11_700),
                KyricsSyllable("ly", 11_700, 12_500)
            ),
            start = 10_500,
            end = 12_500
        ),

        // Bridge
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("Eve", 13_000, 13_200),
                KyricsSyllable("ry ", 13_200, 13_400),
                KyricsSyllable("mo", 13_400, 13_600),
                KyricsSyllable("ment ", 13_600, 14_000),
                KyricsSyllable("feels ", 14_000, 14_400),
                KyricsSyllable("so ", 14_400, 14_600),
                KyricsSyllable("right", 14_600, 15_500)
            ),
            start = 13_000,
            end = 15_500
        ),
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("With ", 15_500, 15_700),
                KyricsSyllable("you ", 15_700, 16_100),
                KyricsSyllable("by ", 16_100, 16_300),
                KyricsSyllable("my ", 16_300, 16_500),
                KyricsSyllable("side", 16_500, 17_500)
            ),
            start = 15_500,
            end = 17_500
        ),

        // Outro
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("For", 18_000, 18_200),
                KyricsSyllable("ev", 18_200, 18_400),
                KyricsSyllable("er ", 18_400, 18_800),
                KyricsSyllable("and ", 18_800, 19_000),
                KyricsSyllable("al", 19_000, 19_200),
                KyricsSyllable("ways", 19_200, 20_000)
            ),
            start = 18_000,
            end = 20_000
        )
    )

    companion object {
        const val TOTAL_DURATION_MS = 20_000L
    }
}
