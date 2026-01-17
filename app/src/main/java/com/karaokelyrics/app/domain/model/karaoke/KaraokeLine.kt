package com.karaokelyrics.app.domain.model.karaoke

import com.karaokelyrics.app.domain.model.ISyncedLine

data class KaraokeLine(
    val syllables: List<KaraokeSyllable>,
    override val start: Int,
    override val end: Int,
    val alignment: KaraokeAlignment = KaraokeAlignment.Center,
    val isAccompaniment: Boolean = false,
    val isDuoView: Boolean = false
) : ISyncedLine {
    override val content: String
        get() = syllables.joinToString("") { it.content }
}