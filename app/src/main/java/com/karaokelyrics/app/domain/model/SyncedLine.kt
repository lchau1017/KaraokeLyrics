package com.karaokelyrics.app.domain.model

data class SyncedLine(
    override val content: String,
    override val start: Int,
    override val end: Int
) : ISyncedLine