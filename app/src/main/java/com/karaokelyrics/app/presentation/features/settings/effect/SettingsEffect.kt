package com.karaokelyrics.app.presentation.features.settings.effect

import com.karaokelyrics.app.domain.model.LyricsSource

sealed class SettingsEffect {
    data class ShowMessage(val message: String) : SettingsEffect()
    object SettingsUpdated : SettingsEffect()
    object SettingsReset : SettingsEffect()
    data class LyricsSourceChanged(val lyricsSource: LyricsSource) : SettingsEffect()
}
