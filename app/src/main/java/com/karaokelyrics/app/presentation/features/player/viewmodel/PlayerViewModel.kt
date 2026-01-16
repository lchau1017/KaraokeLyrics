package com.karaokelyrics.app.presentation.features.player.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaokelyrics.app.domain.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Player controls.
 * Single Responsibility: Only manages playback controls.
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    data class PlayerState(
        val isPlaying: Boolean = false,
        val currentPosition: Long = 0L,
        val duration: Long = 0L
    )

    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    init {
        observePlaybackState()
    }

    fun play() {
        viewModelScope.launch {
            playerRepository.play()
        }
    }

    fun pause() {
        viewModelScope.launch {
            playerRepository.pause()
        }
    }

    fun togglePlayPause() {
        viewModelScope.launch {
            if (_state.value.isPlaying) {
                playerRepository.pause()
            } else {
                playerRepository.play()
            }
        }
    }

    fun seekTo(position: Long) {
        viewModelScope.launch {
            playerRepository.seekTo(position)
        }
    }

    fun seekToLine(lineStartMs: Int) {
        viewModelScope.launch {
            playerRepository.seekTo(lineStartMs.toLong())
        }
    }

    fun loadMedia(fileName: String) {
        viewModelScope.launch {
            playerRepository.loadMedia(fileName)
        }
    }

    private fun observePlaybackState() {
        viewModelScope.launch {
            combine(
                playerRepository.observeIsPlaying(),
                playerRepository.observePlaybackPosition()
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