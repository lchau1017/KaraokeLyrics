package com.karaokelyrics.app.presentation.features.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.karaokelyrics.app.presentation.ui.core.*
import com.karaokelyrics.app.presentation.features.player.viewdata.PlayerControlsViewData
import com.karaokelyrics.app.presentation.features.player.viewdata.SliderColorsViewData
import com.karaokelyrics.app.presentation.features.player.viewdata.PlayButtonColorsViewData

@Composable
fun PlayerControls(
    isPlaying: Boolean,
    position: Long,
    duration: Long,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewData: PlayerControlsViewData = PlayerControlsViewData.default()
) {
    AppSurface(
        viewData = SurfaceViewData(
            backgroundColor = viewData.surfaceColor,
            contentColor = viewData.contentColor,
            elevation = viewData.elevation,
            tonalElevation = viewData.tonalElevation
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 20.dp)
        ) {
            ProgressSection(
                position = position,
                duration = duration,
                onSeek = onSeek,
                sliderColors = viewData.sliderColors,
                timeTextStyle = viewData.timeTextStyle,
                timeTextColor = viewData.timeTextColor
            )

            ControlButtons(
                isPlaying = isPlaying,
                onPlayPause = onPlayPause,
                onOpenSettings = onOpenSettings,
                playButtonColors = viewData.playButtonColors,
                settingsIconColor = viewData.settingsIconColor,
                playButtonSize = viewData.playButtonSize,
                settingsButtonSize = viewData.settingsButtonSize,
                iconSize = viewData.iconSize,
                settingsIconSize = viewData.settingsIconSize
            )
        }
    }
}

@Composable
private fun ProgressSection(
    position: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    sliderColors: SliderColorsViewData,
    timeTextStyle: androidx.compose.ui.text.TextStyle,
    timeTextColor: androidx.compose.ui.graphics.Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Slider(
            value = if (duration > 0) position.toFloat() else 0f,
            onValueChange = { newPosition ->
                if (duration > 0) {
                    onSeek(newPosition.toLong())
                }
            },
            valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = SliderDefaults.colors(
                thumbColor = sliderColors.thumbColor,
                activeTrackColor = sliderColors.activeTrackColor,
                inactiveTrackColor = sliderColors.inactiveTrackColor,
                disabledThumbColor = sliderColors.disabledThumbColor,
                disabledActiveTrackColor = sliderColors.disabledActiveTrackColor,
                disabledInactiveTrackColor = sliderColors.disabledInactiveTrackColor
            )
        )

        TimeDisplay(
            position = position,
            duration = duration,
            textStyle = timeTextStyle,
            textColor = timeTextColor
        )
    }
}

@Composable
private fun TimeDisplay(
    position: Long,
    duration: Long,
    textStyle: androidx.compose.ui.text.TextStyle,
    textColor: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AppText(
            viewData = TextViewData(
                text = formatTime(position),
                style = textStyle,
                color = textColor
            )
        )
        AppText(
            viewData = TextViewData(
                text = formatTime(duration),
                style = textStyle,
                color = textColor
            )
        )
    }
}

@Composable
private fun ControlButtons(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onOpenSettings: () -> Unit,
    playButtonColors: PlayButtonColorsViewData,
    settingsIconColor: androidx.compose.ui.graphics.Color,
    playButtonSize: androidx.compose.ui.unit.Dp,
    settingsButtonSize: androidx.compose.ui.unit.Dp,
    iconSize: androidx.compose.ui.unit.Dp,
    settingsIconSize: androidx.compose.ui.unit.Dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.size(settingsButtonSize))

        PlayPauseButton(
            isPlaying = isPlaying,
            onClick = onPlayPause,
            buttonColors = playButtonColors,
            buttonSize = playButtonSize,
            iconSize = iconSize
        )

        SettingsButton(
            onClick = onOpenSettings,
            iconColor = settingsIconColor,
            buttonSize = settingsButtonSize,
            iconSize = settingsIconSize
        )
    }
}

@Composable
private fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    buttonColors: PlayButtonColorsViewData,
    buttonSize: androidx.compose.ui.unit.Dp,
    iconSize: androidx.compose.ui.unit.Dp
) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(buttonSize),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = buttonColors.backgroundColor,
            contentColor = buttonColors.contentColor
        )
    ) {
        if (isPlaying) {
            PauseIcon(
                color = buttonColors.pauseIconColor,
                modifier = Modifier.size(iconSize * 0.875f) // 28.dp for 32.dp icon
            )
        } else {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = buttonColors.contentColor,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
private fun AppIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    viewData: IconButtonViewData,
    contentDescription: String? = null
) {
    if (viewData.backgroundColor != null && viewData.backgroundColor != Color.Transparent) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(viewData.size),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = viewData.backgroundColor,
                contentColor = viewData.contentColor ?: MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = viewData.contentColor ?: MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(viewData.iconSize)
            )
        }
    } else {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(viewData.size),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = viewData.contentColor ?: MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = viewData.contentColor ?: MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(viewData.iconSize)
            )
        }
    }
}

@Composable
private fun PauseIcon(
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(8.dp)
                .fillMaxHeight(0.7f)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .width(8.dp)
                .fillMaxHeight(0.7f)
                .background(color)
        )
    }
}

@Composable
private fun SettingsButton(
    onClick: () -> Unit,
    iconColor: androidx.compose.ui.graphics.Color,
    buttonSize: androidx.compose.ui.unit.Dp,
    iconSize: androidx.compose.ui.unit.Dp
) {
    AppIconButton(
        icon = Icons.Default.Settings,
        onClick = onClick,
        viewData = IconButtonViewData.default().copy(
            contentColor = iconColor,
            size = buttonSize,
            iconSize = iconSize
        ),
        contentDescription = "Settings"
    )
}

private fun formatTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / 1000) / 60
    return String.format("%02d:%02d", minutes, seconds)
}