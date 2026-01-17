package com.karaokelyrics.app.domain.model.synced

import com.karaokelyrics.app.domain.model.ISyncedLine

data class SyncedLine(
    override val content: String,
    override val start: Int,
    override val end: Int
) : ISyncedLine