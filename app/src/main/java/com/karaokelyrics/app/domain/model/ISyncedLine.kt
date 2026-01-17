package com.karaokelyrics.app.domain.model

interface ISyncedLine {
    val content: String
    val start: Int
    val end: Int
}