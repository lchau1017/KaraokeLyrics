package com.karaokelyrics.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.core.models.KaraokeLine
import com.karaokelyrics.ui.rendering.animation.AnimationStateManager
import com.karaokelyrics.ui.rendering.effects.GradientFactory
import com.karaokelyrics.ui.rendering.effects.VisualEffects
import com.karaokelyrics.ui.rendering.effects.VisualEffects.applyConditionalBlur
import com.karaokelyrics.ui.rendering.text.TextDirectionDetector

/**
 * Composable for displaying a single karaoke line with synchronized highlighting.
 *
 * @param line The synchronized line to display
 * @param currentTimeMs Current playback time in milliseconds
 * @param config Complete configuration for visual, animation, and behavior
 * @param modifier Modifier for the composable
 * @param onLineClick Optional callback when line is clicked
 */
@Composable
fun KaraokeLineDisplay(
    line: ISyncedLine,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig = KaraokeLibraryConfig.Default,
    modifier: Modifier = Modifier,
    onLineClick: ((ISyncedLine) -> Unit)? = null
) {
    val animationManager = remember { AnimationStateManager() }
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    // Determine line state
    val isPlaying = currentTimeMs in line.start..line.end
    val hasPlayed = currentTimeMs > line.end
    val isUpcoming = currentTimeMs < line.start

    // Get line-level animation state
    val lineAnimationState = animationManager.animateLine(
        lineStartTime = line.start,
        lineEndTime = line.end,
        currentTime = currentTimeMs,
        scaleOnPlay = config.animation.lineScaleOnPlay,
        animationDuration = config.animation.lineAnimationDuration.toInt()
    )

    // Calculate opacity
    val opacity = VisualEffects.calculateOpacity(
        isPlaying = isPlaying,
        hasPlayed = hasPlayed,
        distance = 0,
        playingOpacity = config.effects.playingLineOpacity,
        playedOpacity = config.effects.playedLineOpacity,
        upcomingOpacity = config.effects.upcomingLineOpacity
    )

    // Determine text style based on line type
    val textStyle = if (line is KaraokeLine && line.isAccompaniment) {
        TextStyle(
            fontSize = config.visual.accompanimentFontSize,
            fontWeight = config.visual.fontWeight,
            fontFamily = config.visual.fontFamily,
            letterSpacing = config.visual.letterSpacing,
            textAlign = config.visual.textAlign
        )
    } else {
        TextStyle(
            fontSize = config.visual.fontSize,
            fontWeight = config.visual.fontWeight,
            fontFamily = config.visual.fontFamily,
            letterSpacing = config.visual.letterSpacing,
            textAlign = config.visual.textAlign
        )
    }

    // Determine text color
    val textColor = when {
        line is KaraokeLine && line.isAccompaniment -> config.visual.accompanimentTextColor
        isPlaying -> config.visual.playingTextColor
        hasPlayed -> config.visual.playedTextColor
        else -> config.visual.upcomingTextColor
    }

    // Apply modifiers
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(config.layout.linePadding)
            .scale(lineAnimationState.scale)
            .alpha(opacity)
            .applyConditionalBlur(
                isPlaying = isPlaying,
                hasPlayed = hasPlayed,
                playedBlur = config.effects.playedLineBlur,
                upcomingBlur = config.effects.upcomingLineBlur,
                distantBlur = config.effects.distantLineBlur,
                distance = 0
            )
            .then(
                if (config.behavior.enableLineClick && onLineClick != null) {
                    Modifier.clickable { onLineClick(line) }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        when (line) {
            is KaraokeLine -> {
                // Render karaoke line with syllables
                KaraokeSyllableRenderer(
                    line = line,
                    currentTimeMs = currentTimeMs,
                    config = config,
                    textStyle = textStyle,
                    baseColor = textColor
                )
            }
            else -> {
                // Render simple text line
                Text(
                    text = line.getContent(),
                    style = textStyle,
                    color = textColor,
                    maxLines = Int.MAX_VALUE,
                    softWrap = true
                )
            }
        }
    }
}

/**
 * Internal component for rendering karaoke syllables.
 */
@Composable
private fun KaraokeSyllableRenderer(
    line: KaraokeLine,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig,
    textStyle: TextStyle,
    baseColor: Color
) {
    val animationManager = remember { AnimationStateManager() }

    // Build the complete text with appropriate styling
    val fullText = buildAnnotatedString {
        line.syllables.forEach { syllable ->
            val syllableProgress = animationManager.animateSyllableProgress(
                syllableStartTime = syllable.start,
                syllableEndTime = syllable.end,
                currentTime = currentTimeMs
            )

            // Add syllable text with appropriate color
            val color = if (syllableProgress >= 1f) {
                config.visual.playingTextColor
            } else {
                baseColor
            }

            // Apply color span for this syllable
            pushStyle(
                SpanStyle(
                    color = color
                )
            )
            append(syllable.content)
            pop()
        }
    }

    // Display as single Text composable to avoid layout issues
    Text(
        text = fullText,
        style = textStyle,
        maxLines = Int.MAX_VALUE, // Allow text to wrap naturally
        softWrap = true // Enable text wrapping
    )
}