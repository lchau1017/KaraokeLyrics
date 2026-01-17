package com.karaokelyrics.ui.components.viewers

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.karaokelyrics.ui.components.KaraokeSingleLine
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.state.KaraokeUiState
import com.karaokelyrics.ui.state.LineUiState

/**
 * Split dual viewer showing current and next line simultaneously.
 * Perfect for duets or learning apps.
 */
@Composable
internal fun SplitDualViewer(
    uiState: KaraokeUiState,
    config: KaraokeLibraryConfig,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
) {
    val currentLineIndex = uiState.currentLineIndex ?: 0
    val currentLine = uiState.lines.getOrNull(currentLineIndex)
    val nextLine = uiState.lines.getOrNull(currentLineIndex + 1)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Top half - Current line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            currentLine?.let { line ->
                val lineUiState = uiState.getLineState(currentLineIndex)
                val alpha by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(300),
                    label = "currentAlpha"
                )
                Box(modifier = Modifier.alpha(alpha)) {
                    KaraokeSingleLine(
                        line = line,
                        lineUiState = lineUiState,
                        currentTimeMs = uiState.currentTimeMs,
                        config = config,
                        onLineClick = onLineClick?.let { { it(line, currentLineIndex) } }
                    )
                }
            }
        }

        // Divider
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .alpha(0.3f)
        )

        // Bottom half - Next line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            nextLine?.let { line ->
                val nextLineIndex = currentLineIndex + 1
                val lineUiState = uiState.getLineState(nextLineIndex)
                val alpha by animateFloatAsState(
                    targetValue = 0.5f,
                    animationSpec = tween(300),
                    label = "nextAlpha"
                )
                Box(modifier = Modifier.alpha(alpha)) {
                    KaraokeSingleLine(
                        line = line,
                        lineUiState = lineUiState,
                        currentTimeMs = uiState.currentTimeMs,
                        config = config,
                        onLineClick = onLineClick?.let { { it(line, nextLineIndex) } }
                    )
                }
            }
        }
    }
}
