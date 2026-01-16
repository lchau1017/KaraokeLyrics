package com.karaokelyrics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import com.karaokelyrics.ui.core.config.BehaviorConfig
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.config.ScrollBehavior
import com.karaokelyrics.ui.core.models.ISyncedLine
import kotlinx.coroutines.launch

/**
 * Composable for displaying multiple karaoke lines with automatic scrolling and synchronization.
 *
 * @param lines List of synchronized lines to display
 * @param currentTimeMs Current playback time in milliseconds
 * @param config Complete configuration for visual, animation, and behavior
 * @param modifier Modifier for the composable
 * @param onLineClick Optional callback when a line is clicked
 * @param onLineLongPress Optional callback when a line is long-pressed
 */
@Composable
fun KaraokeLyricsDisplay(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig = KaraokeLibraryConfig.Default,
    modifier: Modifier = Modifier,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Find the current playing line index
    val currentLineIndex = remember(currentTimeMs, lines) {
        lines.indexOfFirst { line ->
            currentTimeMs in line.start..line.end
        }.takeIf { it != -1 }
    }

    // Track the previous line index to detect changes
    var previousLineIndex by remember { mutableStateOf<Int?>(null) }

    // Handle automatic scrolling
    LaunchedEffect(currentLineIndex) {
        if (currentLineIndex != null &&
            currentLineIndex != previousLineIndex &&
            config.behavior.scrollBehavior != ScrollBehavior.NONE) {

            previousLineIndex = currentLineIndex

            val targetIndex = when (config.behavior.scrollBehavior) {
                ScrollBehavior.SMOOTH_CENTER -> {
                    // Calculate center position considering visible items
                    val visibleItemsCount = listState.layoutInfo.visibleItemsInfo.size.takeIf { it > 0 } ?: 5
                    (currentLineIndex - visibleItemsCount / 2).coerceAtLeast(0)
                }
                ScrollBehavior.SMOOTH_TOP -> {
                    // Scroll to make current line appear at top with some offset
                    currentLineIndex.coerceAtLeast(0)
                }
                ScrollBehavior.INSTANT_CENTER -> {
                    val visibleItemsCount = listState.layoutInfo.visibleItemsInfo.size.takeIf { it > 0 } ?: 5
                    (currentLineIndex - visibleItemsCount / 2).coerceAtLeast(0)
                }
                ScrollBehavior.PAGED -> currentLineIndex
                else -> currentLineIndex
            }

            when (config.behavior.scrollBehavior) {
                ScrollBehavior.SMOOTH_CENTER, ScrollBehavior.SMOOTH_TOP -> {
                    coroutineScope.launch {
                        listState.animateScrollToItem(
                            index = targetIndex,
                            scrollOffset = 0 // Let contentPadding handle the offset
                        )
                    }
                }
                ScrollBehavior.INSTANT_CENTER -> {
                    coroutineScope.launch {
                        listState.scrollToItem(
                            index = targetIndex,
                            scrollOffset = 0
                        )
                    }
                }
                ScrollBehavior.PAGED -> {
                    // Page-like scrolling with snap behavior
                    coroutineScope.launch {
                        listState.animateScrollToItem(
                            index = currentLineIndex,
                            scrollOffset = 0
                        )
                    }
                }
                else -> {}
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(config.visual.backgroundColor)
            .padding(config.layout.containerPadding)
    ) {
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(config.layout.lineSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                top = config.behavior.scrollOffset,
                bottom = 200.dp
            ) // Space at top for scroll offset and bottom for last lines
        ) {
            itemsIndexed(
                items = lines,
                key = { index, line -> "$index-${line.start}" }
            ) { index, line ->
                val distance = currentLineIndex?.let { kotlin.math.abs(index - it) } ?: 999

                KaraokeLineDisplayWithDistance(
                    line = line,
                    currentTimeMs = currentTimeMs,
                    distance = distance,
                    config = config,
                    onLineClick = if (onLineClick != null) {
                        { onLineClick(line, index) }
                    } else null,
                    onLineLongPress = if (onLineLongPress != null) {
                        { onLineLongPress(line, index) }
                    } else null
                )
            }
        }
    }
}

/**
 * Internal wrapper that includes distance-based effects.
 */
@Composable
private fun KaraokeLineDisplayWithDistance(
    line: ISyncedLine,
    currentTimeMs: Int,
    distance: Int,
    config: KaraokeLibraryConfig,
    onLineClick: ((ISyncedLine) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine) -> Unit)? = null
) {
    // Apply distance-based opacity reduction (more subtle)
    val distanceOpacityModifier = when {
        distance <= 1 -> 1f      // Current and next line - full visibility
        distance == 2 -> 0.9f    // Very slight reduction
        distance == 3 -> 0.8f    // Light reduction
        distance <= 5 -> 0.7f    // Moderate reduction
        else -> 0.6f             // Distant lines still readable
    }

    // Determine if line is played, playing, or upcoming
    val isPlaying = currentTimeMs in line.start..line.end
    val hasPlayed = currentTimeMs > line.end

    // Apply distance-based blur ONLY to upcoming/unplayed lines
    val distanceBlurRadius = if (config.effects.enableBlur && !isPlaying && !hasPlayed) {
        when (distance) {
            0 -> 0.dp  // Very next line - no blur for better readability
            1 -> config.effects.upcomingLineBlur * 0.5f  // Light blur for next line
            2 -> config.effects.upcomingLineBlur  // Medium blur
            3, 4 -> config.effects.upcomingLineBlur * 1.5f  // Stronger blur
            else -> config.effects.distantLineBlur  // Maximum blur for distant
        }
    } else {
        0.dp  // No blur for playing or played lines
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(distanceOpacityModifier)
            .then(
                if (distanceBlurRadius > 0.dp) {
                    Modifier.blur(radius = distanceBlurRadius)
                } else {
                    Modifier
                }
            )
    ) {
        KaraokeLineDisplay(
            line = line,
            currentTimeMs = currentTimeMs,
            config = config,
            onLineClick = onLineClick
        )
    }
}