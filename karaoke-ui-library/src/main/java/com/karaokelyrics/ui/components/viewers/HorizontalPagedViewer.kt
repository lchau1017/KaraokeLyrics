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
import com.karaokelyrics.ui.utils.LineStateUtils

/**
 * Horizontal paged viewer that shows one line at a time with horizontal swipe transitions.
 * Each line appears as a full page with consistent left-to-right flow.
 */
@Composable
internal fun HorizontalPagedViewer(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
) {
    // Find current line index
    val currentLineIndex = remember(currentTimeMs, lines) {
        LineStateUtils.getCurrentLineIndex(lines, currentTimeMs)
    } ?: 0

    // Track if we've shown this line before to maintain consistent direction
    var lastShownIndex by remember { mutableStateOf(-1) }

    // Determine if this is a new line (always moving forward in time)
    val isNewLine = currentLineIndex > lastShownIndex

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
                // Always slide in from right, slide out to left for consistent flow
                // This creates a natural reading direction regardless of index changes
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
            val line = lines.getOrNull(lineIndex)

            if (line != null) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    KaraokeSingleLine(
                        line = line,
                        currentTimeMs = currentTimeMs,
                        config = config,
                        onLineClick = onLineClick?.let { { it(line, lineIndex) } }
                    )
                }
            } else {
                // Empty state when no line
                Spacer(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}