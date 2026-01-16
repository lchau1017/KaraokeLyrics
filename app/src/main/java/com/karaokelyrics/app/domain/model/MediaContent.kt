package com.karaokelyrics.app.domain.model

/**
 * Domain model for media content.
 * Represents a complete media item with lyrics and audio.
 */
data class MediaContent(
    val id: String,
    val title: String,
    val lyricsFileName: String,
    val audioFileName: String,
    val artist: String = "",
    val album: String = ""
)