package com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.direction

import com.karaokelyrics.app.domain.model.karaoke.KaraokeSyllable
import com.karaokelyrics.app.presentation.shared.utils.TextUtils.isRtl

/**
 * Detects text direction for karaoke content.
 * Single Responsibility: Text direction detection only.
 */
object TextDirectionDetector {

    fun isRightToLeft(syllables: List<KaraokeSyllable>): Boolean {
        return syllables.any { it.content.isRtl() }
    }

    fun isRightToLeft(text: String): Boolean {
        return text.isRtl()
    }
}