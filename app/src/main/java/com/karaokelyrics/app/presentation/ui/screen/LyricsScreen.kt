package com.karaokelyrics.app.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karaokelyrics.app.presentation.effect.LyricsEffect
import com.karaokelyrics.app.presentation.intent.LyricsIntent
import com.karaokelyrics.app.presentation.ui.components.KaraokeLyricsView
import com.karaokelyrics.app.presentation.ui.components.SettingsBottomSheet
import com.karaokelyrics.app.presentation.viewmodel.LyricsViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LyricsScreen(
    viewModel: LyricsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showSettings by remember { mutableStateOf(false) }

    // Handle effects
    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is LyricsEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is LyricsEffect.ScrollToLine -> {
                    // Handled in KaraokeLyricsView
                }
            }
        }
    }

    // Load initial lyrics
    LaunchedEffect(Unit) {
        viewModel.processIntent(LyricsIntent.LoadInitialLyrics)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "KaraokeLyrics",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sing Along",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            state.error != null -> {
                val errorMessage = state.error
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error loading lyrics",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "Unknown error",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.processIntent(LyricsIntent.LoadInitialLyrics)
                        }
                    ) {
                        Text("Retry")
                    }
                }
            }
            state.lyrics != null -> {
                state.lyrics?.let { lyricsData ->
                    KaraokeLyricsView(
                        lyrics = lyricsData,
                        currentPosition = { state.playbackPosition },
                        onLineClicked = { line ->
                            val index = lyricsData.lines.indexOf(line)
                            if (index >= 0) {
                                viewModel.processIntent(LyricsIntent.SeekToLine(index))
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .background(state.userSettings.backgroundColor),
                        normalLineTextStyle = LocalTextStyle.current.copy(
                            fontSize = state.userSettings.fontSize.sp.sp,
                            fontWeight = FontWeight.Bold,
                            textMotion = TextMotion.Animated
                        ),
                        accompanimentLineTextStyle = LocalTextStyle.current.copy(
                            fontSize = (state.userSettings.fontSize.sp * 0.6f).sp,
                            fontWeight = FontWeight.Bold,
                            textMotion = TextMotion.Animated
                        ),
                        textColor = state.userSettings.lyricsColor,
                        useBlurEffect = state.userSettings.enableBlurEffect && state.userSettings.enableAnimations,
                        enableCharacterAnimations = state.userSettings.enableCharacterAnimations && state.userSettings.enableAnimations
                    )

                    // Playback controls at bottom
                    PlayerControls(
                        isPlaying = state.isPlaying,
                        position = state.playbackPosition,
                        duration = lyricsData.lines.lastOrNull()?.end?.toLong() ?: 0L,
                        onPlayPause = {
                            viewModel.processIntent(LyricsIntent.PlayPause)
                        },
                        onSeek = { position ->
                            viewModel.processIntent(LyricsIntent.SeekToPosition(position))
                        },
                        onOpenSettings = {
                            showSettings = true
                        },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Settings Bottom Sheet
        SettingsBottomSheet(
            isVisible = showSettings,
            settings = state.userSettings,
            onDismiss = { showSettings = false },
            onUpdateLyricsColor = { color ->
                viewModel.processIntent(LyricsIntent.UpdateLyricsColor(color))
            },
            onUpdateBackgroundColor = { color ->
                viewModel.processIntent(LyricsIntent.UpdateBackgroundColor(color))
            },
            onUpdateFontSize = { fontSize ->
                viewModel.processIntent(LyricsIntent.UpdateFontSize(fontSize))
            },
            onUpdateAnimationsEnabled = { enabled ->
                viewModel.processIntent(LyricsIntent.UpdateAnimationsEnabled(enabled))
            },
            onUpdateBlurEffectEnabled = { enabled ->
                viewModel.processIntent(LyricsIntent.UpdateBlurEffectEnabled(enabled))
            },
            onUpdateCharacterAnimationsEnabled = { enabled ->
                viewModel.processIntent(LyricsIntent.UpdateCharacterAnimationsEnabled(enabled))
            },
            onUpdateDarkMode = { isDark ->
                viewModel.processIntent(LyricsIntent.UpdateDarkMode(isDark))
            },
            onResetToDefaults = {
                viewModel.processIntent(LyricsIntent.ResetSettingsToDefaults)
            }
        )
    }
}

@Composable
private fun PlayerControls(
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
            // Progress section at top
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Larger progress slider with visible thumb
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
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                // Time display
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

            // Controls row with play button and settings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Empty spacer for balance
                Spacer(modifier = Modifier.size(48.dp))

                // Play/Pause button in center
                FilledIconButton(
                    onClick = onPlayPause,
                    modifier = Modifier.size(56.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (isPlaying) {
                        // Custom pause icon
                        Row(
                            modifier = Modifier.size(28.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .fillMaxHeight(0.7f)
                                    .background(MaterialTheme.colorScheme.onPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .fillMaxHeight(0.7f)
                                    .background(MaterialTheme.colorScheme.onPrimary)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Settings icon on the right
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / 1000) / 60
    return String.format("%02d:%02d", minutes, seconds)
}