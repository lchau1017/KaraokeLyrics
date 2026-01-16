package com.karaokelyrics.app.presentation.features.lyrics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaokelyrics.app.domain.model.LyricsSyncState
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.domain.repository.PlayerRepository
import com.karaokelyrics.app.domain.usecase.LoadLyricsUseCase
import com.karaokelyrics.app.domain.usecase.ObserveUserSettingsUseCase
import com.karaokelyrics.app.domain.usecase.SyncLyricsUseCase
import com.karaokelyrics.app.presentation.features.lyrics.effect.LyricsEffect
import com.karaokelyrics.app.presentation.features.lyrics.intent.LyricsIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * MVI ViewModel for Lyrics display following Clean Architecture.
 * Single Responsibility: Manages lyrics display state and handles intents.
 */
@HiltViewModel
class LyricsViewModel @Inject constructor(
    private val loadLyricsUseCase: LoadLyricsUseCase,
    private val syncLyricsUseCase: SyncLyricsUseCase,
    private val playerRepository: PlayerRepository,
    private val observeUserSettingsUseCase: ObserveUserSettingsUseCase
) : ViewModel() {

    data class LyricsState(
        val lyrics: SyncedLyrics? = null,
        val syncState: LyricsSyncState = LyricsSyncState(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val userSettings: UserSettings = UserSettings()
    )

    private val _state = MutableStateFlow(LyricsState())
    val state: StateFlow<LyricsState> = _state.asStateFlow()

    private val _effects = Channel<LyricsEffect>(Channel.BUFFERED)
    val effects: Flow<LyricsEffect> = _effects.receiveAsFlow()

    private val _intents = Channel<LyricsIntent>(Channel.BUFFERED)

    init {
        observeLyricsSync()
        observeUserSettings()
        processIntents()
    }

    fun handleIntent(intent: LyricsIntent) {
        viewModelScope.launch {
            _intents.send(intent)
        }
    }

    private fun processIntents() {
        viewModelScope.launch {
            _intents.receiveAsFlow().collect { intent ->
                when (intent) {
                    is LyricsIntent.LoadLyrics -> loadLyrics(intent.fileName, intent.audioFileName)
                    is LyricsIntent.SeekToLine -> seekToLine(intent.lineIndex)
                    is LyricsIntent.UpdateCurrentPosition -> updatePosition(intent.position)
                }
            }
        }
    }

    private suspend fun loadLyrics(fileName: String, audioFileName: String) {
        _state.update { it.copy(isLoading = true) }

        loadLyricsUseCase(fileName)
            .onSuccess { lyrics ->
                _state.update {
                    it.copy(
                        lyrics = lyrics,
                        isLoading = false,
                        error = null
                    )
                }
                playerRepository.loadMedia(audioFileName)
            }
            .onFailure { error ->
                Timber.e(error, "Failed to load lyrics")
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load lyrics"
                    )
                }
                _effects.send(
                    LyricsEffect.ShowError(
                        error.message ?: "Failed to load lyrics"
                    )
                )
            }
    }

    private suspend fun seekToLine(lineIndex: Int) {
        val line = _state.value.lyrics?.lines?.getOrNull(lineIndex)
        line?.let {
            playerRepository.seekTo(it.start.toLong())
            _effects.send(LyricsEffect.ScrollToLine(lineIndex))
        }
    }

    private fun updatePosition(position: Long) {
        // Position updates are handled by observeLyricsSync
    }

    private fun observeLyricsSync() {
        viewModelScope.launch {
            playerRepository.observePlaybackPosition().collect { position ->
                val lyrics = _state.value.lyrics
                val userSettings = _state.value.userSettings

                if (lyrics != null) {
                    val syncState = syncLyricsUseCase(
                        lyrics,
                        position,
                        userSettings.lyricsTimingOffsetMs
                    )
                    _state.update { it.copy(syncState = syncState) }
                }
            }
        }
    }

    private fun observeUserSettings() {
        viewModelScope.launch {
            observeUserSettingsUseCase().collect { settings ->
                _state.update { it.copy(userSettings = settings) }
            }
        }
    }
}