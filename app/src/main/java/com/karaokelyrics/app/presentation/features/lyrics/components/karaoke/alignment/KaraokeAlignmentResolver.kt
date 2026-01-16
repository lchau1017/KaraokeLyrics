package com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.alignment

import androidx.compose.ui.Alignment
import com.karaokelyrics.app.presentation.features.lyrics.model.alignment.KaraokeAlignment

/**
 * Resolves alignment for karaoke lines based on text direction.
 * Single Responsibility: Alignment resolution only.
 */
object KaraokeAlignmentResolver {

    fun resolveAlignment(
        alignmentStr: String,
        isRtl: Boolean
    ): Alignment {
        return when (alignmentStr) {
            "Center" -> Alignment.Center
            "Start", "Unspecified" -> {
                if (isRtl) Alignment.CenterEnd else Alignment.CenterStart
            }
            "End" -> {
                if (isRtl) Alignment.CenterStart else Alignment.CenterEnd
            }
            else -> Alignment.Center
        }
    }
}