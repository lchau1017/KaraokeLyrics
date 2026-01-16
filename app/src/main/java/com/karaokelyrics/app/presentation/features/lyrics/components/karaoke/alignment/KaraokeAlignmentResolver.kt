package com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.alignment

import androidx.compose.ui.Alignment
import com.karaokelyrics.app.domain.model.karaoke.KaraokeAlignment

/**
 * Resolves alignment for karaoke lines based on text direction.
 * Single Responsibility: Alignment resolution only.
 */
object KaraokeAlignmentResolver {

    fun resolveAlignment(
        karaokeAlignment: KaraokeAlignment,
        isRtl: Boolean
    ): Alignment {
        return when (karaokeAlignment) {
            KaraokeAlignment.Center -> Alignment.Center
            KaraokeAlignment.Start, KaraokeAlignment.Unspecified -> {
                if (isRtl) Alignment.CenterEnd else Alignment.CenterStart
            }
            KaraokeAlignment.End -> {
                if (isRtl) Alignment.CenterStart else Alignment.CenterEnd
            }
        }
    }
}