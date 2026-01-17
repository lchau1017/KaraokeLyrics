package com.karaokelyrics.ui.components.viewers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.karaokelyrics.ui.components.KaraokeSingleLine
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.utils.LineStateUtils
import kotlinx.coroutines.launch

/**
 * Center-focused viewer that keeps the active line centered in the viewport.
 * Perfect for karaoke mode where focus should be on the current line.
 */
@Composable
internal fun CenterFocusedViewer(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Find current line index
    val currentLineIndex = remember(currentTimeMs, lines) {
        LineStateUtils.getCurrentLineIndex(lines, currentTimeMs)
    }

    // Track previous index to detect changes
    var previousIndex by remember { mutableStateOf<Int?>(null) }

    // Scroll to center when line changes
    LaunchedEffect(currentLineIndex) {
        if (currentLineIndex != null && currentLineIndex != previousIndex) {
            previousIndex = currentLineIndex
            coroutineScope.launch {
                // Calculate center position
                listState.animateScrollToItem(
                    index = currentLineIndex,
                    scrollOffset = 0 // Will be adjusted by contentPadding
                )
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenHeight = maxHeight
        val centerOffset = screenHeight * config.layout.viewerConfig.centerOffset

        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                top = centerOffset, // Position active line at center
                bottom = screenHeight - centerOffset // Balance the padding
            ),
            verticalArrangement = Arrangement.spacedBy(config.layout.lineSpacing)
        ) {
            itemsIndexed(
                items = lines,
                key = { index, line -> "$index-${line.start}" }
            ) { index, line ->
                val lineState = remember(line, currentTimeMs) {
                    LineStateUtils.getLineState(line, currentTimeMs)
                }

                // Calculate visibility based on distance from current
                val distance = currentLineIndex?.let {
                    kotlin.math.abs(index - it)
                } ?: 999

                val shouldShow = when {
                    lineState.isPlaying -> true
                    lineState.hasPlayed && distance <= config.layout.viewerConfig.visibleLinesBefore -> true
                    lineState.isUpcoming && distance <= config.layout.viewerConfig.visibleLinesAfter -> true
                    else -> false
                }

                if (shouldShow) {
                    // Apply opacity based on distance
                    val opacity = when (distance) {
                        0 -> 1f
                        1 -> 0.7f
                        2 -> 0.4f
                        else -> 0.2f
                    }

                    Box(modifier = Modifier.alpha(opacity)) {
                        KaraokeSingleLine(
                            line = line,
                            currentTimeMs = currentTimeMs,
                            config = config,
                            onLineClick = onLineClick?.let { { it(line, index) } }
                        )
                    }
                } else {
                    // Empty space for hidden lines
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}