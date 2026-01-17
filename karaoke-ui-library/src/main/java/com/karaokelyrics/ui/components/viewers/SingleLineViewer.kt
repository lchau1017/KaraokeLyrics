package com.karaokelyrics.ui.components.viewers

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.karaokelyrics.ui.components.KaraokeSingleLine
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.utils.LineStateUtils

/**
 * Single line viewer that shows only the currently active line.
 * Perfect for minimal UI, widgets, or single-line displays.
 */
@Composable
internal fun SingleLineViewer(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
) {
    // Find current line
    val currentLineIndex = remember(currentTimeMs, lines) {
        LineStateUtils.getCurrentLineIndex(lines, currentTimeMs)
    }

    val currentLine = currentLineIndex?.let { lines.getOrNull(it) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (config.layout.viewerConfig.transitionAnimation) {
            // Animated transition between lines
            AnimatedContent(
                targetState = currentLine,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(300)) +
                     slideInVertically(animationSpec = tween(300)) { height -> height })
                        .togetherWith(
                            fadeOut(animationSpec = tween(300)) +
                            slideOutVertically(animationSpec = tween(300)) { height -> -height }
                        )
                },
                label = "SingleLineTransition"
            ) { line ->
                if (line != null) {
                    KaraokeSingleLine(
                        line = line,
                        currentTimeMs = currentTimeMs,
                        config = config,
                        onLineClick = currentLineIndex?.let { index ->
                            onLineClick?.let { { it(line, index) } }
                        }
                    )
                } else {
                    // Show nothing when no active line
                    Spacer(modifier = Modifier.fillMaxWidth())
                }
            }
        } else {
            // No animation, direct display
            currentLine?.let { line ->
                KaraokeSingleLine(
                    line = line,
                    currentTimeMs = currentTimeMs,
                    config = config,
                    onLineClick = currentLineIndex?.let { index ->
                        onLineClick?.let { { it(line, index) } }
                    }
                )
            }
        }
    }
}