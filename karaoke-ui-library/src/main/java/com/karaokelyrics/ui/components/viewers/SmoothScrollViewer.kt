package com.karaokelyrics.ui.components.viewers

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.karaokelyrics.ui.components.KaraokeSingleLine
import com.karaokelyrics.ui.components.KaraokeSingleLineStateless
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.rendering.AnimationManager
import com.karaokelyrics.ui.state.KaraokeUiState
import kotlinx.coroutines.launch

/**
 * Smooth scrolling viewer that positions the active line at a comfortable reading position.
 * Shows multiple lines for context, ideal for following along with lyrics.
 *
 * This version uses pre-calculated KaraokeUiState for better performance.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun SmoothScrollViewer(
    uiState: KaraokeUiState,
    config: KaraokeLibraryConfig,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Track previous index to detect changes
    var previousIndex by remember { mutableStateOf<Int?>(null) }

    // Handle initial positioning
    var isInitialized by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.lines) {
        if (uiState.lines.isNotEmpty() && !isInitialized) {
            isInitialized = true
            listState.scrollToItem(0)
        }
    }

    // Smooth scroll when line changes
    LaunchedEffect(uiState.currentLineIndex) {
        val currentIndex = uiState.currentLineIndex
        if (currentIndex != null && currentIndex != previousIndex) {
            previousIndex = currentIndex
            coroutineScope.launch {
                listState.animateScrollToItem(
                    index = currentIndex,
                    scrollOffset = 0
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
                top = scrollPosition,
                bottom = screenHeight * 0.7f
            ),
            verticalArrangement = Arrangement.spacedBy(config.layout.lineSpacing)
        ) {
            itemsIndexed(
                items = uiState.lines,
                key = { index, line -> "$index-${line.start}" }
            ) { index, line ->
                val lineUiState = uiState.getLineState(index)

                KaraokeSingleLineStateless(
                    line = line,
                    lineUiState = lineUiState,
                    currentTimeMs = uiState.currentTimeMs,
                    config = config,
                    onLineClick = onLineClick?.let { callback ->
                        { callback(line, index) }
                    }
                )
            }
        }
    }
}

/**
 * Legacy version for backward compatibility.
 * This version calculates state internally.
 */
@Composable
internal fun SmoothScrollViewerLegacy(
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
                    scrollOffset = 0
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
                top = scrollPosition,
                bottom = screenHeight * 0.7f
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
