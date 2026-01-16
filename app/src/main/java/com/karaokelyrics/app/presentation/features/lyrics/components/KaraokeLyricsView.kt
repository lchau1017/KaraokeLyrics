package com.karaokelyrics.app.presentation.features.lyrics.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.karaokelyrics.app.domain.model.ISyncedLine
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.presentation.features.lyrics.components.line.LyricsLineItem
import com.karaokelyrics.app.presentation.features.lyrics.components.scroll.rememberLyricsScrollController
import com.karaokelyrics.app.presentation.features.lyrics.config.KaraokeConfig
import com.karaokelyrics.app.presentation.features.lyrics.model.LyricsLineUiModel
import com.karaokelyrics.app.presentation.features.lyrics.model.LyricsUiState

/**
 * Refactored KaraokeLyricsView using pre-calculated UI models.
 * "Dumb View" - only renders, no calculations.
 */
@Composable
fun KaraokeLyricsView(
    uiState: LyricsUiState,
    onLineClicked: (ISyncedLine) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    enableCharacterAnimations: Boolean = true,
    offset: Dp = 100.dp,
    config: KaraokeConfig = KaraokeConfig.Default
) {
    val density = LocalDensity.current

    // Calculate stable offset in pixels
    val offsetPx = remember(offset, density) {
        with(density) { offset.toPx().toInt() }
    }

    // Use scroll controller
    val scrollController = rememberLyricsScrollController(listState, offsetPx)

    // Auto-scroll to focused line
    LaunchedEffect(uiState.focusedLineIndex) {
        scrollController.scrollToLine(uiState.focusedLineIndex)
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = offset),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = uiState.lines,
            key = { lineModel ->
                "${lineModel.line.start}-${lineModel.line.end}-${lineModel.index}-${lineModel.visualState.color.value}"
            }
        ) { lineModel ->
            KaraokeLyricsLineItem(
                uiModel = lineModel,
                currentTimeMs = uiState.currentTimeMs,
                enableCharacterAnimations = enableCharacterAnimations,
                onLineClicked = onLineClicked,
                config = config
            )
        }

        // Bottom spacer for scrolling
        item {
            Spacer(modifier = Modifier.height(500.dp))
        }
    }
}

/**
 * Line item that uses pre-calculated UI model.
 * No calculations, just rendering.
 */
@Composable
fun KaraokeLyricsLineItem(
    uiModel: LyricsLineUiModel,
    currentTimeMs: Int,
    enableCharacterAnimations: Boolean,
    onLineClicked: (ISyncedLine) -> Unit,
    config: KaraokeConfig
) {
    // Animate opacity for smooth transitions
    val animatedOpacity by animateFloatAsState(
        targetValue = uiModel.visualState.opacity,
        label = "opacity"
    )

    // Debug: Log the values being passed
    LaunchedEffect(uiModel.index) {
        if (uiModel.index == 0) {
            println("DEBUG: Line ${uiModel.index} - opacity: ${uiModel.visualState.opacity}, color: ${uiModel.visualState.color}, text: ${uiModel.line.content}")
        }
    }

    // Use pre-calculated values from UI model
    LyricsLineItem(
        line = uiModel.line,
        currentTimeMs = currentTimeMs,
        opacity = animatedOpacity,
        scale = uiModel.visualState.scale,
        blur = uiModel.visualState.blur,
        textColor = uiModel.visualState.color,
        textStyle = uiModel.visualState.textStyle,
        enableCharacterAnimations = enableCharacterAnimations,
        onLineClicked = onLineClicked,
        config = config
    )
}