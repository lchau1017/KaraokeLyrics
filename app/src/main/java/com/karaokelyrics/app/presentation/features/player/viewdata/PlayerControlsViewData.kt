package com.karaokelyrics.app.presentation.features.player.viewdata

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * ViewData for PlayerControls styling
 */
data class PlayerControlsViewData(
    val surfaceColor: Color,
    val contentColor: Color,
    val sliderColors: SliderColorsViewData,
    val timeTextStyle: TextStyle,
    val timeTextColor: Color,
    val playButtonColors: PlayButtonColorsViewData,
    val settingsIconColor: Color,
    val playButtonSize: Dp = 56.dp,
    val settingsButtonSize: Dp = 48.dp,
    val iconSize: Dp = 32.dp,
    val settingsIconSize: Dp = 24.dp,
    val elevation: Dp = 0.dp,
    val tonalElevation: Dp = 1.dp
) {
    companion object {
        @Composable
        fun default() = PlayerControlsViewData(
            surfaceColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            sliderColors = SliderColorsViewData.default(),
            timeTextStyle = MaterialTheme.typography.labelSmall,
            timeTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            playButtonColors = PlayButtonColorsViewData.default(),
            settingsIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class SliderColorsViewData(
    val thumbColor: Color,
    val activeTrackColor: Color,
    val inactiveTrackColor: Color,
    val disabledThumbColor: Color,
    val disabledActiveTrackColor: Color,
    val disabledInactiveTrackColor: Color
) {
    companion object {
        @Composable
        fun default() = SliderColorsViewData(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledActiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledInactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f)
        )
    }
}

data class PlayButtonColorsViewData(
    val backgroundColor: Color,
    val contentColor: Color,
    val pauseIconColor: Color
) {
    companion object {
        @Composable
        fun default() = PlayButtonColorsViewData(
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            pauseIconColor = MaterialTheme.colorScheme.onPrimary
        )
    }
}