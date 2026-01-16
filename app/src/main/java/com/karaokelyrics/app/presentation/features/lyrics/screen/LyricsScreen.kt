package com.karaokelyrics.app.presentation.features.lyrics.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.karaokelyrics.app.presentation.features.common.components.ErrorScreen
import com.karaokelyrics.app.presentation.features.common.components.LoadingScreen
import com.karaokelyrics.app.presentation.features.lyrics.components.KaraokeLyricsView
import com.karaokelyrics.app.presentation.features.lyrics.effect.LyricsEffect
import com.karaokelyrics.app.presentation.features.lyrics.viewmodel.LyricsViewModel
import com.karaokelyrics.app.presentation.features.player.components.PlayerControls
import com.karaokelyrics.app.presentation.features.player.viewmodel.PlayerViewModel
import com.karaokelyrics.app.presentation.features.settings.components.SettingsBottomSheet
import com.karaokelyrics.app.presentation.features.settings.mapper.SettingsUiMapper.backgroundColor
import com.karaokelyrics.app.presentation.features.settings.mapper.SettingsUiMapper.lyricsColor
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
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()

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

    // Load initial lyrics
    LaunchedEffect(Unit) {
        lyricsViewModel.loadLyrics("golden-hour.ttml", "golden-hour.m4a")
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
                        lyricsViewModel.loadLyrics("golden-hour.ttml", "golden-hour.m4a")
                    }
                )
            }
            lyricsState.lyrics != null -> {
                LyricsContent(
                    lyricsState = lyricsState,
                    playerState = playerState,
                    settings = settings,
                    onLineClicked = { lineIndex ->
                        lyricsViewModel.onLineClicked(lineIndex)
                    },
                    onPlayPauseClick = {
                        playerViewModel.togglePlayPause()
                    },
                    onSeekTo = { position ->
                        playerViewModel.seekTo(position)
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

        // Settings Bottom Sheet - uses SettingsViewModel
        SettingsBottomSheet(
            isVisible = showSettings,
            settings = settings,
            onDismiss = { showSettings = false },
            onUpdateLyricsColor = settingsViewModel::updateLyricsColor,
            onUpdateBackgroundColor = settingsViewModel::updateBackgroundColor,
            onUpdateFontSize = settingsViewModel::updateFontSize,
            onUpdateAnimationsEnabled = settingsViewModel::updateAnimationsEnabled,
            onUpdateBlurEffectEnabled = settingsViewModel::updateBlurEffectEnabled,
            onUpdateCharacterAnimationsEnabled = settingsViewModel::updateCharacterAnimationsEnabled,
            onUpdateDarkMode = settingsViewModel::updateDarkMode,
            onResetToDefaults = settingsViewModel::resetToDefaults
        )
    }
}

@Composable
private fun LyricsContent(
    lyricsState: LyricsViewModel.LyricsDisplayState,
    playerState: PlayerViewModel.PlayerState,
    settings: com.karaokelyrics.app.domain.model.UserSettings,
    onLineClicked: (Int) -> Unit,
    onPlayPauseClick: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        lyricsState.lyrics?.let { lyricsData ->
            KaraokeLyricsView(
                lyrics = lyricsData,
                currentPosition = { playerState.currentPosition },
                onLineClicked = { line ->
                    val index = lyricsData.lines.indexOf(line)
                    if (index >= 0) onLineClicked(index)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(settings.backgroundColor),
                normalLineTextStyle = LocalTextStyle.current.copy(
                    fontSize = settings.fontSize.sp.sp,
                    fontWeight = FontWeight.Bold,
                    textMotion = TextMotion.Animated
                ),
                accompanimentLineTextStyle = LocalTextStyle.current.copy(
                    fontSize = (settings.fontSize.sp * 0.6f).sp,
                    fontWeight = FontWeight.Bold,
                    textMotion = TextMotion.Animated
                ),
                textColor = settings.lyricsColor,
                useBlurEffect = settings.enableBlurEffect && settings.enableAnimations,
                enableCharacterAnimations = settings.enableCharacterAnimations && settings.enableAnimations
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
}