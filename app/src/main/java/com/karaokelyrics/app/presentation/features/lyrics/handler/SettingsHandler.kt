package com.karaokelyrics.app.presentation.features.lyrics.handler

import androidx.compose.ui.graphics.Color
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.domain.model.theme.ThemeColors
import com.karaokelyrics.app.domain.usecase.ObserveUserSettingsUseCase
import com.karaokelyrics.app.domain.usecase.UpdateUserSettingsUseCase
import com.karaokelyrics.app.domain.usecase.theme.GetCurrentThemeColorsUseCase
import com.karaokelyrics.app.presentation.features.settings.mapper.SettingsUiMapper.toColorArgb
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Handles settings-related operations.
 * Single Responsibility: Only manages user settings and theme.
 */
class SettingsHandler @Inject constructor(
    private val observeUserSettingsUseCase: ObserveUserSettingsUseCase,
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase,
    private val getCurrentThemeColorsUseCase: GetCurrentThemeColorsUseCase
) {

    /**
     * Data class combining settings with computed theme colors.
     */
    data class SettingsWithTheme(
        val settings: UserSettings,
        val themeColors: ThemeColors
    )

    /**
     * Observe user settings with computed theme colors.
     */
    fun observeSettingsWithTheme(): Flow<SettingsWithTheme> {
        return observeUserSettingsUseCase().map { settings ->
            SettingsWithTheme(
                settings = settings,
                themeColors = getCurrentThemeColorsUseCase(settings)
            )
        }
    }

    /**
     * Update lyrics color based on current theme.
     */
    suspend fun updateLyricsColor(color: Color) {
        // For now, update the color for current theme only
        // A more complete implementation would update the correct theme color
        updateUserSettingsUseCase.updateLyricsColor(color.toColorArgb())
    }

    /**
     * Update background color based on current theme.
     */
    suspend fun updateBackgroundColor(color: Color) {
        // For now, update the color for current theme only
        // A more complete implementation would update the correct theme color
        updateUserSettingsUseCase.updateBackgroundColor(color.toColorArgb())
    }

    /**
     * Update font size.
     */
    suspend fun updateFontSize(fontSize: FontSize) {
        updateUserSettingsUseCase.updateFontSize(fontSize)
    }

    /**
     * Update animations enabled state.
     */
    suspend fun updateAnimationsEnabled(enabled: Boolean) {
        updateUserSettingsUseCase.updateAnimationsEnabled(enabled)
    }

    /**
     * Update blur effect enabled state.
     */
    suspend fun updateBlurEffectEnabled(enabled: Boolean) {
        updateUserSettingsUseCase.updateBlurEffectEnabled(enabled)
    }

    /**
     * Update character animations enabled state.
     */
    suspend fun updateCharacterAnimationsEnabled(enabled: Boolean) {
        updateUserSettingsUseCase.updateCharacterAnimationsEnabled(enabled)
    }

    /**
     * Update dark mode state.
     */
    suspend fun updateDarkMode(isDark: Boolean) {
        updateUserSettingsUseCase.updateDarkMode(isDark)
    }

    /**
     * Reset all settings to defaults.
     */
    suspend fun resetToDefaults() {
        updateUserSettingsUseCase.resetToDefaults()
    }
}