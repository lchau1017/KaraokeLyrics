package com.karaokelyrics.app.presentation.mapper

import com.karaokelyrics.app.domain.model.LyricsLine
import com.karaokelyrics.app.domain.model.LyricsSyllable
import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable

/**
 * Maps domain [LyricsLine] to library [KyricsLine] for use in KyricsViewer.
 * This mapper exists at the presentation layer boundary only.
 */
fun LyricsLine.toKyricsLine(): KyricsLine = KyricsLine(
    start = start,
    end = end,
    syllables = syllables.map { it.toKyricsSyllable() }
)

fun LyricsSyllable.toKyricsSyllable(): KyricsSyllable = KyricsSyllable(
    start = start,
    end = end,
    content = content
)
