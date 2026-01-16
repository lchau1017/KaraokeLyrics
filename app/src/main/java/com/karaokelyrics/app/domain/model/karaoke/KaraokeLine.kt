package com.karaokelyrics.app.domain.model.karaoke

import com.karaokelyrics.app.domain.model.ISyncedLine

data class KaraokeLine(
    val syllables: List<KaraokeSyllable>,
    override val start: Int,
    override val end: Int,
    val metadata: Map<String, String> = emptyMap(), // Generic metadata instead of UI-specific fields
    val isAccompaniment: Boolean = false
) : ISyncedLine {
    override val content: String
        get() = syllables.joinToString("") { it.content }
}