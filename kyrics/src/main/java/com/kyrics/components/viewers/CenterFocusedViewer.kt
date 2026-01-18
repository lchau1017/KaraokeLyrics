package com.kyrics.components.viewers

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kyrics.components.KyricsSingleLine
import com.kyrics.config.KyricsConfig
import com.kyrics.models.ISyncedLine
import com.kyrics.state.KyricsUiState
import com.kyrics.state.LineUiState

/**
 * Center-focused viewer that shows only the active line truly centered in the viewport.
 * Perfect for karaoke mode where focus should be on the current line only.
 */
@Composable
internal fun CenterFocusedViewer(uiState: KyricsUiState, config: KyricsConfig, onLineClick: ((ISyncedLine, Int) -> Unit)? = null) {
    val currentLineIndex = uiState.currentLineIndex
    val currentLine = uiState.currentLine

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        currentLine?.let { line ->
            val lineUiState = currentLineIndex?.let { uiState.getLineState(it) } ?: LineUiState.Playing

            KyricsSingleLine(
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
