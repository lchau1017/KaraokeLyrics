package com.karaokelyrics.app.presentation.features.settings.effect

sealed class SettingsEffect {
    data class ShowMessage(val message: String) : SettingsEffect()
    object SettingsUpdated : SettingsEffect()
    object SettingsReset : SettingsEffect()
}
