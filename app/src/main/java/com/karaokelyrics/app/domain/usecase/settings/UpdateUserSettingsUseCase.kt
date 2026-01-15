package com.karaokelyrics.app.domain.usecase.settings

import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.domain.repository.SettingsRepository
import androidx.compose.ui.graphics.Color
import javax.inject.Inject

/**
 * Use case for updating user settings
 * Single Responsibility: Update user preferences
 */
class UpdateUserSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend fun updateLyricsColor(color: Color) {
        settingsRepository.updateLyricsColor(color)
    }

    suspend fun updateBackgroundColor(color: Color) {
        settingsRepository.updateBackgroundColor(color)
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

    suspend fun updateDarkMode(isDark: Boolean) {
        settingsRepository.updateDarkMode(isDark)
    }

    suspend fun resetToDefaults() {
        settingsRepository.resetToDefaults()
    }
}