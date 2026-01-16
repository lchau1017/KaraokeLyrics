package com.karaokelyrics.app.presentation.features.lyrics.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karaokelyrics.app.domain.model.ISyncedLine
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.presentation.features.lyrics.components.focus.LyricsFocusCalculator
import com.karaokelyrics.app.presentation.features.lyrics.components.focus.rememberAllFocusedIndices
import com.karaokelyrics.app.presentation.features.lyrics.components.focus.rememberFocusedLineIndex
import com.karaokelyrics.app.presentation.features.lyrics.components.line.LyricsLineItem
import com.karaokelyrics.app.presentation.features.lyrics.components.opacity.LyricsOpacityCalculator
import com.karaokelyrics.app.presentation.features.lyrics.components.opacity.animatedOpacity
import com.karaokelyrics.app.presentation.features.lyrics.components.scroll.rememberLyricsScrollController

/**
 * Refactored KaraokeLyricsView following SOLID principles.
 * Delegates responsibilities to specialized components.
 */
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
    enableCharacterAnimations: Boolean = true,
    offset: Dp = 100.dp
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val currentTimeMs = currentPosition().toInt()

    // Calculate stable offset in pixels
    val offsetPx = remember(offset, density) {
        with(density) { offset.toPx().toInt() }
    }

    // Use focused line calculators
    val firstFocusedLineIndex by rememberFocusedLineIndex(lyrics, currentTimeMs)
    val allFocusedLineIndices by rememberAllFocusedIndices(lyrics, currentTimeMs)

    // Use scroll controller
    val scrollController = rememberLyricsScrollController(listState, offsetPx)

    // Auto-scroll to focused line
    LaunchedEffect(firstFocusedLineIndex) {
        scrollController.scrollToLine(firstFocusedLineIndex)
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = offset),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(
            items = lyrics.lines,
            key = { index, line -> "${line.start}-${line.end}-$index-${textColor.value}" }
        ) { index, line ->
            val isCurrentLine = index in allFocusedLineIndices
            val hasBeenPlayed = line.end <= currentTimeMs
            val isUpcoming = line.start > currentTimeMs

            // Calculate distance using the focus calculator
            val distanceFromCurrent = LyricsFocusCalculator.calculateDistanceFromCurrent(
                index = index,
                focusedIndices = allFocusedLineIndices,
                lines = lyrics.lines,
                currentTimeMs = currentTimeMs
            )

            // Calculate visual properties using specialized calculators
            val targetOpacity = LyricsOpacityCalculator.calculateOpacity(
                isCurrentLine = isCurrentLine,
                hasBeenPlayed = hasBeenPlayed,
                isUpcoming = isUpcoming,
                distanceFromCurrent = distanceFromCurrent,
                currentTimeMs = currentTimeMs,
                lineEndTime = line.end
            )

            val opacity by animatedOpacity(targetOpacity)

            val scale = LyricsOpacityCalculator.calculateScale(
                isCurrentLine = isCurrentLine,
                hasBeenPlayed = hasBeenPlayed,
                isUpcoming = isUpcoming,
                distanceFromCurrent = distanceFromCurrent
            )

            val blur = LyricsOpacityCalculator.calculateBlur(
                useBlurEffect = useBlurEffect,
                isUpcoming = isUpcoming,
                distanceFromCurrent = distanceFromCurrent
            )

            // Determine text style
            val textStyle = when (line) {
                is KaraokeLine -> if (line.isAccompaniment) {
                    accompanimentLineTextStyle
                } else {
                    normalLineTextStyle
                }
                else -> normalLineTextStyle
            }

            // Use the new line item component
            LyricsLineItem(
                line = line,
                currentTimeMs = currentTimeMs,
                opacity = opacity,
                scale = scale,
                blur = blur,
                textColor = textColor,
                textStyle = textStyle,
                enableCharacterAnimations = enableCharacterAnimations,
                onLineClicked = onLineClicked
            )
        }

        // Bottom spacer for scrolling
        item {
            Spacer(modifier = Modifier.height(500.dp))
        }
    }
}