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
import com.karaokelyrics.ui.rendering.AnimationManager

/**
 * Fade through viewer with pure opacity transitions.
 * Minimalist approach with no movement, just fades.
 */
@Composable
internal fun FadeThroughViewer(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
) {
    val animationManager = remember { AnimationManager() }

    val currentLineIndex = remember(currentTimeMs, lines) {
        animationManager.getCurrentLineIndex(lines, currentTimeMs)
    } ?: 0

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
            val line = lines.getOrNull(lineIndex)

            if (line != null) {
                KaraokeSingleLine(
                    line = line,
                    currentTimeMs = currentTimeMs,
                    config = config,
                    onLineClick = onLineClick?.let { { it(line, lineIndex) } }
                )
            } else {
                Spacer(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}