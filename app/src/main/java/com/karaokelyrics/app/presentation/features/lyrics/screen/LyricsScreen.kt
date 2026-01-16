package com.karaokelyrics.app.presentation.features.lyrics.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karaokelyrics.app.presentation.features.common.components.ErrorScreen
import com.karaokelyrics.app.presentation.features.common.components.LoadingScreen
import com.karaokelyrics.app.presentation.features.lyrics.components.KaraokeLyricsViewNew
import com.karaokelyrics.app.presentation.features.lyrics.effect.LyricsEffect
import com.karaokelyrics.app.presentation.features.lyrics.intent.LyricsIntent
import com.karaokelyrics.app.presentation.features.lyrics.viewmodel.LyricsViewModel
import com.karaokelyrics.app.presentation.features.player.components.PlayerControls
import com.karaokelyrics.app.presentation.features.player.intent.PlayerIntent
import com.karaokelyrics.app.presentation.features.player.viewmodel.PlayerViewModel
import com.karaokelyrics.app.presentation.features.settings.components.SettingsBottomSheet
import com.karaokelyrics.app.presentation.features.settings.intent.SettingsIntent
// Removed unused imports
import com.karaokelyrics.app.presentation.features.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Main Lyrics Screen using separate ViewModels for better separation of concerns.
 * Each ViewModel has a single responsibility:
 * - LyricsViewModel: Lyrics display and sync
 * - PlayerViewModel: Playback controls
 * - SettingsViewModel: User settings
 */
@Composable
fun LyricsScreen(
    lyricsViewModel: LyricsViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val lyricsState by lyricsViewModel.state.collectAsStateWithLifecycle()
    val playerState by playerViewModel.state.collectAsStateWithLifecycle()
    val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showSettings by remember { mutableStateOf(false) }

    // Handle effects from lyrics ViewModel
    LaunchedEffect(lyricsViewModel) {
        lyricsViewModel.effects.collectLatest { effect ->
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

    // Load initial lyrics using MVI intent
    LaunchedEffect(Unit) {
        lyricsViewModel.handleIntent(
            LyricsIntent.LoadLyrics("golden-hour.ttml", "golden-hour.m4a")
        )
    }

    // Set duration when lyrics are loaded
    LaunchedEffect(lyricsState.lyrics) {
        lyricsState.lyrics?.let { lyrics ->
            val duration = lyrics.lines.lastOrNull()?.end?.toLong() ?: 0L
            playerViewModel.setDuration(duration)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            lyricsState.isLoading -> {
                LoadingScreen()
            }
            lyricsState.error != null -> {
                ErrorScreen(
                    errorMessage = lyricsState.error,
                    onRetry = {
                        lyricsViewModel.handleIntent(
                            LyricsIntent.LoadLyrics("golden-hour.ttml", "golden-hour.m4a")
                        )
                    }
                )
            }
            lyricsState.lyrics != null -> {
                LyricsContent(
                    lyricsState = lyricsState,
                    playerState = playerState,
                    settings = settingsState.settings,
                    onLineClicked = { lineIndex ->
                        lyricsViewModel.handleIntent(LyricsIntent.SeekToLine(lineIndex))
                    },
                    onPlayPauseClick = {
                        playerViewModel.handleIntent(PlayerIntent.PlayPause)
                    },
                    onSeekTo = { position ->
                        playerViewModel.handleIntent(PlayerIntent.SeekToPosition(position))
                    },
                    onSettingsClick = {
                        showSettings = true
                    }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Settings Bottom Sheet - uses MVI intents
        SettingsBottomSheet(
            isVisible = showSettings,
            settings = settingsState.settings,
            onDismiss = { showSettings = false },
            onUpdateLyricsColor = { color ->
                settingsViewModel.handleIntent(SettingsIntent.UpdateLyricsColor(color))
            },
            onUpdateBackgroundColor = { color ->
                settingsViewModel.handleIntent(SettingsIntent.UpdateBackgroundColor(color))
            },
            onUpdateFontSize = { fontSize ->
                settingsViewModel.handleIntent(SettingsIntent.UpdateFontSize(fontSize))
            },
            onUpdateAnimationsEnabled = { enabled ->
                settingsViewModel.handleIntent(SettingsIntent.UpdateAnimationsEnabled(enabled))
            },
            onUpdateBlurEffectEnabled = { enabled ->
                settingsViewModel.handleIntent(SettingsIntent.UpdateBlurEffectEnabled(enabled))
            },
            onUpdateCharacterAnimationsEnabled = { enabled ->
                settingsViewModel.handleIntent(SettingsIntent.UpdateCharacterAnimationsEnabled(enabled))
            },
            onUpdateDarkMode = { isDark ->
                settingsViewModel.handleIntent(SettingsIntent.UpdateDarkMode(isDark))
            },
            onResetToDefaults = {
                settingsViewModel.handleIntent(SettingsIntent.ResetToDefaults)
            }
        )
    }
}

@Composable
private fun LyricsContent(
    lyricsState: LyricsViewModel.LyricsState,
    playerState: PlayerViewModel.PlayerState,
    settings: com.karaokelyrics.app.domain.model.UserSettings,
    onLineClicked: (Int) -> Unit,
    onPlayPauseClick: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Use the karaoke library
        KaraokeLyricsViewNew(
            lyrics = lyricsState.lyrics,
            currentTimeMs = lyricsState.currentTimeMs,
            libraryConfig = lyricsState.libraryConfig,
            onLineClicked = { line ->
                lyricsState.lyrics?.lines?.indexOf(line)?.let { index ->
                    if (index >= 0) onLineClicked(index)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color(settings.backgroundColorArgb))
        )

        PlayerControls(
            isPlaying = playerState.isPlaying,
            position = playerState.currentPosition,
            duration = playerState.duration,
            onPlayPause = onPlayPauseClick,
            onSeek = onSeekTo,
            onOpenSettings = onSettingsClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}