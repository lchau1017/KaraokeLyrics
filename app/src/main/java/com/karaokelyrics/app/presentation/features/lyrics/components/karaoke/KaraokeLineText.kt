package com.karaokelyrics.app.presentation.features.lyrics.components.karaoke

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.alignment.KaraokeAlignmentResolver
import com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.direction.TextDirectionDetector
import com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.layout.rememberKaraokeLayout
import com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.renderer.KaraokeRowRenderer
import com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.state.KaraokeLineState
import com.karaokelyrics.app.presentation.features.lyrics.config.KaraokeConfig
import com.karaokelyrics.app.presentation.shared.animation.rememberAnimationStateManager
import com.karaokelyrics.app.presentation.shared.rendering.SyllableRenderer

/**
 * Refactored KaraokeLineContainer following SOLID principles.
 * Delegates responsibilities to specialized components.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun KaraokeLineText(
    line: KaraokeLine,
    currentTimeMs: Int,
    textStyle: TextStyle,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier,
    enableCharacterAnimations: Boolean = true,
    enableBlurEffect: Boolean = true,
    config: KaraokeConfig = KaraokeConfig.Default
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val animationStateManager = rememberAnimationStateManager()
    val syllableRenderer = remember { SyllableRenderer() }
    val rowRenderer = remember { KaraokeRowRenderer(syllableRenderer, animationStateManager) }

    // Create state object
    val isRtl = remember(line.syllables) {
        TextDirectionDetector.isRightToLeft(line.syllables)
    }

    val lineState = remember(activeColor, inactiveColor, enableCharacterAnimations, enableBlurEffect, isRtl) {
        KaraokeLineState(
            activeColor = activeColor,
            inactiveColor = inactiveColor,
            enableCharacterAnimations = enableCharacterAnimations,
            enableBlurEffect = enableBlurEffect,
            isRtl = isRtl
        )
    }

    // Clear animations when visual settings change
    LaunchedEffect(lineState.visualKey) {
        animationStateManager.clearAllAnimations()
    }

    // Resolve alignment
    val alignment = remember(line.alignment, isRtl) {
        KaraokeAlignmentResolver.resolveAlignment(line.alignment, isRtl)
    }

    // Clean up old animations periodically based on config
    LaunchedEffect(currentTimeMs) {
        if (currentTimeMs % config.cleanupInterval == 0) {
            animationStateManager.clearOldAnimations(currentTimeMs)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = config.linePadding, vertical = config.lineSpacing / 2),
        contentAlignment = alignment
    ) {
        val availableWidthPx = with(density) { maxWidth.toPx() }
        val lineHeight = with(density) { textStyle.fontSize.toPx() * 1.5f }

        // Calculate layout using dedicated calculator
        val layout = rememberKaraokeLayout(
            line = line,
            textStyle = textStyle,
            availableWidthPx = availableWidthPx,
            lineHeight = lineHeight,
            textMeasurer = textMeasurer,
            enableCharacterAnimations = lineState.enableCharacterAnimations,
            isRtl = lineState.isRtl
        )

        // Render with animation support
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(density) { layout.totalHeight.toDp() })
        ) {
            // Delegate rendering to specialized renderer
            with(rowRenderer) {
                layout.syllableLayouts.forEach { rowLayouts ->
                    renderRow(
                        rowLayouts = rowLayouts,
                        currentTimeMs = currentTimeMs,
                        activeColor = lineState.activeColor,
                        inactiveColor = lineState.inactiveColor,
                        enableCharacterAnimations = lineState.enableCharacterAnimations,
                        enableBlurEffect = lineState.enableBlurEffect,
                        isRtl = lineState.isRtl
                    )
                }
            }
        }
    }
}
    