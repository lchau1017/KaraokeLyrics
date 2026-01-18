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
                KyricsSyllable("Un", 8500, 8700),
                KyricsSyllable("der ", 8700, 8900),
                KyricsSyllable("the ", 8900, 9100),
                KyricsSyllable("moon", 9100, 9500),
                KyricsSyllable("light", 9500, 10500)
            ),
            start = 8500,
            end = 10500
        ),
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("Hold ", 10500, 10900),
                KyricsSyllable("me ", 10900, 11300),
                KyricsSyllable("close", 11300, 11700),
                KyricsSyllable("ly", 11700, 12500)
            ),
            start = 10500,
            end = 12500
        ),

        // Bridge
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("Eve", 13000, 13200),
                KyricsSyllable("ry ", 13200, 13400),
                KyricsSyllable("mo", 13400, 13600),
                KyricsSyllable("ment ", 13600, 14000),
                KyricsSyllable("feels ", 14000, 14400),
                KyricsSyllable("so ", 14400, 14600),
                KyricsSyllable("right", 14600, 15500)
            ),
            start = 13000,
            end = 15500
        ),
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("With ", 15500, 15700),
                KyricsSyllable("you ", 15700, 16100),
                KyricsSyllable("by ", 16100, 16300),
                KyricsSyllable("my ", 16300, 16500),
                KyricsSyllable("side", 16500, 17500)
            ),
            start = 15500,
            end = 17500
        ),

        // Outro
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("For", 18000, 18200),
                KyricsSyllable("ev", 18200, 18400),
                KyricsSyllable("er ", 18400, 18800),
                KyricsSyllable("and ", 18800, 19000),
                KyricsSyllable("al", 19000, 19200),
                KyricsSyllable("ways", 19200, 20000)
            ),
            start = 18000,
            end = 20000
        )
    )

    companion object {
        const val TOTAL_DURATION_MS = 20000L
    }
}
