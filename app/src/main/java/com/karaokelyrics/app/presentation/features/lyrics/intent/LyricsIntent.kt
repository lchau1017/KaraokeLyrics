package com.karaokelyrics.app.presentation.features.lyrics.intent

sealed class LyricsIntent {
    object LoadDefaultContent : LyricsIntent()
    data class LoadMediaContent(val contentId: String) : LyricsIntent()
    data class SeekToLine(val lineIndex: Int) : LyricsIntent()
    data class UpdateCurrentPosition(val position: Long) : LyricsIntent()
}