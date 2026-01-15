package com.karaokelyrics.app.presentation.intent

sealed class LyricsIntent {
    object LoadInitialLyrics : LyricsIntent()
    object PlayPause : LyricsIntent()
    data class SeekToLine(val lineIndex: Int) : LyricsIntent()
    data class SeekToPosition(val position: Long) : LyricsIntent()
}