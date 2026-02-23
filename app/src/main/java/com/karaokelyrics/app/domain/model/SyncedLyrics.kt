package com.karaokelyrics.app.domain.model

data class SyncedLyrics(val lines: List<LyricsLine>, val metadata: Map<String, String> = emptyMap())
