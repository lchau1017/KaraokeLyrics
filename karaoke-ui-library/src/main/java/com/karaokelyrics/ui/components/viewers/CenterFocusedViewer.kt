package com.karaokelyrics.ui.components.viewers

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.karaokelyrics.ui.components.KaraokeSingleLine
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.state.KaraokeUiState
import com.karaokelyrics.ui.state.LineUiState

/**
 * Center-focused viewer that shows only the active line truly centered in the viewport.
 * Perfect for karaoke mode where focus should be on the current line only.
 */
@Composable
internal fun CenterFocusedViewer(
    uiState: KaraokeUiState,
    config: KaraokeLibraryConfig,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
) {
    val currentLineIndex = uiState.currentLineIndex
    val currentLine = uiState.currentLine

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        currentLine?.let { line ->
            val lineUiState = currentLineIndex?.let { uiState.getLineState(it) } ?: LineUiState.Playing

            KaraokeSingleLine(
                line = line,
                lineUiState = lineUiState,
                currentTimeMs = uiState.currentTimeMs,
                config = config,
                onLineClick = currentLineIndex?.let { index ->
                    onLineClick?.let { callback -> { callback(line, index) } }
                }
            )
        }
    }
}
