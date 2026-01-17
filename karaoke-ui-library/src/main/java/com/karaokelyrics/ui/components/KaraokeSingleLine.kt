package com.karaokelyrics.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.core.models.KaraokeLine
import com.karaokelyrics.ui.rendering.AnimationManager
import com.karaokelyrics.ui.rendering.EffectsManager
import com.karaokelyrics.ui.rendering.color.ColorCalculator
import com.karaokelyrics.ui.rendering.syllable.SyllableRenderer
import com.karaokelyrics.ui.utils.LineStateUtils

/**
 * Composable for displaying a single karaoke line with synchronized highlighting.
 * This is the main public API for rendering a single line of karaoke text.
 *
 * @param line The synchronized line to display
 * @param currentTimeMs Current playback time in milliseconds
 * @param config Complete configuration for visual, animation, and behavior
 * @param modifier Modifier for the composable
 * @param onLineClick Optional callback when line is clicked
 */
@Composable
fun KaraokeSingleLine(
    line: ISyncedLine,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig = KaraokeLibraryConfig.Default,
    modifier: Modifier = Modifier,
    onLineClick: ((ISyncedLine) -> Unit)? = null
) {
    // Manager instances
    val animationManager = remember(config.animation) { AnimationManager() }
    val effectsManager = remember { EffectsManager() }
    val colorCalculator = remember { ColorCalculator() }

    // Determine line state
    val lineState = LineStateUtils.getLineState(line, currentTimeMs)

    // Calculate animations
    val lineAnimationState = animationManager.animateLine(
        lineStartTime = line.start,
        lineEndTime = line.end,
        currentTime = currentTimeMs,
        scaleOnPlay = config.animation.lineScaleOnPlay,
        animationDuration = config.animation.lineAnimationDuration.toInt()
    )

    val pulseScale = if (config.animation.enablePulse) {
        animationManager.animatePulse(
            enabled = lineState.isPlaying,
            minScale = config.animation.pulseMinScale,
            maxScale = config.animation.pulseMaxScale,
            duration = config.animation.pulseDuration
        )
    } else 1f

    val shimmerProgress = if (config.animation.enableShimmer) {
        animationManager.animateShimmer(
            enabled = lineState.isPlaying,
            duration = config.animation.shimmerDuration
        )
    } else 0f

    // Calculate visual properties
    val opacity = effectsManager.calculateOpacity(
        isPlaying = lineState.isPlaying,
        hasPlayed = lineState.hasPlayed,
        distance = 0,
        playingOpacity = config.effects.playingLineOpacity,
        playedOpacity = config.effects.playedLineOpacity,
        upcomingOpacity = config.effects.upcomingLineOpacity
    )

    val textStyle = createTextStyle(line, config)
    val textColor = calculateTextColor(line, lineState, config, colorCalculator)

    // Main container
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(config.layout.linePadding)
            .scale(lineAnimationState.scale * pulseScale)
            .alpha(opacity)
            .then(
                if (config.behavior.enableLineClick && onLineClick != null) {
                    Modifier.clickable { onLineClick(line) }
                } else {
                    Modifier
                }
            ),
        contentAlignment = getContentAlignment(config.visual.textAlign)
    ) {
        when (line) {
            is KaraokeLine -> {
                // Render karaoke line with syllable-level timing
                SyllableRenderer(
                    line = line,
                    currentTimeMs = currentTimeMs,
                    config = config,
                    textStyle = textStyle,
                    baseColor = textColor,
                    shimmerProgress = shimmerProgress
                )
            }
            else -> {
                // Render simple text line
                SimpleTextLine(
                    text = line.getContent(),
                    textStyle = textStyle,
                    textColor = textColor
                )
            }
        }
    }
}

/**
 * Simple text line without karaoke effects
 */
@Composable
private fun SimpleTextLine(
    text: String,
    textStyle: TextStyle,
    textColor: androidx.compose.ui.graphics.Color
) {
    Text(
        text = text,
        style = textStyle,
        color = textColor,
        maxLines = Int.MAX_VALUE,
        softWrap = true
    )
}

/**
 * Create text style based on line type and configuration
 */
private fun createTextStyle(
    line: ISyncedLine,
    config: KaraokeLibraryConfig
): TextStyle {
    val isAccompaniment = line is KaraokeLine && line.isAccompaniment

    return TextStyle(
        fontSize = if (isAccompaniment) {
            config.visual.accompanimentFontSize
        } else {
            config.visual.fontSize
        },
        fontWeight = config.visual.fontWeight,
        fontFamily = config.visual.fontFamily,
        letterSpacing = config.visual.letterSpacing,
        textAlign = config.visual.textAlign
    )
}

/**
 * Calculate text color based on line state
 */
private fun calculateTextColor(
    line: ISyncedLine,
    lineState: LineStateUtils.LineState,
    config: KaraokeLibraryConfig,
    colorCalculator: ColorCalculator
): androidx.compose.ui.graphics.Color {
    val isAccompaniment = line is KaraokeLine && line.isAccompaniment

    return colorCalculator.calculateLineColor(
        isPlaying = lineState.isPlaying,
        hasPlayed = lineState.hasPlayed,
        isAccompaniment = isAccompaniment,
        playingTextColor = config.visual.playingTextColor,
        playedTextColor = config.visual.playedTextColor,
        upcomingTextColor = config.visual.upcomingTextColor,
        accompanimentTextColor = config.visual.accompanimentTextColor
    )
}

/**
 * Get content alignment based on text align
 */
private fun getContentAlignment(textAlign: TextAlign?): Alignment {
    return when (textAlign) {
        TextAlign.Start, TextAlign.Left -> Alignment.CenterStart
        TextAlign.End, TextAlign.Right -> Alignment.CenterEnd
        else -> Alignment.Center
    }
}