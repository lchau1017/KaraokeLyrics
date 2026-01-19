package com.karaokelyrics.app.domain.model

import com.kyrics.models.SyncedLine

data class SyncedLyrics(val lines: List<SyncedLine>, val metadata: Map<String, String> = emptyMap())
