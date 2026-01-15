package com.karaokelyrics.app.domain.repository

import androidx.compose.ui.graphics.Color
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    /**
     * Observe user settings changes
     */
    fun getUserSettings(): Flow<UserSettings>

    /**
     * Update lyrics color
     */
    suspend fun updateLyricsColor(color: Color)

    /**
     * Update background color
     */
    suspend fun updateBackgroundColor(color: Color)

    /**
     * Update font size preference
     */
    suspend fun updateFontSize(fontSize: FontSize)

    /**
     * Update animations enabled state
     */
    suspend fun updateAnimationsEnabled(enabled: Boolean)

    /**
     * Update blur effect enabled state
     */
    suspend fun updateBlurEffectEnabled(enabled: Boolean)

    /**
     * Update character animations enabled state
     */
    suspend fun updateCharacterAnimationsEnabled(enabled: Boolean)

    /**
     * Update lyrics timing offset in milliseconds
     */
    suspend fun updateLyricsTimingOffset(offsetMs: Int)

    /**
     * Update dark mode preference
     */
    suspend fun updateDarkMode(isDark: Boolean)

    /**
     * Reset all settings to default values
     */
    suspend fun resetToDefaults()
}