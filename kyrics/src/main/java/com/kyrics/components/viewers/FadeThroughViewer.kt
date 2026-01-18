package com.kyrics.components.viewers

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kyrics.components.KyricsSingleLine
import com.kyrics.config.KyricsConfig
import com.kyrics.models.ISyncedLine
import com.kyrics.state.KyricsUiState

/**
 * Fade through viewer with pure opacity transitions.
 * Minimalist approach with no movement, just fades.
 */
@Composable
internal fun FadeThroughViewer(uiState: KyricsUiState, config: KyricsConfig, onLineClick: ((ISyncedLine, Int) -> Unit)? = null) {
    val currentLineIndex = uiState.currentLineIndex ?: 0

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = currentLineIndex,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    )
                ) togetherWith fadeOut(
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    )
                )
            },
            label = "FadeThroughTransition"
        ) { lineIndex ->
            val line = uiState.lines.getOrNull(lineIndex)
            val lineUiState = uiState.getLineState(lineIndex)

            if (line != null) {
                KyricsSingleLine(
                    line = line,
                    lineUiState = lineUiState,
                    currentTimeMs = uiState.currentTimeMs,
                    config = config,
                    onLineClick = onLineClick?.let { { it(line, lineIndex) } }
                )
            } else {
                Spacer(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
