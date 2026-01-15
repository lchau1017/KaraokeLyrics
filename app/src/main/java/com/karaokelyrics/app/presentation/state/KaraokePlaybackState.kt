package com.karaokelyrics.app.presentation.state

import androidx.compose.runtime.*
import com.karaokelyrics.app.domain.model.ISyncedLine
import com.karaokelyrics.app.domain.model.SyncedLyrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Centralized state management for karaoke playback
 */
class KaraokePlaybackState {
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _lyrics = MutableStateFlow<SyncedLyrics?>(null)
    val lyrics: StateFlow<SyncedLyrics?> = _lyrics.asStateFlow()

    private val _currentLineIndex = MutableStateFlow(-1)
    val currentLineIndex: StateFlow<Int> = _currentLineIndex.asStateFlow()

    fun updatePosition(position: Long) {
        _currentPosition.value = position
        updateCurrentLine()
    }

    fun setPlaying(playing: Boolean) {
        _isPlaying.value = playing
    }

    fun setLyrics(lyrics: SyncedLyrics?) {
        _lyrics.value = lyrics
        updateCurrentLine()
    }

    private fun updateCurrentLine() {
        val lyricsData = _lyrics.value ?: return
        val position = _currentPosition.value

        val newIndex = lyricsData.lines.indexOfLast { line ->
            position >= line.start && position < line.end
        }

        if (newIndex != _currentLineIndex.value) {
            _currentLineIndex.value = newIndex
        }
    }

    fun seekToLine(lineIndex: Int) {
        val lyricsData = _lyrics.value ?: return
        if (lineIndex in lyricsData.lines.indices) {
            val line = lyricsData.lines[lineIndex]
            _currentPosition.value = line.start.toLong()
        }
    }

    fun getActiveLines(position: Long = _currentPosition.value): List<Int> {
        val lyricsData = _lyrics.value ?: return emptyList()
        return lyricsData.lines.mapIndexedNotNull { index, line ->
            if (position >= line.start && position < line.end) index else null
        }
    }

    fun getLineDistance(index: Int, activeIndices: List<Int>): Int {
        return when {
            activeIndices.isEmpty() -> Int.MAX_VALUE
            index in activeIndices -> 0
            index < activeIndices.first() -> activeIndices.first() - index
            index > activeIndices.last() -> index - activeIndices.last()
            else -> 0
        }
    }
}

@Composable
fun rememberKaraokePlaybackState(): KaraokePlaybackState {
    return remember { KaraokePlaybackState() }
}