package com.karaokelyrics.app.data.preferences

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.UserSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        // Dark theme colors
        val DARK_LYRICS_COLOR_KEY = intPreferencesKey("dark_lyrics_color")
        val DARK_BACKGROUND_COLOR_KEY = intPreferencesKey("dark_background_color")

        // Light theme colors
        val LIGHT_LYRICS_COLOR_KEY = intPreferencesKey("light_lyrics_color")
        val LIGHT_BACKGROUND_COLOR_KEY = intPreferencesKey("light_background_color")

        // Legacy keys for migration
        val LYRICS_COLOR_KEY = intPreferencesKey("lyrics_color")
        val BACKGROUND_COLOR_KEY = intPreferencesKey("background_color")

        val FONT_SIZE_KEY = stringPreferencesKey("font_size")
        val ENABLE_ANIMATIONS_KEY = booleanPreferencesKey("enable_animations")
        val ENABLE_BLUR_EFFECT_KEY = booleanPreferencesKey("enable_blur_effect")
        val ENABLE_CHARACTER_ANIMATIONS_KEY = booleanPreferencesKey("enable_character_animations")
        val LYRICS_TIMING_OFFSET_MS_KEY = intPreferencesKey("lyrics_timing_offset_ms")
        val IS_DARK_MODE_KEY = booleanPreferencesKey("is_dark_mode")
    }

    val userSettings: Flow<UserSettings> = dataStore.data.map { preferences ->
        // Migration: Use legacy keys if new ones don't exist
        val defaultDarkLyrics = Color(0xFF1DB954).toArgb()
        val defaultDarkBackground = Color(0xFF121212).toArgb()
        val defaultLightLyrics = Color(0xFF1DB954).toArgb()
        val defaultLightBackground = Color(0xFFFFFFFF).toArgb()

        UserSettings(
            darkLyricsColor = Color(
                preferences[DARK_LYRICS_COLOR_KEY]
                    ?: preferences[LYRICS_COLOR_KEY]
                    ?: defaultDarkLyrics
            ),
            darkBackgroundColor = Color(
                preferences[DARK_BACKGROUND_COLOR_KEY]
                    ?: preferences[BACKGROUND_COLOR_KEY]
                    ?: defaultDarkBackground
            ),
            lightLyricsColor = Color(
                preferences[LIGHT_LYRICS_COLOR_KEY] ?: defaultLightLyrics
            ),
            lightBackgroundColor = Color(
                preferences[LIGHT_BACKGROUND_COLOR_KEY] ?: defaultLightBackground
            ),
            fontSize = FontSize.valueOf(preferences[FONT_SIZE_KEY] ?: FontSize.MEDIUM.name),
            enableAnimations = preferences[ENABLE_ANIMATIONS_KEY] ?: true,
            enableBlurEffect = preferences[ENABLE_BLUR_EFFECT_KEY] ?: true,
            enableCharacterAnimations = preferences[ENABLE_CHARACTER_ANIMATIONS_KEY] ?: true,
            lyricsTimingOffsetMs = preferences[LYRICS_TIMING_OFFSET_MS_KEY] ?: 200,
            isDarkMode = preferences[IS_DARK_MODE_KEY] ?: true
        )
    }

    suspend fun updateLyricsColor(color: Color) {
        dataStore.edit { preferences ->
            val isDark = preferences[IS_DARK_MODE_KEY] ?: true
            if (isDark) {
                preferences[DARK_LYRICS_COLOR_KEY] = color.toArgb()
            } else {
                preferences[LIGHT_LYRICS_COLOR_KEY] = color.toArgb()
            }
        }
    }

    suspend fun updateBackgroundColor(color: Color) {
        dataStore.edit { preferences ->
            val isDark = preferences[IS_DARK_MODE_KEY] ?: true
            if (isDark) {
                preferences[DARK_BACKGROUND_COLOR_KEY] = color.toArgb()
            } else {
                preferences[LIGHT_BACKGROUND_COLOR_KEY] = color.toArgb()
            }
        }
    }

    suspend fun updateFontSize(fontSize: FontSize) {
        dataStore.edit { preferences ->
            preferences[FONT_SIZE_KEY] = fontSize.name
        }
    }

    suspend fun updateAnimationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[ENABLE_ANIMATIONS_KEY] = enabled
        }
    }

    suspend fun updateBlurEffectEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[ENABLE_BLUR_EFFECT_KEY] = enabled
        }
    }

    suspend fun updateCharacterAnimationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[ENABLE_CHARACTER_ANIMATIONS_KEY] = enabled
        }
    }

    suspend fun updateLyricsTimingOffset(offsetMs: Int) {
        dataStore.edit { preferences ->
            preferences[LYRICS_TIMING_OFFSET_MS_KEY] = offsetMs
        }
    }

    suspend fun updateDarkMode(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE_KEY] = isDark
        }
    }

    suspend fun resetToDefaults() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}