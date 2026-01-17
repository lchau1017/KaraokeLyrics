package com.karaokelyrics.app.presentation.features.player.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaokelyrics.app.presentation.features.player.effect.PlayerEffect
import com.karaokelyrics.app.presentation.features.player.intent.PlayerIntent
import com.karaokelyrics.app.presentation.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * MVI ViewModel for Player controls following Clean Architecture.
 * Single Responsibility: Manages playback state and handles intents.
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(private val playerController: PlayerController) : ViewModel() {

    data class PlayerState(val isPlaying: Boolean = false, val currentPosition: Long = 0L, val duration: Long = 0L)

    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    private val _effects = Channel<PlayerEffect>(Channel.BUFFERED)
    val effects: Flow<PlayerEffect> = _effects.receiveAsFlow()

    private val _intents = Channel<PlayerIntent>(Channel.BUFFERED)

    init {
        observePlaybackState()
        processIntents()
    }

    fun handleIntent(intent: PlayerIntent) {
        viewModelScope.launch {
            _intents.send(intent)
        }
    }

    private fun processIntents() {
        viewModelScope.launch {
            _intents.receiveAsFlow().collect { intent ->
                when (intent) {
                    is PlayerIntent.PlayPause -> togglePlayPause()
                    is PlayerIntent.SeekToPosition -> seekTo(intent.position)
                    is PlayerIntent.LoadMedia -> loadMedia(intent.fileName)
                }
            }
        }
    }

    private suspend fun togglePlayPause() {
        if (_state.value.isPlaying) {
            playerController.pause()
            _effects.send(PlayerEffect.PlaybackPaused)
        } else {
            playerController.play()
            _effects.send(PlayerEffect.PlaybackStarted)
        }
    }

    private suspend fun seekTo(position: Long) {
        playerController.seekTo(position)
        _effects.send(PlayerEffect.SeekCompleted(position))
    }

    private suspend fun loadMedia(fileName: String) {
        try {
            playerController.loadMedia(fileName)
        } catch (e: Exception) {
            _effects.send(PlayerEffect.ShowError(e.message ?: "Failed to load media"))
        }
    }

    private fun observePlaybackState() {
        viewModelScope.launch {
            combine(
                playerController.observeIsPlaying(),
                playerController.observePlaybackPosition()
            ) { isPlaying, position ->
                PlayerState(
                    isPlaying = isPlaying,
                    currentPosition = position,
                    duration = _state.value.duration // Keep existing duration
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    fun setDuration(duration: Long) {
        _state.update { it.copy(duration = duration) }
    }
}
