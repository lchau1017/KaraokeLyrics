package com.karaokelyrics.app.presentation.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karaokelyrics.app.domain.model.UserSettings
import com.kyrics.config.KyricsConfig
import com.kyrics.config.KyricsPresets
import com.kyrics.config.ViewerType
import com.kyrics.config.kyricsConfig
import javax.inject.Inject

/**
 * Maps app user settings to library configuration using the Kyrics DSL.
 * This maintains the decoupling between the app's settings model and the library's configuration.
 */
class LibraryConfigMapper @Inject constructor() {

    /**
     * Convert user settings to library configuration using Kyrics v1.3.0 DSL.
     */
    fun mapToLibraryConfig(userSettings: UserSettings): KyricsConfig {
        val primaryColor = Color(userSettings.lyricsColorArgb)
        val bgColor = Color(userSettings.backgroundColorArgb)

        return kyricsConfig {
            colors {
                playing = primaryColor
                played = primaryColor.copy(alpha = 0.7f)
                upcoming = primaryColor.copy(alpha = 0.4f)
                background = bgColor
            }

            typography {
                fontSize = userSettings.fontSize.sp.sp
                fontWeight = FontWeight.Bold
                textAlign = TextAlign.Center
            }

            gradient {
                enabled = false
                angle = 45f
            }

            blur {
                enabled = userSettings.enableBlurEffect
                playedLineBlur = 2.dp
                upcomingLineBlur = 3.dp
                distantLineBlur = 5.dp
            }

            viewer {
                type = ViewerType.SMOOTH_SCROLL
            }

            layout {
                lineSpacing = 12.dp
            }
        }
    }

    /**
     * Create a preset configuration based on user preference.
     */
    fun getPresetConfig(presetName: String): KyricsConfig = when (presetName.lowercase()) {
        "classic" -> KyricsPresets.Classic
        "neon" -> KyricsPresets.Neon
        else -> KyricsConfig.Default
    }
}
