package com.karaokelyrics.ui.components.viewers

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.karaokelyrics.ui.components.KaraokeSingleLine
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.utils.LineStateUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Paged viewer that groups lines and shows them page by page.
 * Lines are grouped by timing gaps, showing all lines in a group at once.
 */
@Composable
internal fun PagedViewer(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
) {
    // Group lines into pages based on timing gaps
    val pages = remember(lines) {
        groupLinesIntoPages(lines)
    }

    // Find current page based on current time
    val currentPageIndex = remember(currentTimeMs, pages) {
        pages.indexOfFirst { page ->
            page.any { line ->
                val lineState = LineStateUtils.getLineState(line, currentTimeMs)
                lineState.isPlaying || lineState.isUpcoming
            }
        }.takeIf { it >= 0 } ?: pages.lastIndex
    }

    // Track previous page for auto-advance
    var previousPageIndex by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    // Auto-advance pages when configured
    LaunchedEffect(currentPageIndex) {
        if (config.layout.viewerConfig.autoAdvancePages &&
            currentPageIndex != previousPageIndex &&
            currentPageIndex > previousPageIndex) {
            previousPageIndex = currentPageIndex
            // Add a small delay before transitioning
            coroutineScope.launch {
                delay(config.layout.viewerConfig.pageTransitionDelay.toLong())
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = currentPageIndex,
            transitionSpec = {
                if (targetState > initialState) {
                    // Moving forward
                    (slideInHorizontally { width -> width } + fadeIn(tween(500)))
                        .togetherWith(slideOutHorizontally { width -> -width } + fadeOut(tween(500)))
                } else {
                    // Moving backward
                    (slideInHorizontally { width -> -width } + fadeIn(tween(500)))
                        .togetherWith(slideOutHorizontally { width -> width } + fadeOut(tween(500)))
                }
            },
            label = "PageTransition"
        ) { pageIndex ->
            val currentPage = pages.getOrNull(pageIndex) ?: emptyList()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    config.layout.lineSpacing,
                    Alignment.CenterVertically
                )
            ) {
                currentPage.forEachIndexed { indexInPage, line ->
                    val globalIndex = lines.indexOf(line)
                    val lineState = remember(line, currentTimeMs) {
                        LineStateUtils.getLineState(line, currentTimeMs)
                    }

                    // Apply opacity based on line state
                    val opacity = when {
                        lineState.isPlaying -> 1f
                        lineState.hasPlayed -> 0.5f
                        lineState.isUpcoming -> 0.7f
                        else -> 0.4f
                    }

                    Box(modifier = Modifier.alpha(opacity)) {
                        KaraokeSingleLine(
                            line = line,
                            currentTimeMs = currentTimeMs,
                            config = config,
                            onLineClick = onLineClick?.let { { it(line, globalIndex) } }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Groups lines into pages based on timing gaps.
 * A new page starts when there's a significant gap between lines.
 */
private fun groupLinesIntoPages(
    lines: List<ISyncedLine>,
    gapThresholdMs: Int = 3000 // 3 seconds gap = new page
): List<List<ISyncedLine>> {
    if (lines.isEmpty()) return emptyList()

    val pages = mutableListOf<MutableList<ISyncedLine>>()
    var currentPage = mutableListOf<ISyncedLine>()

    lines.forEachIndexed { index, line ->
        if (index == 0) {
            currentPage.add(line)
        } else {
            val previousLine = lines[index - 1]
            val gap = line.start - previousLine.end

            if (gap > gapThresholdMs) {
                // Start a new page
                pages.add(currentPage)
                currentPage = mutableListOf(line)
            } else {
                currentPage.add(line)
            }
        }
    }

    // Add the last page
    if (currentPage.isNotEmpty()) {
        pages.add(currentPage)
    }

    return pages
}