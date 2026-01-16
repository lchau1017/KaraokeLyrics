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
import com.karaokelyrics.app.presentation.features.lyrics.model.LyricsRenderModel
import com.karaokelyrics.app.presentation.features.lyrics.model.LyricsRenderState

/**
 * Refactored KaraokeLyricsView using pre-calculated render models.
 * "Dumb View" - only renders, no calculations.
 */
@Composable
fun KaraokeLyricsView(
    renderState: LyricsRenderState,
    onLineClicked: (ISyncedLine) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    val density = LocalDensity.current
    val globalConfig = renderState.globalConfig

    // Calculate stable offset in pixels
    val offsetPx = remember(globalConfig.verticalPadding, density) {
        with(density) { globalConfig.verticalPadding.dp.toPx().toInt() }
    }

    // Use scroll controller
    val scrollController = rememberLyricsScrollController(listState, offsetPx)

    // Auto-scroll to target
    LaunchedEffect(renderState.scrollTarget) {
        renderState.scrollTarget?.let {
            scrollController.scrollToLine(it.index)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            vertical = globalConfig.verticalPadding.dp,
            horizontal = globalConfig.horizontalPadding.dp
        ),
        verticalArrangement = Arrangement.spacedBy(globalConfig.lineSpacing.dp)
    ) {
        items(
            items = renderState.models,
            key = { model ->
                "${model.line.start}-${model.line.end}-${model.index}"
            }
        ) { model ->
            KaraokeLyricsLineItem(
                renderModel = model,
                onLineClicked = onLineClicked
            )
        }

        // Bottom spacer for scrolling
        item {
            Spacer(modifier = Modifier.height(500.dp))
        }
    }
}

/**
 * Line item that uses pre-calculated render model.
 * No calculations, just rendering.
 */
@Composable
fun KaraokeLyricsLineItem(
    renderModel: LyricsRenderModel,
    onLineClicked: (ISyncedLine) -> Unit
) {
    // Animate opacity for smooth transitions
    val animatedOpacity by animateFloatAsState(
        targetValue = renderModel.visual.opacity,
        label = "opacity"
    )

    // Use pre-calculated values from render model
    LyricsLineItem(
        line = renderModel.line,
        currentTimeMs = renderModel.timing.currentTimeMs,
        opacity = animatedOpacity,
        scale = renderModel.visual.scale,
        blur = renderModel.visual.blur,
        textColor = renderModel.visual.textColor,
        textStyle = renderModel.visual.textStyle,
        enableCharacterAnimations = renderModel.visual.enableCharacterAnimations,
        onLineClicked = onLineClicked,
        config = KaraokeConfig(
            characterFloatOffset = renderModel.visual.characterFloatOffset
        )
    )
}