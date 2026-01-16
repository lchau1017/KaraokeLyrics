package com.karaokelyrics.app.domain.model.layout

import androidx.compose.ui.geometry.Offset
import com.karaokelyrics.app.domain.model.karaoke.KaraokeSyllable

/**
 * Pure layout information for a syllable.
 * This model focuses only on positioning and sizing, following SRP.
 * No animation or rendering concerns.
 */
data class SyllableLayoutInfo(
    val syllable: KaraokeSyllable,
    val width: Float,
    val height: Float,
    val position: Offset,
    val baseline: Float,
    val wordId: Int
)

/**
 * Layout information for an entire line of lyrics.
 */
data class LineLayoutInfo(
    val syllables: List<SyllableLayoutInfo>,
    val lineHeight: Float,
    val lineWidth: Float,
    val baseline: Float
)

/**
 * Word-level layout information.
 */
data class WordLayoutInfo(
    val wordId: Int,
    val syllables: List<SyllableLayoutInfo>,
    val totalWidth: Float,
    val center: Offset
)