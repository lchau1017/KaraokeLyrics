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
 * Horizontal paged viewer that shows one line at a time with horizontal swipe transitions.
 * Each line appears as a full page with consistent left-to-right flow.
 */
@Composable
internal fun HorizontalPagedViewer(uiState: KyricsUiState, config: KyricsConfig, onLineClick: ((ISyncedLine, Int) -> Unit)? = null) {
    val currentLineIndex = uiState.currentLineIndex ?: 0

    // Track if we've shown this line before to maintain consistent direction
    var lastShownIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(currentLineIndex) {
        lastShownIndex = currentLineIndex
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = currentLineIndex,
            transitionSpec = {
                (
                    slideInHorizontally(
                        animationSpec = tween(500, easing = FastOutSlowInEasing)
                    ) { fullWidth -> fullWidth } + fadeIn(
                        animationSpec = tween(300)
                    )
                    ).togetherWith(
                    slideOutHorizontally(
                        animationSpec = tween(500, easing = FastOutSlowInEasing)
                    ) { fullWidth -> -fullWidth } + fadeOut(
                        animationSpec = tween(300)
                    )
                )
            },
            label = "HorizontalPageTransition"
        ) { lineIndex ->
            val line = uiState.lines.getOrNull(lineIndex)
            val lineUiState = uiState.getLineState(lineIndex)

            if (line != null) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    KyricsSingleLine(
                        line = line,
                        lineUiState = lineUiState,
                        currentTimeMs = uiState.currentTimeMs,
                        config = config,
                        onLineClick = onLineClick?.let { { it(line, lineIndex) } }
                    )
                }
            } else {
                Spacer(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
