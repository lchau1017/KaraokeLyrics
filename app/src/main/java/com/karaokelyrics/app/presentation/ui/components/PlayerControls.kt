package com.karaokelyrics.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.karaokelyrics.app.presentation.ui.theme.ColorStyles

@Composable
fun PlayerControls(
    isPlaying: Boolean,
    position: Long,
    duration: Long,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.dp,
        tonalElevation = 1.dp
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
                onSeek = onSeek
            )

            ControlButtons(
                isPlaying = isPlaying,
                onPlayPause = onPlayPause,
                onOpenSettings = onOpenSettings
            )
        }
    }
}

@Composable
private fun ProgressSection(
    position: Long,
    duration: Long,
    onSeek: (Long) -> Unit
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
                thumbColor = ColorStyles.sliderThumb(),
                activeTrackColor = ColorStyles.sliderActiveTrack(),
                inactiveTrackColor = ColorStyles.sliderInactiveTrack()
            )
        )

        TimeDisplay(
            position = position,
            duration = duration
        )
    }
}

@Composable
private fun TimeDisplay(
    position: Long,
    duration: Long
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = formatTime(position),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = formatTime(duration),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun ControlButtons(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.size(48.dp))

        PlayPauseButton(
            isPlaying = isPlaying,
            onClick = onPlayPause
        )

        SettingsButton(
            onClick = onOpenSettings
        )
    }
}

@Composable
private fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(56.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = ColorStyles.primaryButton(),
            contentColor = ColorStyles.onPrimaryButton()
        )
    ) {
        if (isPlaying) {
            PauseIcon()
        } else {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = ColorStyles.onPrimaryButton(),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun PauseIcon() {
    Row(
        modifier = Modifier.size(28.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(8.dp)
                .fillMaxHeight(0.7f)
                .background(ColorStyles.onPrimaryButton())
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .width(8.dp)
                .fillMaxHeight(0.7f)
                .background(ColorStyles.onPrimaryButton())
        )
    }
}

@Composable
private fun SettingsButton(
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = ColorStyles.controlIcon(),
            modifier = Modifier.size(24.dp)
        )
    }
}

private fun formatTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / 1000) / 60
    return String.format("%02d:%02d", minutes, seconds)
}