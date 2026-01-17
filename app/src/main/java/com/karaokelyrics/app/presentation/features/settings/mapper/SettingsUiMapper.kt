package com.karaokelyrics.app.presentation.features.settings.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.karaokelyrics.app.domain.model.UserSettings

/**
 * Maps between domain UserSettings (with ARGB integers) and UI layer (with Compose Color)
 */
object SettingsUiMapper {

    /**
     * Extension to get Compose Colors from domain model
     */
    val UserSettings.darkLyricsColor: Color
        get() = Color(darkLyricsColorArgb)

    val UserSettings.darkBackgroundColor: Color
        get() = Color(darkBackgroundColorArgb)

    val UserSettings.lightLyricsColor: Color
        get() = Color(lightLyricsColorArgb)

    val UserSettings.lightBackgroundColor: Color
        get() = Color(lightBackgroundColorArgb)

    val UserSettings.lyricsColor: Color
        get() = Color(lyricsColorArgb)

    val UserSettings.backgroundColor: Color
        get() = Color(backgroundColorArgb)

    /**
     * Convert Color to ARGB for domain layer
     */
    fun Color.toColorArgb(): Int = this.toArgb()
}