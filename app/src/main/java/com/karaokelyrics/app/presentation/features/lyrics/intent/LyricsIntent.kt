package com.karaokelyrics.app.presentation.features.lyrics.intent

sealed class LyricsIntent {
    data class LoadLyrics(val fileName: String, val audioFileName: String) : LyricsIntent()
    data class SeekToLine(val lineIndex: Int) : LyricsIntent()
    data class UpdateCurrentPosition(val position: Long) : LyricsIntent()
}