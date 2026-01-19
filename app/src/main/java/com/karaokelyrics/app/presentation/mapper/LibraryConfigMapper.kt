package com.karaokelyrics.app.presentation.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karaokelyrics.app.domain.model.UserSettings
import com.kyrics.config.KyricsConfig
import com.kyrics.config.ViewerType
import com.kyrics.config.kyricsConfig
import javax.inject.Inject

/**
 * Maps app user settings to library configuration using the Kyrics DSL.
 * This maintains the decoupling between the app's settings model and the library's configuration.
 */
class LibraryConfigMapper @Inject constructor() {

    /**
     * Convert user settings to library configuration using Kyrics DSL.
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

            animations {
                characterAnimations = userSettings.enableCharacterAnimations
                characterDuration = 800f
                characterScale = 1.15f
                characterFloat = 6f
                lineAnimations = userSettings.enableAnimations
                lineScale = 1.05f
            }

            effects {
                blur = userSettings.enableBlurEffect
                blurIntensity = 1.0f
            }

            gradient {
                enabled = false
                angle = 45f
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
        "minimal" -> KyricsConfig.Minimal
        "dramatic" -> KyricsConfig.Dramatic
        else -> KyricsConfig.Default
    }
}
