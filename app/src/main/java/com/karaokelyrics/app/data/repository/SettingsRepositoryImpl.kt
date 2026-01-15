package com.karaokelyrics.app.data.repository

import androidx.compose.ui.graphics.Color
import com.karaokelyrics.app.data.datasource.local.SettingsLocalDataSource
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for settings
 * Dependency Inversion Principle: Depends on abstraction (SettingsRepository)
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val localDataSource: SettingsLocalDataSource
) : SettingsRepository {

    override fun observeUserSettings(): Flow<UserSettings> {
        return localDataSource.userSettings
    }

    override suspend fun updateLyricsColor(color: Color) {
        val settings = localDataSource.userSettings.first()
        if (settings.isDarkMode) {
            localDataSource.updateDarkLyricsColor(color)
        } else {
            localDataSource.updateLightLyricsColor(color)
        }
    }

    override suspend fun updateBackgroundColor(color: Color) {
        val settings = localDataSource.userSettings.first()
        if (settings.isDarkMode) {
            localDataSource.updateDarkBackgroundColor(color)
        } else {
            localDataSource.updateLightBackgroundColor(color)
        }
    }

    override suspend fun updateFontSize(fontSize: FontSize) {
        localDataSource.updateFontSize(fontSize)
    }

    override suspend fun updateAnimationsEnabled(enabled: Boolean) {
        localDataSource.updateAnimationsEnabled(enabled)
    }

    override suspend fun updateBlurEffectEnabled(enabled: Boolean) {
        localDataSource.updateBlurEffectEnabled(enabled)
    }

    override suspend fun updateCharacterAnimationsEnabled(enabled: Boolean) {
        localDataSource.updateCharacterAnimationsEnabled(enabled)
    }

    override suspend fun updateDarkMode(isDark: Boolean) {
        localDataSource.updateDarkMode(isDark)
    }

    override suspend fun resetToDefaults() {
        localDataSource.clearAll()
    }
}