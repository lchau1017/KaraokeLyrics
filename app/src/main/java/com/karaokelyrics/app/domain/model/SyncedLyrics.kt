package com.karaokelyrics.app.domain.model

data class SyncedLyrics(val lines: List<ISyncedLine>, val metadata: Map<String, String> = emptyMap())
