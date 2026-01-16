package com.karaokelyrics.app.data.source.local

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides available media content information.
 * This is the single source of truth for available lyrics and audio files.
 */
@Singleton
class MediaContentProvider @Inject constructor() {

    /**
     * Data class representing a media content item.
     */
    data class MediaContent(
        val id: String,
        val title: String,
        val lyricsFileName: String,
        val audioFileName: String,
        val artist: String = "",
        val album: String = ""
    )

    /**
     * Get all available media content.
     * In a production app, this might come from a database or API.
     */
    fun getAvailableContent(): List<MediaContent> = listOf(
        MediaContent(
            id = "golden-hour",
            title = "Golden Hour",
            lyricsFileName = "golden-hour.ttml",
            audioFileName = "golden-hour.m4a",
            artist = "JVKE",
            album = "this is what ____ feels like (Vol. 1-4)"
        ),
        // Add more content here as needed
    )

    /**
     * Get default content to load on app start.
     */
    fun getDefaultContent(): MediaContent = getAvailableContent().first()

    /**
     * Find content by ID.
     */
    fun findContentById(id: String): MediaContent? =
        getAvailableContent().find { it.id == id }

    /**
     * Get content by index.
     */
    fun getContentByIndex(index: Int): MediaContent? =
        getAvailableContent().getOrNull(index)
}