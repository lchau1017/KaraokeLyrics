package com.karaokelyrics.ui.core.models

/**
 * Implementation of ISyncedLine for karaoke content with syllable-level timing.
 */
data class KaraokeLine(
    val syllables: List<KaraokeSyllable>,
    override val start: Int,
    override val end: Int,
    val metadata: Map<String, String> = emptyMap()
) : ISyncedLine {

    override fun getContent(): String = syllables.joinToString("") { it.content }

    /**
     * Check if this is an accompaniment/background vocal line
     */
    val isAccompaniment: Boolean
        get() = metadata["type"] == "accompaniment"

    /**
     * Get alignment hint for this line
     */
    val alignment: String
        get() = metadata["alignment"] ?: "center"
}
