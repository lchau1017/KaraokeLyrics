package com.karaokelyrics.app.data.repository

import com.karaokelyrics.app.data.source.local.PreferencesDataSource
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class SettingsRepositoryImpl @Inject constructor(private val preferencesDataSource: PreferencesDataSource) : SettingsRepository {

    override fun getUserSettings(): Flow<UserSettings> = preferencesDataSource.userSettings

    override suspend fun updateLyricsColor(colorArgb: Int) {
        preferencesDataSource.updateLyricsColor(colorArgb)
    }

    override suspend fun updateBackgroundColor(colorArgb: Int) {
        preferencesDataSource.updateBackgroundColor(colorArgb)
    }

    override suspend fun updateFontSize(fontSize: FontSize) {
        preferencesDataSource.updateFontSize(fontSize)
    }

    override suspend fun updateAnimationsEnabled(enabled: Boolean) {
        preferencesDataSource.updateAnimationsEnabled(enabled)
    }

    override suspend fun updateBlurEffectEnabled(enabled: Boolean) {
        preferencesDataSource.updateBlurEffectEnabled(enabled)
    }

    override suspend fun updateCharacterAnimationsEnabled(enabled: Boolean) {
        preferencesDataSource.updateCharacterAnimationsEnabled(enabled)
    }

    override suspend fun updateLyricsTimingOffset(offsetMs: Int) {
        preferencesDataSource.updateLyricsTimingOffset(offsetMs)
    }

    override suspend fun updateDarkMode(isDark: Boolean) {
        preferencesDataSource.updateDarkModeEnabled(isDark)
    }

    override suspend fun resetToDefaults() {
        preferencesDataSource.clearSettings()
    }
}
