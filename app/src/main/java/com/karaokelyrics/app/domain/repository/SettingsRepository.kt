package com.karaokelyrics.app.domain.repository

import androidx.compose.ui.graphics.Color
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user settings
 * Interface Segregation Principle: Focused on settings operations only
 */
interface SettingsRepository {
    fun observeUserSettings(): Flow<UserSettings>
    suspend fun updateLyricsColor(color: Color)
    suspend fun updateBackgroundColor(color: Color)
    suspend fun updateFontSize(fontSize: FontSize)
    suspend fun updateAnimationsEnabled(enabled: Boolean)
    suspend fun updateBlurEffectEnabled(enabled: Boolean)
    suspend fun updateCharacterAnimationsEnabled(enabled: Boolean)
    suspend fun updateDarkMode(isDark: Boolean)
    suspend fun resetToDefaults()
}