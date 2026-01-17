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

    // Find the current playing line index (for distance calculation)
    val currentLineIndex = remember(currentTimeMs, lines) {
        LineStateUtils.getCurrentLineIndex(lines, currentTimeMs)
    }

    // Find the last played line index
    val lastPlayedLineIndex = remember(currentTimeMs, lines) {
        LineStateUtils.getLastPlayedLineIndex(lines, currentTimeMs)
    }

    // Track the previous last played line index to detect changes
    var previousLastPlayedIndex by remember { mutableStateOf<Int?>(null) }

    // Handle automatic scrolling based on last played line
    LaunchedEffect(lastPlayedLineIndex) {
        if (lastPlayedLineIndex != null &&
            lastPlayedLineIndex != previousLastPlayedIndex) {

            previousLastPlayedIndex = lastPlayedLineIndex

            // Scroll to show the next line after the last played
            coroutineScope.launch {
                val targetIndex = (lastPlayedLineIndex + 1).coerceAtMost(lines.size - 1)
                listState.animateScrollToItem(
                    index = targetIndex,
                    scrollOffset = 0
                )
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
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),  // Status bar padding only
                bottom = screenHeight // Full screen height bottom padding
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
                        val spaceBefore = when {
                            // Add extra space only before the first active line in the group
                            isFirstActive -> 60.dp
                            // Add extra space before first upcoming line
                            isFirstUpcoming -> 60.dp
                            // Normal spacing for all other cases
                            else -> config.layout.lineSpacing
                        }
                        Spacer(modifier = Modifier.height(spaceBefore))
                    }

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

                    // Add extra spacing after last played line
                    if (isLastPlayed) {
                        Spacer(modifier = Modifier.height(screenHeight * 0.25f))
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