package com.karaokelyrics.app.domain.model

import com.kyrics.models.KyricsLine

data class SyncedLyrics(val lines: List<KyricsLine>, val metadata: Map<String, String> = emptyMap())
