package com.karaokelyrics.app.domain.model

data class KyricsLine(
    val syllables: List<KyricsSyllable>,
    override val start: Int,
    override val end: Int,
    val metadata: Map<String, String> = emptyMap(), // Generic metadata instead of UI-specific fields
    val isAccompaniment: Boolean = false
) : ISyncedLine {
    override val content: String
        get() = syllables.joinToString("") { it.content }
}
