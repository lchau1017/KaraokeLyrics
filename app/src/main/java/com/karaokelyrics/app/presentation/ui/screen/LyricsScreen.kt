package com.karaokelyrics.app.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.karaokelyrics.app.presentation.ui.components.SettingsPanel
import com.karaokelyrics.app.presentation.viewmodel.LyricsViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LyricsScreen(
    viewModel: LyricsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

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

                    // Controls and Settings at bottom
                    Column(
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        // Settings Panel
                        SettingsPanel(
                            settings = state.userSettings,
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

                        // Playback controls
                        PlayerControls(
                            isPlaying = state.isPlaying,
                            position = state.playbackPosition,
                            duration = lyricsData.lines.lastOrNull()?.end?.toLong() ?: 0L,
                            onPlayPause = {
                                viewModel.processIntent(LyricsIntent.PlayPause)
                            },
                            onSeek = { position ->
                                viewModel.processIntent(LyricsIntent.SeekToPosition(position))
                            }
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
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
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Progress slider
            Slider(
                value = position.toFloat(),
                onValueChange = { onSeek(it.toLong()) },
                valueRange = 0f..duration.toFloat(),
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.outline
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier.size(64.dp)
                ) {
                    if (isPlaying) {
                        // Custom pause icon using two rectangles
                        Row(
                            modifier = Modifier.size(48.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(14.dp)
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .width(14.dp)
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            // Time display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(position),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = formatTime(duration),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / 1000) / 60
    return String.format("%02d:%02d", minutes, seconds)
}