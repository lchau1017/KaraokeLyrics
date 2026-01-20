package com.karaokelyrics.app.presentation.features.settings.intent

import androidx.compose.ui.graphics.Color
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.LyricsSource

sealed class SettingsIntent {
    data class UpdateLyricsColor(val color: Color) : SettingsIntent()
    data class UpdateBackgroundColor(val color: Color) : SettingsIntent()
    data class UpdateFontSize(val fontSize: FontSize) : SettingsIntent()
    data class UpdateAnimationsEnabled(val enabled: Boolean) : SettingsIntent()
    data class UpdateBlurEffectEnabled(val enabled: Boolean) : SettingsIntent()
    data class UpdateCharacterAnimationsEnabled(val enabled: Boolean) : SettingsIntent()
    data class UpdateDarkMode(val isDark: Boolean) : SettingsIntent()
    data class UpdateLyricsSource(val lyricsSource: LyricsSource) : SettingsIntent()
    object ResetToDefaults : SettingsIntent()
}
