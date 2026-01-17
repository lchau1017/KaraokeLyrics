package com.karaokelyrics.ui.components.viewers

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.karaokelyrics.ui.components.KaraokeSingleLine
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.rendering.AnimationManager

/**
 * Center-focused viewer that shows only the active line truly centered in the viewport.
 * Perfect for karaoke mode where focus should be on the current line only.
 */
@Composable
internal fun CenterFocusedViewer(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
) {
    val animationManager = remember { AnimationManager() }

    // Find current line
    val currentLineIndex = remember(currentTimeMs, lines) {
        animationManager.getCurrentLineIndex(lines, currentTimeMs)
    }

    // Get the current active line
    val currentLine = currentLineIndex?.let { lines.getOrNull(it) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center // True center alignment
    ) {
        // Only show the active line
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