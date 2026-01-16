package com.karaokelyrics.app.presentation.features.settings.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.domain.model.theme.ThemeColors

/**
 * Maps between domain UserSettings (with ARGB integers) and UI layer (with Compose Color)
 * This follows SRP by only handling UI-Domain mapping, not business logic.
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

    /**
     * Extension to get Compose Colors from ThemeColors
     */
    val ThemeColors.lyricsColor: Color
        get() = Color(lyricsColorArgb)

    val ThemeColors.backgroundColor: Color
        get() = Color(backgroundColorArgb)

    /**
     * Convert Color to ARGB for domain layer
     */
    fun Color.toColorArgb(): Int = this.toArgb()
}