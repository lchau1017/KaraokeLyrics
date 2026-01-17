package com.karaokelyrics.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.core.models.KaraokeLine
import com.karaokelyrics.ui.rendering.animatePulse
import com.karaokelyrics.ui.rendering.syllable.SyllableRenderer
import com.karaokelyrics.ui.state.LineUiState

/**
 * Stateless composable for displaying a single karaoke line with synchronized highlighting.
 * Uses pre-calculated LineUiState for efficient rendering.
 *
 * @param line The synchronized line to display
 * @param lineUiState Pre-calculated UI state for this line
 * @param currentTimeMs Current playback time in milliseconds
 * @param config Complete configuration for visual, animation, and behavior
 * @param modifier Modifier for the composable
 * @param onLineClick Optional callback when line is clicked
 */
@Composable
internal fun KaraokeSingleLine(
    line: ISyncedLine,
    lineUiState: LineUiState,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig,
    modifier: Modifier = Modifier,
    onLineClick: ((ISyncedLine) -> Unit)? = null
) {
    // Use pre-calculated state for opacity and scale
    // But still need animated values for smooth transitions
    val animatedScale by animateFloatAsState(
        targetValue = lineUiState.scale,
        animationSpec = tween(
            durationMillis = config.animation.lineAnimationDuration.toInt(),
            easing = FastOutSlowInEasing
        ),
        label = "lineScale"
    )

    val animatedOpacity by animateFloatAsState(
        targetValue = lineUiState.opacity,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "lineOpacity"
    )

    val pulseScale = if (config.animation.enablePulse && lineUiState.isPlaying) {
        animatePulse(
            enabled = true,
            minScale = config.animation.pulseMinScale,
            maxScale = config.animation.pulseMaxScale,
            duration = config.animation.pulseDuration
        )
    } else 1f

    val textStyle = createTextStyle(line, config)
    val textColor = calculateTextColor(line, lineUiState, config)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(config.layout.linePadding)
            .scale(animatedScale * pulseScale)
            .alpha(animatedOpacity)
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
                SyllableRenderer(
                    line = line,
                    currentTimeMs = currentTimeMs,
                    config = config,
                    textStyle = textStyle,
                    baseColor = textColor
                )
            }
            else -> {
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
    textColor: Color
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
 * Calculate text color based on LineUiState
 */
private fun calculateTextColor(
    line: ISyncedLine,
    lineUiState: LineUiState,
    config: KaraokeLibraryConfig
): Color {
    val isAccompaniment = line is KaraokeLine && line.isAccompaniment

    return when {
        isAccompaniment -> config.visual.accompanimentTextColor
        lineUiState.isPlaying -> config.visual.upcomingTextColor // Base color for unplayed chars in active line
        lineUiState.hasPlayed -> config.visual.playedTextColor
        else -> config.visual.upcomingTextColor
    }
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
