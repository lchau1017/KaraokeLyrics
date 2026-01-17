package com.karaokelyrics.app.presentation.intent

import androidx.compose.ui.graphics.Color
import com.karaokelyrics.app.domain.model.FontSize

sealed class LyricsIntent {
    object LoadInitialLyrics : LyricsIntent()
    object PlayPause : LyricsIntent()
    data class SeekToLine(val lineIndex: Int) : LyricsIntent()
    data class SeekToPosition(val position: Long) : LyricsIntent()

    // Settings intents
    data class UpdateLyricsColor(val color: Color) : LyricsIntent()
    data class UpdateBackgroundColor(val color: Color) : LyricsIntent()
    data class UpdateFontSize(val fontSize: FontSize) : LyricsIntent()
    data class UpdateAnimationsEnabled(val enabled: Boolean) : LyricsIntent()
    data class UpdateBlurEffectEnabled(val enabled: Boolean) : LyricsIntent()
    data class UpdateCharacterAnimationsEnabled(val enabled: Boolean) : LyricsIntent()
    data class UpdateDarkMode(val isDark: Boolean) : LyricsIntent()
    object ResetSettingsToDefaults : LyricsIntent()
}