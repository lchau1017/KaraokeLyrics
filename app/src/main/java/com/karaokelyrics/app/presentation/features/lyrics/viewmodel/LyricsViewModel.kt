package com.karaokelyrics.app.presentation.features.lyrics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaokelyrics.app.domain.repository.PlayerRepository
import com.karaokelyrics.app.domain.usecase.LoadLyricsUseCase
import com.karaokelyrics.app.domain.usecase.SyncLyricsUseCase
import com.karaokelyrics.app.domain.usecase.CoordinatePlaybackSyncUseCase
import com.karaokelyrics.app.domain.usecase.ObserveUserSettingsUseCase
import com.karaokelyrics.app.domain.usecase.UpdateUserSettingsUseCase
import com.karaokelyrics.app.domain.model.LyricsSyncState
import com.karaokelyrics.app.presentation.features.lyrics.effect.LyricsEffect
import com.karaokelyrics.app.presentation.features.lyrics.state.LyricsIntent
import com.karaokelyrics.app.presentation.features.lyrics.state.LyricsUiState
import com.karaokelyrics.app.presentation.features.settings.mapper.SettingsUiMapper.toColorArgb
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LyricsViewModel @Inject constructor(
    private val loadLyricsUseCase: LoadLyricsUseCase,
    private val syncLyricsUseCase: SyncLyricsUseCase,
    private val coordinatePlaybackSyncUseCase: CoordinatePlaybackSyncUseCase,
    private val playerRepository: PlayerRepository,
    private val observeUserSettingsUseCase: ObserveUserSettingsUseCase,
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LyricsUiState())
    val state: StateFlow<LyricsUiState> = _state.asStateFlow()

    private val _effects = Channel<LyricsEffect>(Channel.BUFFERED)
    val effects: Flow<LyricsEffect> = _effects.receiveAsFlow()

    init {
        observePlaybackState()
        observeUserSettings()
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
            loadLyricsUseCase("golden-hour.ttml")
                .onSuccess { lyrics ->
                    _state.update {
                        it.copy(
                            lyrics = lyrics,
                            isLoading = false,
                            error = null
                        )
                    }
                    // Load the corresponding audio
                    playerRepository.loadMedia("golden-hour.m4a")
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
        // Use domain use case for coordinated playback and sync logic (simplified)
        viewModelScope.launch {
            coordinatePlaybackSyncUseCase.observePlaybackOnly().collect { coordinated ->
                val lyrics = _state.value.lyrics
                val userSettings = _state.value.userSettings

                // If we have lyrics, get the sync state using domain logic
                val syncState = if (lyrics != null) {
                    syncLyricsUseCase(lyrics, coordinated.playbackPosition, userSettings.lyricsTimingOffsetMs)
                } else {
                    null
                }

                _state.update { currentState ->
                    currentState.copy(
                        syncState = syncState ?: LyricsSyncState(),
                        playbackPosition = coordinated.playbackPosition,
                        isPlaying = coordinated.isPlaying
                    )
                }
            }
        }
    }

    private fun togglePlayPause() {
        viewModelScope.launch {
            if (_state.value.isPlaying) {
                playerRepository.pause()
            } else {
                playerRepository.play()
            }
        }
    }

    private fun seekToLine(lineIndex: Int) {
        viewModelScope.launch {
            val line = _state.value.lyrics?.lines?.getOrNull(lineIndex)
            line?.let {
                playerRepository.seekTo(it.start.toLong())
                _effects.send(LyricsEffect.ScrollToLine(lineIndex))
            }
        }
    }

    private fun seekToPosition(position: Long) {
        viewModelScope.launch {
            playerRepository.seekTo(position)
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
    private fun updateLyricsColor(color: androidx.compose.ui.graphics.Color) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateLyricsColor(color.toColorArgb())
        }
    }

    private fun updateBackgroundColor(color: androidx.compose.ui.graphics.Color) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateBackgroundColor(color.toColorArgb())
        }
    }

    private fun updateFontSize(fontSize: com.karaokelyrics.app.domain.model.FontSize) {
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
            updateUserSettingsUseCase.resetToDefaults()
        }
    }
}