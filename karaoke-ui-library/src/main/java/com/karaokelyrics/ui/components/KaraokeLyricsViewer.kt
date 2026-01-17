package com.karaokelyrics.ui.components

import android.annotation.SuppressLint
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.utils.LineStateUtils
import kotlinx.coroutines.launch

/**
 * Complete karaoke lyrics viewer with automatic scrolling and synchronization.
 * This container manages the entire lyrics display experience, including:
 * - Auto-scrolling to keep current line in view
 * - Distance-based visual effects (blur, opacity)
 * - Intelligent spacing between line groups
 * - Smooth transitions and animations
 *
 * @param lines List of synchronized lines to display
 * @param currentTimeMs Current playback time in milliseconds
 * @param config Complete configuration for visual, animation, and behavior
 * @param modifier Modifier for the composable
 * @param onLineClick Optional callback when a line is clicked
 * @param onLineLongPress Optional callback when a line is long-pressed
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun KaraokeLyricsViewer(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig = KaraokeLibraryConfig.Default,
    modifier: Modifier = Modifier,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    onLineLongPress: ((ISyncedLine, Int) -> Unit)? = null
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Find the current playing line index (for distance calculation)
    val currentLineIndex = remember(currentTimeMs, lines) {
        LineStateUtils.getCurrentLineIndex(lines, currentTimeMs)
    }

    // Find the next upcoming line when no active line
    val nextUpcomingIndex = remember(currentTimeMs, lines) {
        if (currentLineIndex == null) {
            // Find first line that hasn't started yet
            lines.indexOfFirst { line -> currentTimeMs < line.start }.takeIf { it >= 0 }
        } else null
    }

    // Track previous indices to detect changes
    var previousCurrentIndex by remember { mutableStateOf<Int?>(null) }
    var previousUpcomingIndex by remember { mutableStateOf<Int?>(null) }

    // Handle initial positioning for first line
    var isInitialized by remember { mutableStateOf(false) }
    LaunchedEffect(lines) {
        if (lines.isNotEmpty() && !isInitialized) {
            isInitialized = true
            // If first line is about to play or playing, position it like other lines
            if (currentTimeMs >= lines[0].start - 1000) {
                listState.scrollToItem(
                    index = 0,
                    scrollOffset = 0  // Position first line at top with padding
                )
            }
        }
    }

    // Handle automatic scrolling based on line changes
    LaunchedEffect(currentLineIndex, nextUpcomingIndex) {
        when {
            // Active line exists - scroll to it
            currentLineIndex != null && currentLineIndex != previousCurrentIndex -> {
                previousCurrentIndex = currentLineIndex
                previousUpcomingIndex = null

                coroutineScope.launch {
                    // Always use same positioning for consistency
                    listState.animateScrollToItem(
                        index = currentLineIndex,
                        scrollOffset = 0  // Position at top (contentPadding handles the offset)
                    )
                }
            }

            // No active line but there's an upcoming line - scroll to show upcoming
            currentLineIndex == null && nextUpcomingIndex != null && nextUpcomingIndex != previousUpcomingIndex -> {
                previousUpcomingIndex = nextUpcomingIndex
                previousCurrentIndex = null

                coroutineScope.launch {
                    // Scroll to upcoming line to move played lines out
                    listState.animateScrollToItem(
                        index = nextUpcomingIndex,
                        scrollOffset = 0  // Upcoming at top, played lines scroll out
                    )
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(config.visual.backgroundColor)
            .padding(config.layout.containerPadding)
    ) {
        val screenHeight = maxHeight

        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                top = config.layout.scrollTopOffset,  // Use scrollTopOffset for consistent top spacing
                bottom = screenHeight * config.layout.contentBottomPaddingRatio // Configurable bottom padding ratio
            )
        ) {
            itemsIndexed(
                items = lines,
                key = { index, line -> "$index-${line.start}" }
            ) { index, line ->
                // Check line states using utility
                val lineState = LineStateUtils.getLineState(line, currentTimeMs)

                // Check special positions
                val isLastPlayed = LineStateUtils.isLastPlayedLine(index, lines, currentTimeMs)
                val isFirstUpcoming = LineStateUtils.isFirstUpcomingLine(index, lines, currentTimeMs)
                val isFirstActive = LineStateUtils.isFirstActiveLine(index, lines, currentTimeMs)

                Column {
                    // Add spacing before line
                    if (index > 0) {
                        val hasActiveLine = LineStateUtils.hasActiveLine(lines, currentTimeMs)

                        val spaceBefore = when {
                            // Large space before first active line (separation from played lines)
                            isFirstActive -> config.layout.activeGroupSpacing

                            // Large space before first upcoming when NO active line exists
                            // This creates visual separation when in gap between lines
                            isFirstUpcoming && !hasActiveLine -> config.layout.activeGroupSpacing * 1.5f

                            // Normal space before first upcoming when active line exists
                            isFirstUpcoming && hasActiveLine -> config.layout.upcomingGroupSpacing

                            // Standard spacing between lines in same group
                            else -> config.layout.lineSpacing
                        }
                        Spacer(modifier = Modifier.height(spaceBefore))
                    }
                    // Note: First line positioning is handled by scrolling, no need for extra padding

                    KaraokeSingleLineWithDistance(
                        line = line,
                        currentTimeMs = currentTimeMs,
                        distance = LineStateUtils.getDistanceFromCurrentLine(index, currentLineIndex),
                        config = config,
                        onLineClick = if (onLineClick != null) {
                            { onLineClick(line, index) }
                        } else null,
                        onLineLongPress = if (onLineLongPress != null) {
                            { onLineLongPress(line, index) }
                        } else null
                    )

                    // Add extra spacing after last played line for visual separation
                    // This ensures played lines don't disappear too quickly
                    if (isLastPlayed) {
                        val hasActiveLine = LineStateUtils.hasActiveLine(lines, currentTimeMs)
                        // Use larger spacing when no active line to push content down
                        val spaceAfter = if (!hasActiveLine) {
                            screenHeight * 0.6f // More than half screen when in gap
                        } else {
                            screenHeight * 0.25f // Reduced spacing when active to hide upcoming
                        }
                        Spacer(modifier = Modifier.height(spaceAfter))
                    }
                }
            }
        }
    }
}

/**
 * Internal wrapper that includes distance-based effects for a single line.
 */
@Composable
private fun KaraokeSingleLineWithDistance(
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

    // Determine line state using utility
    val lineState = LineStateUtils.getLineState(line, currentTimeMs)

    // Apply distance-based blur ONLY to upcoming/unplayed lines
    val distanceBlurRadius = if (config.effects.enableBlur && !lineState.isPlaying && !lineState.hasPlayed) {
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
        KaraokeSingleLine(
            line = line,
            currentTimeMs = currentTimeMs,
            config = config,
            onLineClick = onLineClick
        )
    }
}