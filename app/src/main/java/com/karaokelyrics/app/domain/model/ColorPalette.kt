package com.karaokelyrics.app.domain.model

/**
 * Centralized color palette for the app.
 * All colors are stored as ARGB integers for domain layer.
 */
object ColorPalette {
    // Core Brand Colors
    object Brand {
        val spotifyGreen = 0xFF1DB954.toInt()
        val spotifyBlack = 0xFF121212.toInt()
        val spotifyDarkGray = 0xFF181818.toInt()
        val spotifyMediumGray = 0xFF282828.toInt()
        val spotifyLightGray = 0xFF535353.toInt()
        val spotifyTextGray = 0xFFB3B3B3.toInt()
        val white = 0xFFFFFFFF.toInt()
        val black = 0xFF000000.toInt()
    }

    // Vibrant Colors for Lyrics
    object Vibrant {
        val purple = 0xFF9B59B6.toInt()
        val blue = 0xFF3498DB.toInt()
        val red = 0xFFE74C3C.toInt()
        val orange = 0xFFF39C12.toInt()
        val pink = 0xFFE91E63.toInt()
        val cyan = 0xFF00BCD4.toInt()
        val yellow = 0xFFFFEB3B.toInt()
    }

    // Dark Theme Colors
    object Dark {
        val defaultLyrics = Brand.spotifyGreen
        val defaultBackground = Brand.spotifyBlack

        val lyricOptions = listOf(
            Brand.spotifyGreen,
            Brand.white,
            Vibrant.purple,
            Vibrant.blue,
            Vibrant.red,
            Vibrant.orange,
            Vibrant.pink,
            Vibrant.cyan,
            Vibrant.yellow
        )

        val backgroundOptions = listOf(
            Brand.spotifyBlack,
            0xFF2C2C2C.toInt(), // dark gray
            Brand.black,
            0xFF1A1A1A.toInt(),
            0xFF2A2A2A.toInt(),
            0xFF0D47A1.toInt(), // dark blue
            0xFF1B5E20.toInt(), // dark green
            0xFF4A148C.toInt() // dark purple
        )
    }

    // Light Theme Colors
    object Light {
        val defaultLyrics = Brand.spotifyGreen
        val defaultBackground = Brand.white

        val lyricOptions = listOf(
            Brand.spotifyGreen,
            0xFF1A1A1A.toInt(), // dark gray for contrast
            0xFF7B1FA2.toInt(), // darker purple
            0xFF1976D2.toInt(), // darker blue
            0xFFD32F2F.toInt(), // darker red
            0xFFFF8F00.toInt(), // darker orange
            0xFFC2185B.toInt(), // darker pink
            0xFF0097A7.toInt(), // darker cyan
            0xFFFBC02D.toInt() // darker yellow
        )

        val backgroundOptions = listOf(
            Brand.white,
            0xFFF5F5F5.toInt(), // light gray
            0xFFE8F5E8.toInt(), // light green
            0xFFE3F2FD.toInt(), // light blue
            0xFFFFF3E0.toInt(), // light orange
            0xFFF3E5F5.toInt(), // light purple
            0xFFE0F2F1.toInt(), // light cyan
            0xFFFFFDE7.toInt() // light yellow
        )
    }
}
