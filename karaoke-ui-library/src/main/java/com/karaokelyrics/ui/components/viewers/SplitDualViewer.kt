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
import com.karaokelyrics.ui.utils.LineStateUtils

/**
 * Split dual viewer showing current and next line simultaneously.
 * Perfect for duets or learning apps.
 */
@Composable
internal fun SplitDualViewer(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
) {
    val currentLineIndex = remember(currentTimeMs, lines) {
        LineStateUtils.getCurrentLineIndex(lines, currentTimeMs)
    } ?: 0

    val currentLine = lines.getOrNull(currentLineIndex)
    val nextLine = lines.getOrNull(currentLineIndex + 1)

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
                val alpha by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(300),
                    label = "currentAlpha"
                )
                Box(modifier = Modifier.alpha(alpha)) {
                    KaraokeSingleLine(
                        line = line,
                        currentTimeMs = currentTimeMs,
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
                val alpha by animateFloatAsState(
                    targetValue = 0.5f,
                    animationSpec = tween(300),
                    label = "nextAlpha"
                )
                Box(modifier = Modifier.alpha(alpha)) {
                    KaraokeSingleLine(
                        line = line,
                        currentTimeMs = currentTimeMs,
                        config = config,
                        onLineClick = onLineClick?.let { { it(line, currentLineIndex + 1) } }
                    )
                }
            }
        }
    }
}