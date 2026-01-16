package com.karaokelyrics.app.domain.usecase.theme

import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.domain.model.theme.ThemeColors
import javax.inject.Inject

/**
 * Use case for determining the current theme colors based on user settings.
 * This encapsulates the business logic that was previously in the UserSettings model.
 *
 * Single Responsibility: Only handles theme color resolution.
 */
class GetCurrentThemeColorsUseCase @Inject constructor() {

    /**
     * Get the current theme colors based on the dark/light mode setting.
     *
     * @param settings The user's current settings
     * @return ThemeColors with appropriate colors for the current theme
     */
    operator fun invoke(settings: UserSettings): ThemeColors {
        return if (settings.isDarkMode) {
            ThemeColors(
                lyricsColorArgb = settings.darkLyricsColorArgb,
                backgroundColorArgb = settings.darkBackgroundColorArgb
            )
        } else {
            ThemeColors(
                lyricsColorArgb = settings.lightLyricsColorArgb,
                backgroundColorArgb = settings.lightBackgroundColorArgb
            )
        }
    }
}