package com.karaokelyrics.app.presentation.features.lyrics.coordinator

import com.karaokelyrics.app.domain.model.LyricsSyncState
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.presentation.player.PlayerController
import com.karaokelyrics.app.domain.usecase.SyncLyricsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Domain use case that coordinates playback state with lyrics synchronization.
 * Encapsulates the business logic that was previously in LyricsViewModel.
 */
class PlaybackSyncCoordinator @Inject constructor(
    private val playerController: PlayerController,
    private val syncLyricsUseCase: SyncLyricsUseCase
) {

    /**
     * Represents the coordinated playback and sync state.
     */
    data class PlaybackSyncState(
        val playbackPosition: Long,
        val isPlaying: Boolean,
        val syncState: LyricsSyncState?
    )

    /**
     * Coordinates playback state with lyrics synchronization.
     * Returns a flow of coordinated state that combines playback and sync information.
     */
    operator fun invoke(
        lyrics: SyncedLyrics?,
        userSettings: UserSettings
    ): Flow<PlaybackSyncState> {
        return combine(
            playerController.observePlaybackPosition(),
            playerController.observeIsPlaying()
        ) { position, isPlaying ->
            val syncState = if (lyrics != null) {
                // Use the timing offset from user settings
                val timingOffset = userSettings.lyricsTimingOffsetMs
                syncLyricsUseCase(lyrics, position, timingOffset)
            } else {
                null
            }

            PlaybackSyncState(
                playbackPosition = position,
                isPlaying = isPlaying,
                syncState = syncState
            )
        }
    }

    /**
     * Simplified version that doesn't require lyrics or user settings.
     * Useful for when we just need playback state without sync.
     */
    fun observePlaybackOnly(): Flow<PlaybackSyncState> {
        return combine(
            playerController.observePlaybackPosition(),
            playerController.observeIsPlaying()
        ) { position, isPlaying ->
            PlaybackSyncState(
                playbackPosition = position,
                isPlaying = isPlaying,
                syncState = null
            )
        }
    }
}