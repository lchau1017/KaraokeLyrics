package com.karaokelyrics.app.presentation.features.lyrics.handler

import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.usecase.LoadLyricsUseCase
import com.karaokelyrics.app.domain.usecase.SyncLyricsUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Handles lyrics-related operations.
 * Single Responsibility: Only manages lyrics loading and synchronization.
 */
class LyricsHandler @Inject constructor(
    private val loadLyricsUseCase: LoadLyricsUseCase,
    private val syncLyricsUseCase: SyncLyricsUseCase
) {

    /**
     * Load lyrics from a file.
     *
     * @param fileName The name of the file to load
     * @return Result containing the loaded lyrics or error
     */
    suspend fun loadLyrics(fileName: String): Result<SyncedLyrics> {
        return loadLyricsUseCase(fileName)
    }

    /**
     * Sync lyrics with playback position.
     *
     * @param lyrics The lyrics to sync
     * @param positionMs Current playback position in milliseconds
     * @return Flow of sync states
     */
    fun syncWithPlayback(
        lyrics: SyncedLyrics,
        positionMs: Long
    ) = syncLyricsUseCase(lyrics, positionMs)

    /**
     * Find the index of a line at a given position.
     *
     * @param lyrics The lyrics to search
     * @param positionMs The position to find
     * @return The index of the line at the position, or -1 if not found
     */
    fun findLineIndexAtPosition(
        lyrics: SyncedLyrics,
        positionMs: Long
    ): Int {
        return lyrics.lines.indexOfFirst { line ->
            positionMs >= line.start && positionMs <= line.end
        }
    }
}