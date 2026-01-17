package com.karaokelyrics.ui.components.viewers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.karaokelyrics.ui.components.KaraokeSingleLine
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.rendering.AnimationManager
import kotlinx.coroutines.launch

/**
 * Smooth scrolling viewer that positions the active line at a comfortable reading position.
 * Shows multiple lines for context, ideal for following along with lyrics.
 */
@Composable
internal fun SmoothScrollViewer(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
) {
    val animationManager = remember { AnimationManager() }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Find current line index
    val currentLineIndex = remember(currentTimeMs, lines) {
        animationManager.getCurrentLineIndex(lines, currentTimeMs)
    }

    // Track previous index to detect changes
    var previousIndex by remember { mutableStateOf<Int?>(null) }

    // Handle initial positioning
    var isInitialized by remember { mutableStateOf(false) }
    LaunchedEffect(lines) {
        if (lines.isNotEmpty() && !isInitialized) {
            isInitialized = true
            // Start at the beginning
            listState.scrollToItem(0)
        }
    }

    // Smooth scroll when line changes
    LaunchedEffect(currentLineIndex) {
        if (currentLineIndex != null && currentLineIndex != previousIndex) {
            previousIndex = currentLineIndex
            coroutineScope.launch {
                listState.animateScrollToItem(
                    index = currentLineIndex,
                    scrollOffset = 0 // Adjusted by contentPadding
                )
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenHeight = maxHeight
        val scrollPosition = screenHeight * config.layout.viewerConfig.scrollPosition

        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                top = scrollPosition, // Position active line at reading position
                bottom = screenHeight * 0.7f // Allow scrolling last line up
            ),
            verticalArrangement = Arrangement.spacedBy(config.layout.lineSpacing)
        ) {
            itemsIndexed(
                items = lines,
                key = { index, line -> "$index-${line.start}" }
            ) { index, line ->
                val lineState = remember(line, currentTimeMs) {
                    animationManager.getLineState(line, currentTimeMs)
                }

                // Apply different opacity based on line state
                val opacity = when {
                    lineState.isPlaying -> 1f
                    lineState.hasPlayed -> 0.6f
                    lineState.isUpcoming -> 0.4f
                    else -> 0.4f
                }

                Box(modifier = Modifier.alpha(opacity)) {
                    KaraokeSingleLine(
                        line = line,
                        currentTimeMs = currentTimeMs,
                        config = config,
                        onLineClick = onLineClick?.let { { it(line, index) } }
                    )
                }
            }
        }
    }
}