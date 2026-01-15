package com.karaokelyrics.app.presentation.ui.screen

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
import com.karaokelyrics.app.presentation.effect.LyricsEffect
import com.karaokelyrics.app.presentation.intent.LyricsIntent
import com.karaokelyrics.app.presentation.ui.components.*
import com.karaokelyrics.app.presentation.viewmodel.LyricsViewModel
import com.karaokelyrics.app.presentation.state.LyricsUiState
import com.karaokelyrics.app.presentation.ui.manager.LyricsLayoutManager
import dagger.hilt.android.EntryPointAccessors
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LyricsScreen(
    viewModel: LyricsViewModel = hiltViewModel(),
    layoutManager: LyricsLayoutManager
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
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
                LoadingScreen()
            }
            state.error != null -> {
                ErrorScreen(
                    errorMessage = state.error,
                    onRetry = {
                        viewModel.processIntent(LyricsIntent.LoadInitialLyrics)
                    }
                )
            }
            state.lyrics != null -> {
                LyricsContent(
                    state = state,
                    onLineClicked = { line ->
                        state.lyrics?.let { lyricsData ->
                            val index = lyricsData.lines.indexOf(line)
                            if (index >= 0) {
                                viewModel.processIntent(LyricsIntent.SeekToLine(index))
                            }
                        }
                    },
                    onPlayPause = {
                        viewModel.processIntent(LyricsIntent.PlayPause)
                    },
                    onSeek = { position ->
                        viewModel.processIntent(LyricsIntent.SeekToPosition(position))
                    },
                    onOpenSettings = {
                        showSettings = true
                    },
                    layoutManager = layoutManager // Pass the layout manager
                )
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
private fun LyricsContent(
    state: LyricsUiState,
    onLineClicked: (com.karaokelyrics.app.domain.model.ISyncedLine) -> Unit,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    layoutManager: LyricsLayoutManager
) {
    state.lyrics?.let { lyricsData ->
        Box(modifier = Modifier.fillMaxSize()) {
            KaraokeLyricsView(
                lyrics = lyricsData,
                currentPosition = { state.playbackPosition },
                onLineClicked = onLineClicked,
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
                enableCharacterAnimations = state.userSettings.enableCharacterAnimations && state.userSettings.enableAnimations,
                layoutManager = layoutManager // Pass the Clean Architecture manager for testing
            )

            PlayerControls(
                isPlaying = state.isPlaying,
                position = state.playbackPosition,
                duration = lyricsData.lines.lastOrNull()?.end?.toLong() ?: 0L,
                onPlayPause = onPlayPause,
                onSeek = onSeek,
                onOpenSettings = onOpenSettings,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}