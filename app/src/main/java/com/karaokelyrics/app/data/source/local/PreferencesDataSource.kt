package com.karaokelyrics.app.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.UserSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// Extension property to get DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

/**
 * Local data source for user preferences using DataStore.
 * Single responsibility: Persist and retrieve user settings.
 */
@Singleton
class PreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Define preference keys
    private object PreferencesKeys {
        val DARK_LYRICS_COLOR = intPreferencesKey("dark_lyrics_color")
        val DARK_BACKGROUND_COLOR = intPreferencesKey("dark_background_color")
        val LIGHT_LYRICS_COLOR = intPreferencesKey("light_lyrics_color")
        val LIGHT_BACKGROUND_COLOR = intPreferencesKey("light_background_color")
        val FONT_SIZE = stringPreferencesKey("font_size")
        val ENABLE_ANIMATIONS = booleanPreferencesKey("enable_animations")
        val ENABLE_BLUR_EFFECT = booleanPreferencesKey("enable_blur_effect")
        val ENABLE_CHARACTER_ANIMATIONS = booleanPreferencesKey("enable_character_animations")
        val LYRICS_TIMING_OFFSET_MS = intPreferencesKey("lyrics_timing_offset_ms")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    /**
     * Default settings values.
     */
    private val defaultSettings = UserSettings()

    /**
     * Observe user settings as a Flow.
     */
    val userSettings: Flow<UserSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            mapPreferencesToUserSettings(preferences)
        }

    /**
     * Update a specific setting.
     */
    suspend fun updateLyricsColor(color: Int) {
        context.dataStore.edit { preferences ->
            val isDark = preferences[PreferencesKeys.IS_DARK_MODE] ?: true
            if (isDark) {
                preferences[PreferencesKeys.DARK_LYRICS_COLOR] = color
            } else {
                preferences[PreferencesKeys.LIGHT_LYRICS_COLOR] = color
            }
        }
    }

    suspend fun updateBackgroundColor(color: Int) {
        context.dataStore.edit { preferences ->
            val isDark = preferences[PreferencesKeys.IS_DARK_MODE] ?: true
            if (isDark) {
                preferences[PreferencesKeys.DARK_BACKGROUND_COLOR] = color
            } else {
                preferences[PreferencesKeys.LIGHT_BACKGROUND_COLOR] = color
            }
        }
    }

    suspend fun updateFontSize(fontSize: FontSize) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FONT_SIZE] = fontSize.name
        }
    }

    suspend fun updateAnimationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ENABLE_ANIMATIONS] = enabled
        }
    }

    suspend fun updateBlurEffectEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ENABLE_BLUR_EFFECT] = enabled
        }
    }

    suspend fun updateCharacterAnimationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ENABLE_CHARACTER_ANIMATIONS] = enabled
        }
    }

    suspend fun updateLyricsTimingOffset(offsetMs: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LYRICS_TIMING_OFFSET_MS] = offsetMs
        }
    }

    suspend fun updateDarkModeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_MODE] = enabled
        }
    }

    /**
     * Update all settings at once.
     */
    suspend fun updateAllSettings(settings: UserSettings) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_LYRICS_COLOR] = settings.darkLyricsColorArgb
            preferences[PreferencesKeys.DARK_BACKGROUND_COLOR] = settings.darkBackgroundColorArgb
            preferences[PreferencesKeys.LIGHT_LYRICS_COLOR] = settings.lightLyricsColorArgb
            preferences[PreferencesKeys.LIGHT_BACKGROUND_COLOR] = settings.lightBackgroundColorArgb
            preferences[PreferencesKeys.FONT_SIZE] = settings.fontSize.name
            preferences[PreferencesKeys.ENABLE_ANIMATIONS] = settings.enableAnimations
            preferences[PreferencesKeys.ENABLE_BLUR_EFFECT] = settings.enableBlurEffect
            preferences[PreferencesKeys.ENABLE_CHARACTER_ANIMATIONS] = settings.enableCharacterAnimations
            preferences[PreferencesKeys.LYRICS_TIMING_OFFSET_MS] = settings.lyricsTimingOffsetMs
            preferences[PreferencesKeys.IS_DARK_MODE] = settings.isDarkMode
        }
    }

    /**
     * Clear all settings (reset to defaults).
     */
    suspend fun clearSettings() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Map preferences to domain model.
     */
    private fun mapPreferencesToUserSettings(preferences: Preferences): UserSettings {
        return UserSettings(
            darkLyricsColorArgb = preferences[PreferencesKeys.DARK_LYRICS_COLOR] ?: defaultSettings.darkLyricsColorArgb,
            darkBackgroundColorArgb = preferences[PreferencesKeys.DARK_BACKGROUND_COLOR] ?: defaultSettings.darkBackgroundColorArgb,
            lightLyricsColorArgb = preferences[PreferencesKeys.LIGHT_LYRICS_COLOR] ?: defaultSettings.lightLyricsColorArgb,
            lightBackgroundColorArgb = preferences[PreferencesKeys.LIGHT_BACKGROUND_COLOR] ?: defaultSettings.lightBackgroundColorArgb,
            fontSize = preferences[PreferencesKeys.FONT_SIZE]?.let {
                try {
                    FontSize.valueOf(it)
                } catch (e: IllegalArgumentException) {
                    defaultSettings.fontSize
                }
            } ?: defaultSettings.fontSize,
            enableAnimations = preferences[PreferencesKeys.ENABLE_ANIMATIONS] ?: defaultSettings.enableAnimations,
            enableBlurEffect = preferences[PreferencesKeys.ENABLE_BLUR_EFFECT] ?: defaultSettings.enableBlurEffect,
            enableCharacterAnimations = preferences[PreferencesKeys.ENABLE_CHARACTER_ANIMATIONS] ?: defaultSettings.enableCharacterAnimations,
            lyricsTimingOffsetMs = preferences[PreferencesKeys.LYRICS_TIMING_OFFSET_MS] ?: defaultSettings.lyricsTimingOffsetMs,
            isDarkMode = preferences[PreferencesKeys.IS_DARK_MODE] ?: defaultSettings.isDarkMode
        )
    }
}