package com.karaokelyrics.app.presentation.features.lyrics.effect

sealed class LyricsEffect {
    data class ShowError(val message: String) : LyricsEffect()
    data class ScrollToLine(val lineIndex: Int) : LyricsEffect()
}