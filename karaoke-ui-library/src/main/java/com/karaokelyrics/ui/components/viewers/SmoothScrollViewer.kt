package com.karaokelyrics.ui.components.viewers

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.karaokelyrics.ui.components.KaraokeSingleLine
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.state.KaraokeUiState
import kotlinx.coroutines.launch

/**
 * Smooth scrolling viewer that positions the active line at a comfortable reading position.
 * Shows multiple lines for context, ideal for following along with lyrics.
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

                KaraokeSingleLine(
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
