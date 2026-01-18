package com.kyrics.components.viewers

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.kyrics.components.KyricsSingleLine
import com.kyrics.config.KyricsConfig
import com.kyrics.models.ISyncedLine
import com.kyrics.state.KyricsUiState

/**
 * Split dual viewer showing current and next line simultaneously.
 * Perfect for duets or learning apps.
 */
@Composable
internal fun SplitDualViewer(uiState: KyricsUiState, config: KyricsConfig, onLineClick: ((ISyncedLine, Int) -> Unit)? = null) {
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
                    KyricsSingleLine(
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
                    KyricsSingleLine(
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
