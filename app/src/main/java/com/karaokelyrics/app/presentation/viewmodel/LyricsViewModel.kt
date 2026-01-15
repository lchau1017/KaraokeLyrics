package com.karaokelyrics.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaokelyrics.app.domain.repository.PlayerRepository
import com.karaokelyrics.app.domain.usecase.LoadLyricsUseCase
import com.karaokelyrics.app.domain.usecase.SyncLyricsUseCase
import com.karaokelyrics.app.presentation.effect.LyricsEffect
import com.karaokelyrics.app.presentation.intent.LyricsIntent
import com.karaokelyrics.app.presentation.state.LyricsUiState
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
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LyricsUiState())
    val state: StateFlow<LyricsUiState> = _state.asStateFlow()

    private val _effects = Channel<LyricsEffect>(Channel.BUFFERED)
    val effects: Flow<LyricsEffect> = _effects.receiveAsFlow()

    init {
        observePlaybackState()
    }

    fun processIntent(intent: LyricsIntent) {
        when (intent) {
            is LyricsIntent.LoadInitialLyrics -> loadInitialLyrics()
            is LyricsIntent.PlayPause -> togglePlayPause()
            is LyricsIntent.SeekToLine -> seekToLine(intent.lineIndex)
            is LyricsIntent.SeekToPosition -> seekToPosition(intent.position)
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
        // Observe playback position
        viewModelScope.launch {
            combine(
                playerRepository.observePlaybackPosition(),
                playerRepository.observeIsPlaying()
            ) { position, isPlaying ->
                position to isPlaying
            }.collect { (position, isPlaying) ->
                val lyrics = _state.value.lyrics
                if (lyrics != null) {
                    val syncState = syncLyricsUseCase(lyrics, position)
                    _state.update { currentState ->
                        currentState.copy(
                            syncState = syncState,
                            playbackPosition = position,
                            isPlaying = isPlaying
                        )
                    }
                } else {
                    _state.update { currentState ->
                        currentState.copy(
                            playbackPosition = position,
                            isPlaying = isPlaying
                        )
                    }
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
}