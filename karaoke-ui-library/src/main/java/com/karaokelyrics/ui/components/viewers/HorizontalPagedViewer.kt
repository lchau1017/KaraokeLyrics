package com.karaokelyrics.ui.components.viewers

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.karaokelyrics.ui.components.KaraokeSingleLine
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.state.KaraokeUiState

/**
 * Horizontal paged viewer that shows one line at a time with horizontal swipe transitions.
 * Each line appears as a full page with consistent left-to-right flow.
 */
@Composable
internal fun HorizontalPagedViewer(
    uiState: KaraokeUiState,
    config: KaraokeLibraryConfig,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null
) {
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
                (slideInHorizontally(
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                ) { fullWidth -> fullWidth } + fadeIn(
                    animationSpec = tween(300)
                )).togetherWith(
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
                    KaraokeSingleLine(
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
