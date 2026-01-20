package com.karaokelyrics.app.presentation.features.lyrics.intent

import com.karaokelyrics.app.domain.model.LyricsSource

sealed class LyricsIntent {
    object LoadDefaultContent : LyricsIntent()
    data class LoadMediaContent(val contentId: String) : LyricsIntent()
    data class LoadLyricsWithSource(val lyricsSource: LyricsSource) : LyricsIntent()
    data class SeekToLine(val lineIndex: Int) : LyricsIntent()
    data class UpdateCurrentPosition(val position: Long) : LyricsIntent()
}
