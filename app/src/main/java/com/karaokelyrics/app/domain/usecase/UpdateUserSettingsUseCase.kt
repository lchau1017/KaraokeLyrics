package com.karaokelyrics.app.domain.usecase

import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Domain use case for updating user settings
 */
class UpdateUserSettingsUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {

    suspend fun updateLyricsColor(colorArgb: Int) {
        settingsRepository.updateLyricsColor(colorArgb)
    }

    suspend fun updateBackgroundColor(colorArgb: Int) {
        settingsRepository.updateBackgroundColor(colorArgb)
    }

    suspend fun updateFontSize(fontSize: FontSize) {
        settingsRepository.updateFontSize(fontSize)
    }

    suspend fun updateAnimationsEnabled(enabled: Boolean) {
        settingsRepository.updateAnimationsEnabled(enabled)
    }

    suspend fun updateBlurEffectEnabled(enabled: Boolean) {
        settingsRepository.updateBlurEffectEnabled(enabled)
    }

    suspend fun updateCharacterAnimationsEnabled(enabled: Boolean) {
        settingsRepository.updateCharacterAnimationsEnabled(enabled)
    }

    suspend fun updateLyricsTimingOffset(offsetMs: Int) {
        settingsRepository.updateLyricsTimingOffset(offsetMs)
    }

    suspend fun updateDarkMode(isDark: Boolean) {
        settingsRepository.updateDarkMode(isDark)
    }

    suspend fun resetToDefaults() {
        settingsRepository.resetToDefaults()
    }
}
