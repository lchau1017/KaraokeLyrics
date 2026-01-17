package com.karaokelyrics.app.presentation.ui.components

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karaokelyrics.app.domain.model.ISyncedLine
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.model.karaoke.KaraokeAlignment
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.domain.model.synced.SyncedLine
import kotlinx.coroutines.launch

@Composable
fun KaraokeLyricsView(
    lyrics: SyncedLyrics,
    currentPosition: () -> Long,
    onLineClicked: (ISyncedLine) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    normalLineTextStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 34.sp,
        fontWeight = FontWeight.Bold,
        textMotion = TextMotion.Animated,
    ),
    accompanimentLineTextStyle: TextStyle = LocalTextStyle.current.copy(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        textMotion = TextMotion.Animated,
    ),
    textColor: Color = Color.White,
    useBlurEffect: Boolean = true,
    offset: Dp = 200.dp
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val currentTimeMs = currentPosition().toInt()

    // Track if we're scrolling programmatically
    val scrollInCode = remember { mutableStateOf(false) }

    // Calculate stable offset in pixels
    val offsetPx = remember(offset, density) {
        with(density) { offset.toPx().toInt() }
    }

    // Find the current focused line index - matching original logic
    val firstFocusedLineIndex by remember(lyrics.lines, currentTimeMs) {
        derivedStateOf {
            val rawIndex = lyrics.lines.indexOfLast { line ->
                currentTimeMs >= line.start && currentTimeMs < line.end
            }

            // If it's an accompaniment line, find the nearest main line
            if (rawIndex >= 0) {
                val line = lyrics.lines[rawIndex] as? KaraokeLine
                if (line != null && line.isAccompaniment) {
                    // Find the previous non-accompaniment line
                    for (i in rawIndex downTo 0) {
                        val checkLine = lyrics.lines[i] as? KaraokeLine
                        if (checkLine == null || !checkLine.isAccompaniment) {
                            return@derivedStateOf i
                        }
                    }
                }
            }
            rawIndex
        }
    }

    // Get all currently highlighted lines (for multi-voice support)
    val allFocusedLineIndices by remember(lyrics.lines, currentTimeMs) {
        derivedStateOf {
            lyrics.lines.mapIndexedNotNull { index, line ->
                // Only highlight if we're actually within the line timing
                if (currentTimeMs >= line.start && currentTimeMs < line.end) index else null
            }
        }
    }

    // Improved auto-scroll logic from original
    LaunchedEffect(firstFocusedLineIndex, offsetPx) {
        if (firstFocusedLineIndex >= 0 && !scrollInCode.value && !listState.isScrollInProgress) {
            val items = listState.layoutInfo.visibleItemsInfo
            val targetItem = items.firstOrNull { it.index == firstFocusedLineIndex }

            if (targetItem != null) {
                // Calculate scroll offset to center the item with offset
                val viewportStart = listState.layoutInfo.viewportStartOffset
                val targetOffset = targetItem.offset - viewportStart - offsetPx

                if (kotlin.math.abs(targetOffset) > 10) { // Only scroll if offset is significant
                    try {
                        scrollInCode.value = true
                        listState.animateScrollBy(
                            targetOffset.toFloat(),
                            animationSpec = tween(400, easing = EaseOut)
                        )
                    } finally {
                        scrollInCode.value = false
                    }
                }
            } else {
                // Item not visible, scroll to it
                try {
                    scrollInCode.value = true
                    listState.animateScrollToItem(firstFocusedLineIndex, -offsetPx)
                } finally {
                    scrollInCode.value = false
                }
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = offset),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(
            items = lyrics.lines,
            key = { index, line -> "${line.start}-${line.end}-$index" }
        ) { index, line ->
            val isCurrentLine = index in allFocusedLineIndices

            // Determine if this line has been played
            val hasBeenPlayed = line.end <= currentTimeMs
            val isUpcoming = line.start > currentTimeMs

            // Calculate distance from the closest active line
            val distanceFromCurrent = when {
                allFocusedLineIndices.isEmpty() -> {
                    // No active line - calculate based on whether played or upcoming
                    if (hasBeenPlayed) {
                        // Distance from last played line
                        val lastPlayedIndex = lyrics.lines.indexOfLast { it.end <= currentTimeMs }
                        if (lastPlayedIndex >= 0) kotlin.math.abs(index - lastPlayedIndex) + 3
                        else Int.MAX_VALUE
                    } else {
                        // Distance from next upcoming line
                        val nextIndex = lyrics.lines.indexOfFirst { it.start > currentTimeMs }
                        if (nextIndex >= 0) kotlin.math.abs(index - nextIndex) + 3
                        else Int.MAX_VALUE
                    }
                }
                index < allFocusedLineIndices.first() -> allFocusedLineIndices.first() - index
                index > allFocusedLineIndices.last() -> index - allFocusedLineIndices.last()
                else -> 0
            }

            // Calculate opacity with proper fade for played lines
            val opacity by animateFloatAsState(
                targetValue = when {
                    isCurrentLine -> 1f // Currently playing
                    hasBeenPlayed -> {
                        // Fade out effect for played lines
                        val timeSincePlayed = (currentTimeMs - line.end).coerceAtLeast(0)
                        when {
                            timeSincePlayed < 500 -> 0.8f
                            timeSincePlayed < 1000 -> 0.6f
                            timeSincePlayed < 2000 -> 0.4f
                            else -> 0.25f // Minimum opacity for old played lines
                        }
                    }
                    isUpcoming -> {
                        // Upcoming lines based on distance
                        when (distanceFromCurrent) {
                            1 -> 0.6f
                            2 -> 0.45f
                            3 -> 0.35f
                            else -> 0.25f
                        }
                    }
                    else -> 0.3f
                },
                animationSpec = tween(300),
                label = "opacity"
            )

            // Scale animation for current line
            val scale by animateFloatAsState(
                targetValue = if (isCurrentLine) 1.05f else 1f,
                animationSpec = tween(300),
                label = "scale"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLineClicked(line) }
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        alpha = opacity
                    }
                    .padding(horizontal = 24.dp),
                contentAlignment = when (line) {
                    is KaraokeLine -> when (line.alignment) {
                        KaraokeAlignment.Start -> Alignment.CenterStart
                        KaraokeAlignment.End -> Alignment.CenterEnd
                        else -> Alignment.Center
                    }
                    else -> Alignment.Center
                }
            ) {
                when (line) {
                    is KaraokeLine -> {
                        KaraokeLineText(
                            line = line,
                            currentPosition = currentTimeMs,
                            textStyle = if (line.isAccompaniment) {
                                accompanimentLineTextStyle
                            } else {
                                normalLineTextStyle
                            },
                            activeColor = if (line.isAccompaniment) {
                                textColor.copy(alpha = 0.7f)
                            } else {
                                textColor
                            },
                            inactiveColor = if (line.isAccompaniment) {
                                textColor.copy(alpha = 0.2f)
                            } else {
                                textColor.copy(alpha = 0.3f)
                            },
                            enableBlurEffect = useBlurEffect,
                            enableCharacterAnimations = true
                        )
                    }
                    is SyncedLine -> {
                        Text(
                            text = line.content,
                            style = normalLineTextStyle,
                            color = if (isCurrentLine) textColor else textColor.copy(alpha = 0.3f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Bottom spacer for scrolling
        item {
            Spacer(modifier = Modifier.height(500.dp))
        }
    }
}