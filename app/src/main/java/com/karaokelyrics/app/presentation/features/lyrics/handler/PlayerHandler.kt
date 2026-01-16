package com.karaokelyrics.app.presentation.features.lyrics.handler

import com.karaokelyrics.app.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Handles player control operations.
 * Single Responsibility: Only manages media playback control.
 */
class PlayerHandler @Inject constructor(
    private val playerRepository: PlayerRepository
) {

    /**
     * Get the current playing state.
     */
    val isPlaying: Flow<Boolean> = playerRepository.observeIsPlaying()

    /**
     * Get the current playback position.
     */
    val playbackPosition: Flow<Long> = playerRepository.observePlaybackPosition()

    /**
     * Load media for playback.
     *
     * @param fileName The name of the media file to load
     */
    suspend fun loadMedia(fileName: String) {
        playerRepository.loadMedia(fileName)
    }

    /**
     * Start playback.
     */
    suspend fun play() {
        playerRepository.play()
    }

    /**
     * Pause playback.
     */
    suspend fun pause() {
        playerRepository.pause()
    }

    /**
     * Toggle between play and pause.
     */
    suspend fun togglePlayPause() {
        if (isPlaying.first()) {
            pause()
        } else {
            play()
        }
    }

    /**
     * Seek to a specific position.
     *
     * @param positionMs The position to seek to in milliseconds
     */
    suspend fun seekTo(positionMs: Long) {
        playerRepository.seekTo(positionMs)
    }

    /**
     * Seek to a specific line.
     *
     * @param lineStartMs The start time of the line in milliseconds
     */
    suspend fun seekToLine(lineStartMs: Int) {
        playerRepository.seekTo(lineStartMs.toLong())
    }

    /**
     * Release player resources.
     */
    fun release() {
        // Repository should handle cleanup
    }
}