package com.karaokelyrics.app.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data source for user settings
 * Single Responsibility: Manage settings persistence
 */
@Singleton
class SettingsLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val DARK_LYRICS_COLOR_KEY = intPreferencesKey("dark_lyrics_color")
        private val DARK_BACKGROUND_COLOR_KEY = intPreferencesKey("dark_background_color")
        private val LIGHT_LYRICS_COLOR_KEY = intPreferencesKey("light_lyrics_color")
        private val LIGHT_BACKGROUND_COLOR_KEY = intPreferencesKey("light_background_color")
        private val FONT_SIZE_KEY = stringPreferencesKey("font_size")
        private val ENABLE_ANIMATIONS_KEY = booleanPreferencesKey("enable_animations")
        private val ENABLE_BLUR_EFFECT_KEY = booleanPreferencesKey("enable_blur_effect")
        private val ENABLE_CHARACTER_ANIMATIONS_KEY = booleanPreferencesKey("enable_character_animations")
        private val IS_DARK_MODE_KEY = booleanPreferencesKey("is_dark_mode")
    }

    val userSettings: Flow<UserSettings> = dataStore.data.map { preferences ->
        UserSettings(
            darkLyricsColor = Color(preferences[DARK_LYRICS_COLOR_KEY] ?: UserSettings.DEFAULT_DARK_LYRICS_COLOR.toArgb()),
            darkBackgroundColor = Color(preferences[DARK_BACKGROUND_COLOR_KEY] ?: UserSettings.DEFAULT_DARK_BACKGROUND_COLOR.toArgb()),
            lightLyricsColor = Color(preferences[LIGHT_LYRICS_COLOR_KEY] ?: UserSettings.DEFAULT_LIGHT_LYRICS_COLOR.toArgb()),
            lightBackgroundColor = Color(preferences[LIGHT_BACKGROUND_COLOR_KEY] ?: UserSettings.DEFAULT_LIGHT_BACKGROUND_COLOR.toArgb()),
            fontSize = FontSize.valueOf(preferences[FONT_SIZE_KEY] ?: FontSize.MEDIUM.name),
            enableAnimations = preferences[ENABLE_ANIMATIONS_KEY] ?: true,
            enableBlurEffect = preferences[ENABLE_BLUR_EFFECT_KEY] ?: true,
            enableCharacterAnimations = preferences[ENABLE_CHARACTER_ANIMATIONS_KEY] ?: true,
            isDarkMode = preferences[IS_DARK_MODE_KEY] ?: true
        )
    }

    suspend fun updateSetting(key: Preferences.Key<*>, value: Any) {
        dataStore.edit { preferences ->
            @Suppress("UNCHECKED_CAST")
            when (value) {
                is Int -> preferences[key as Preferences.Key<Int>] = value
                is String -> preferences[key as Preferences.Key<String>] = value
                is Boolean -> preferences[key as Preferences.Key<Boolean>] = value
            }
        }
    }

    suspend fun updateDarkLyricsColor(color: Color) {
        updateSetting(DARK_LYRICS_COLOR_KEY, color.toArgb())
    }

    suspend fun updateDarkBackgroundColor(color: Color) {
        updateSetting(DARK_BACKGROUND_COLOR_KEY, color.toArgb())
    }

    suspend fun updateLightLyricsColor(color: Color) {
        updateSetting(LIGHT_LYRICS_COLOR_KEY, color.toArgb())
    }

    suspend fun updateLightBackgroundColor(color: Color) {
        updateSetting(LIGHT_BACKGROUND_COLOR_KEY, color.toArgb())
    }

    suspend fun updateFontSize(fontSize: FontSize) {
        updateSetting(FONT_SIZE_KEY, fontSize.name)
    }

    suspend fun updateAnimationsEnabled(enabled: Boolean) {
        updateSetting(ENABLE_ANIMATIONS_KEY, enabled)
    }

    suspend fun updateBlurEffectEnabled(enabled: Boolean) {
        updateSetting(ENABLE_BLUR_EFFECT_KEY, enabled)
    }

    suspend fun updateCharacterAnimationsEnabled(enabled: Boolean) {
        updateSetting(ENABLE_CHARACTER_ANIMATIONS_KEY, enabled)
    }

    suspend fun updateDarkMode(isDark: Boolean) {
        updateSetting(IS_DARK_MODE_KEY, isDark)
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}