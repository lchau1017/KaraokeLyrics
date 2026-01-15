package com.karaokelyrics.app.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.usecase.lyrics.LoadLyricsFromAssetUseCase
import com.karaokelyrics.app.domain.usecase.lyrics.SyncLyricsWithPlaybackUseCase
import com.karaokelyrics.app.domain.usecase.player.LoadMediaUseCase
import com.karaokelyrics.app.domain.usecase.player.ObservePlayerStateUseCase
import com.karaokelyrics.app.domain.usecase.player.PlayPauseUseCase
import com.karaokelyrics.app.domain.usecase.player.SeekToPositionUseCase
import com.karaokelyrics.app.domain.usecase.settings.ObserveUserSettingsUseCase
import com.karaokelyrics.app.domain.usecase.settings.ResetSettingsUseCase
import com.karaokelyrics.app.domain.usecase.settings.UpdateUserSettingsUseCase
import com.karaokelyrics.app.presentation.effect.LyricsEffect
import com.karaokelyrics.app.presentation.intent.LyricsIntent
import com.karaokelyrics.app.presentation.state.LyricsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for lyrics presentation
 * Orchestrates use cases to manage UI state
 */
@HiltViewModel
class LyricsViewModel @Inject constructor(
    private val loadLyricsFromAssetUseCase: LoadLyricsFromAssetUseCase,
    private val syncLyricsWithPlaybackUseCase: SyncLyricsWithPlaybackUseCase,
    private val loadMediaUseCase: LoadMediaUseCase,
    private val playPauseUseCase: PlayPauseUseCase,
    private val seekToPositionUseCase: SeekToPositionUseCase,
    private val observePlayerStateUseCase: ObservePlayerStateUseCase,
    private val observeUserSettingsUseCase: ObserveUserSettingsUseCase,
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase,
    private val resetSettingsUseCase: ResetSettingsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LyricsUiState())
    val state: StateFlow<LyricsUiState> = _state.asStateFlow()

    private val _effects = Channel<LyricsEffect>(Channel.BUFFERED)
    val effects: Flow<LyricsEffect> = _effects.receiveAsFlow()

    init {
        observePlaybackState()
        observeUserSettings()
        observeLyricsSyncState()
    }

    fun processIntent(intent: LyricsIntent) {
        when (intent) {
            is LyricsIntent.LoadInitialLyrics -> loadInitialLyrics()
            is LyricsIntent.PlayPause -> togglePlayPause()
            is LyricsIntent.SeekToLine -> seekToLine(intent.lineIndex)
            is LyricsIntent.SeekToPosition -> seekToPosition(intent.position)

            // Settings intents
            is LyricsIntent.UpdateLyricsColor -> updateLyricsColor(intent.color)
            is LyricsIntent.UpdateBackgroundColor -> updateBackgroundColor(intent.color)
            is LyricsIntent.UpdateFontSize -> updateFontSize(intent.fontSize)
            is LyricsIntent.UpdateAnimationsEnabled -> updateAnimationsEnabled(intent.enabled)
            is LyricsIntent.UpdateBlurEffectEnabled -> updateBlurEffectEnabled(intent.enabled)
            is LyricsIntent.UpdateCharacterAnimationsEnabled -> updateCharacterAnimationsEnabled(intent.enabled)
            is LyricsIntent.UpdateDarkMode -> updateDarkMode(intent.isDark)
            is LyricsIntent.ResetSettingsToDefaults -> resetSettingsToDefaults()
        }
    }

    private fun loadInitialLyrics() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Load the golden hour TTML file
            loadLyricsFromAssetUseCase("golden-hour.ttml")
                .onSuccess { lyrics ->
                    _state.update {
                        it.copy(
                            lyrics = lyrics,
                            isLoading = false,
                            error = null
                        )
                    }
                    // Load the corresponding audio
                    loadMediaUseCase("golden-hour.m4a")
                }
                .onFailure { error ->
                    Timber.e(error, "Failed to load lyrics")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                    _effects.send(
                        LyricsEffect.ShowError(
                            error.message ?: "Failed to load lyrics"
                        )
                    )
                }
        }
    }

    private fun observePlaybackState() {
        // Observe player state
        viewModelScope.launch {
            observePlayerStateUseCase().collect { playerState ->
                _state.update { currentState ->
                    currentState.copy(
                        playbackPosition = playerState.playbackPosition,
                        isPlaying = playerState.isPlaying,
                        duration = playerState.duration
                    )
                }
            }
        }
    }

    private fun observeLyricsSyncState() {
        // Observe lyrics sync state
        viewModelScope.launch {
            syncLyricsWithPlaybackUseCase().collect { syncState ->
                syncState?.let {
                    _state.update { currentState ->
                        currentState.copy(syncState = it)
                    }
                }
            }
        }
    }

    private fun togglePlayPause() {
        viewModelScope.launch {
            playPauseUseCase(_state.value.isPlaying)
        }
    }

    private fun seekToLine(lineIndex: Int) {
        viewModelScope.launch {
            val line = _state.value.lyrics?.lines?.getOrNull(lineIndex)
            line?.let {
                seekToPositionUseCase(it.start.toLong())
                _effects.send(LyricsEffect.ScrollToLine(lineIndex))
            }
        }
    }

    private fun seekToPosition(position: Long) {
        viewModelScope.launch {
            seekToPositionUseCase(position)
        }
    }

    private fun observeUserSettings() {
        viewModelScope.launch {
            observeUserSettingsUseCase().collect { settings ->
                _state.update { it.copy(userSettings = settings) }
            }
        }
    }

    // Settings update methods
    private fun updateLyricsColor(color: Color) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateLyricsColor(color)
        }
    }

    private fun updateBackgroundColor(color: Color) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateBackgroundColor(color)
        }
    }

    private fun updateFontSize(fontSize: FontSize) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateFontSize(fontSize)
        }
    }

    private fun updateAnimationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateAnimationsEnabled(enabled)
        }
    }

    private fun updateBlurEffectEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateBlurEffectEnabled(enabled)
        }
    }

    private fun updateCharacterAnimationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateCharacterAnimationsEnabled(enabled)
        }
    }

    private fun updateDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateDarkMode(isDark)
        }
    }

    private fun resetSettingsToDefaults() {
        viewModelScope.launch {
            resetSettingsUseCase()
        }
    }
}