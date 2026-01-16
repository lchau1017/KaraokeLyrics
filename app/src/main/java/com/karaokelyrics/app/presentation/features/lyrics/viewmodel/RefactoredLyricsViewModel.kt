package com.karaokelyrics.app.presentation.features.lyrics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaokelyrics.app.domain.model.ISyncedLine
import com.karaokelyrics.app.domain.model.LyricsSyncState
import com.karaokelyrics.app.domain.usecase.CoordinatePlaybackSyncUseCase
import com.karaokelyrics.app.presentation.features.lyrics.effect.LyricsEffect
import com.karaokelyrics.app.presentation.features.lyrics.handler.LyricsHandler
import com.karaokelyrics.app.presentation.features.lyrics.handler.PlayerHandler
import com.karaokelyrics.app.presentation.features.lyrics.handler.SettingsHandler
import com.karaokelyrics.app.presentation.features.lyrics.state.LyricsIntent
import com.karaokelyrics.app.presentation.features.lyrics.state.LyricsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Refactored ViewModel following Single Responsibility Principle.
 * This ViewModel only orchestrates handlers and manages UI state.
 * All business logic is delegated to specialized handlers.
 */
@HiltViewModel
class RefactoredLyricsViewModel @Inject constructor(
    private val lyricsHandler: LyricsHandler,
    private val playerHandler: PlayerHandler,
    private val settingsHandler: SettingsHandler,
    private val coordinatePlaybackSyncUseCase: CoordinatePlaybackSyncUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LyricsUiState())
    val state: StateFlow<LyricsUiState> = _state.asStateFlow()

    private val _effects = Channel<LyricsEffect>(Channel.BUFFERED)
    val effects: Flow<LyricsEffect> = _effects.receiveAsFlow()

    init {
        observePlaybackState()
        observeSettings()
    }

    /**
     * Process user intents.
     * This method only routes intents to appropriate handlers.
     */
    fun processIntent(intent: LyricsIntent) {
        viewModelScope.launch {
            when (intent) {
                // Lyrics intents
                is LyricsIntent.LoadInitialLyrics -> handleLoadInitialLyrics()

                // Player intents
                is LyricsIntent.PlayPause -> handlePlayPause()
                is LyricsIntent.SeekToLine -> handleSeekToLine(intent.lineIndex)
                is LyricsIntent.SeekToPosition -> handleSeekToPosition(intent.position)

                // Settings intents
                is LyricsIntent.UpdateLyricsColor ->
                    settingsHandler.updateLyricsColor(intent.color)
                is LyricsIntent.UpdateBackgroundColor ->
                    settingsHandler.updateBackgroundColor(intent.color)
                is LyricsIntent.UpdateFontSize ->
                    settingsHandler.updateFontSize(intent.fontSize)
                is LyricsIntent.UpdateAnimationsEnabled ->
                    settingsHandler.updateAnimationsEnabled(intent.enabled)
                is LyricsIntent.UpdateBlurEffectEnabled ->
                    settingsHandler.updateBlurEffectEnabled(intent.enabled)
                is LyricsIntent.UpdateCharacterAnimationsEnabled ->
                    settingsHandler.updateCharacterAnimationsEnabled(intent.enabled)
                is LyricsIntent.UpdateDarkMode ->
                    settingsHandler.updateDarkMode(intent.isDark)
                is LyricsIntent.ResetSettingsToDefaults ->
                    settingsHandler.resetToDefaults()
            }
        }
    }

    private suspend fun handleLoadInitialLyrics() {
        _state.update { it.copy(isLoading = true) }

        lyricsHandler.loadLyrics("golden-hour.ttml")
            .onSuccess { lyrics ->
                _state.update {
                    it.copy(
                        lyrics = lyrics,
                        isLoading = false,
                        error = null
                    )
                }
                // Load corresponding audio
                playerHandler.loadMedia("golden-hour.m4a")
            }
            .onFailure { error ->
                Timber.e(error, "Failed to load lyrics")
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load lyrics"
                    )
                }
                _effects.send(LyricsEffect.ShowError(
                    error.message ?: "Failed to load lyrics"
                ))
            }
    }

    private suspend fun handlePlayPause() {
        playerHandler.togglePlayPause()
    }

    private suspend fun handleSeekToLine(lineIndex: Int) {
        state.value.lyrics?.let { lyrics ->
            if (lineIndex in lyrics.lines.indices) {
                val line = lyrics.lines[lineIndex] as? ISyncedLine
                line?.let {
                    playerHandler.seekToLine(it.start)
                    _effects.send(LyricsEffect.ScrollToLine(lineIndex))
                }
            }
        }
    }

    private suspend fun handleSeekToPosition(position: Long) {
        playerHandler.seekTo(position)
    }

    private fun observePlaybackState() {
        viewModelScope.launch {
            // Use the coordinator to handle both playback and sync state
            coordinatePlaybackSyncUseCase(
                state.value.lyrics,
                state.value.userSettings
            ).collect { playbackSyncState ->
                _state.update {
                    it.copy(
                        playbackPosition = playbackSyncState.playbackPosition,
                        isPlaying = playbackSyncState.isPlaying,
                        syncState = playbackSyncState.syncState ?: LyricsSyncState()
                    )
                }
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsHandler.observeSettingsWithTheme().collect { settingsWithTheme ->
                _state.update {
                    it.copy(
                        userSettings = settingsWithTheme.settings,
                        themeColors = settingsWithTheme.themeColors
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerHandler.release()
    }
}