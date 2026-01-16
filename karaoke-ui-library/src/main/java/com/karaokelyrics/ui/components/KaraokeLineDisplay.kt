package com.karaokelyrics.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale as drawScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.core.models.KaraokeLine
import com.karaokelyrics.ui.rendering.animation.AnimationStateManager
import com.karaokelyrics.ui.rendering.animation.CharacterAnimationCalculator
import com.karaokelyrics.ui.rendering.effects.GradientFactory
import com.karaokelyrics.ui.rendering.effects.VisualEffects
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

    // Determine text color (base color for unplayed characters)
    val textColor = when {
        line is KaraokeLine && line.isAccompaniment -> config.visual.accompanimentTextColor
        isPlaying -> config.visual.upcomingTextColor  // Use upcoming color for unplayed chars in active line
        hasPlayed -> config.visual.playedTextColor
        else -> config.visual.upcomingTextColor
    }

    // Apply modifiers (blur is handled at the display level, not here)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(config.layout.linePadding)
            .scale(lineAnimationState.scale)
            .alpha(opacity)
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
 * Internal component for rendering karaoke syllables with character-by-character progression.
 */
@Composable
private fun KaraokeSyllableRenderer(
    line: KaraokeLine,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig,
    textStyle: TextStyle,
    baseColor: Color
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val animationManager = remember { AnimationStateManager() }
    val characterAnimator = remember { CharacterAnimationCalculator() }

    // Calculate the total text to get dimensions
    val fullText = line.syllables.joinToString("") { it.content }
    val textLayoutResult = remember(fullText, textStyle) {
        textMeasurer.measure(
            text = fullText,
            style = textStyle
        )
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(with(density) { textLayoutResult.size.height.toDp() })
    ) {
        // Create gradient for character-by-character progression
        val gradient = createKaraokeGradient(
            line = line,
            currentTimeMs = currentTimeMs,
            width = size.width,
            activeColor = config.visual.playingTextColor,
            inactiveColor = baseColor
        )

        var xOffset = 0f
        line.syllables.forEach { syllable ->
            // For each syllable, render its characters
            val syllableDuration = syllable.end - syllable.start
            val charCount = syllable.content.length
            val charDuration = if (charCount > 0) syllableDuration.toFloat() / charCount else 0f

            syllable.content.forEachIndexed { charIndex, char ->
                val charStartTime = syllable.start + (charIndex * charDuration).toInt()
                val charEndTime = syllable.start + ((charIndex + 1) * charDuration).toInt()

                // Determine character state
                val hasCharPlayed = currentTimeMs > charEndTime
                val isCharActive = currentTimeMs >= charStartTime &&
                    currentTimeMs <= (charEndTime + config.animation.characterAnimationDuration.toInt())

                // Measure this character
                val charText = char.toString()
                val charLayout = textMeasurer.measure(
                    text = charText,
                    style = textStyle
                )

                // Get character color based on precise timing
                val charColor = when {
                    currentTimeMs > charEndTime -> {
                        // Character has been fully played
                        config.visual.playingTextColor
                    }
                    currentTimeMs >= charStartTime -> {
                        // Character is currently playing - interpolate
                        val progress = if (charEndTime > charStartTime) {
                            ((currentTimeMs - charStartTime).toFloat() / (charEndTime - charStartTime))
                                .coerceIn(0f, 1f)
                        } else 1f
                        lerpColor(baseColor, config.visual.playingTextColor, progress)
                    }
                    else -> {
                        // Character hasn't started yet
                        baseColor
                    }
                }

                // Apply character animations if enabled
                if (config.animation.enableCharacterAnimations && isCharActive) {
                    val animState = characterAnimator.calculateCharacterAnimation(
                        characterStartTime = charStartTime,
                        characterEndTime = charEndTime,
                        currentTime = currentTimeMs,
                        animationDuration = config.animation.characterAnimationDuration,
                        maxScale = config.animation.characterMaxScale,
                        floatOffset = config.animation.characterFloatOffset,
                        rotationDegrees = config.animation.characterRotationDegrees
                    )

                    drawIntoCanvas {
                        drawScale(
                            scale = animState.scale,
                            pivot = Offset(xOffset + charLayout.size.width / 2f, charLayout.size.height / 2f)
                        ) {
                            rotate(
                                degrees = animState.rotation,
                                pivot = Offset(xOffset + charLayout.size.width / 2f, charLayout.size.height / 2f)
                            ) {
                                drawText(
                                    textLayoutResult = charLayout,
                                    color = charColor,
                                    topLeft = Offset(xOffset + animState.offset.x, animState.offset.y)
                                )
                            }
                        }
                    }
                } else {
                    // Draw without animation
                    drawText(
                        textLayoutResult = charLayout,
                        color = charColor,
                        topLeft = Offset(xOffset, 0f)
                    )
                }

                xOffset += charLayout.size.width
            }
        }
    }
}

/**
 * Create a gradient for karaoke progression based on character timing.
 */
private fun createKaraokeGradient(
    line: KaraokeLine,
    currentTimeMs: Int,
    width: Float,
    activeColor: Color,
    inactiveColor: Color
): Brush {
    if (line.syllables.isEmpty()) {
        return Brush.horizontalGradient(listOf(inactiveColor, inactiveColor))
    }

    val firstStart = line.syllables.first().start
    val lastEnd = line.syllables.last().end

    // Before line starts
    if (currentTimeMs < firstStart) {
        return Brush.horizontalGradient(listOf(inactiveColor, inactiveColor))
    }

    // After line ends
    if (currentTimeMs >= lastEnd) {
        return Brush.horizontalGradient(listOf(inactiveColor, inactiveColor))
    }

    // Calculate progress through the entire line
    var totalChars = 0
    var playedChars = 0

    line.syllables.forEach { syllable ->
        val syllableChars = syllable.content.length
        val syllableDuration = syllable.end - syllable.start
        val charDuration = if (syllableChars > 0) syllableDuration.toFloat() / syllableChars else 0f

        repeat(syllableChars) { charIndex ->
            val charEndTime = syllable.start + ((charIndex + 1) * charDuration).toInt()
            if (currentTimeMs > charEndTime) {
                playedChars++
            }
            totalChars++
        }
    }

    val progress = if (totalChars > 0) playedChars.toFloat() / totalChars else 0f

    // Create gradient with smooth transition
    val fadeWidth = 0.05f
    val fadeStart = (progress - fadeWidth).coerceAtLeast(0f)
    val fadeEnd = (progress + fadeWidth).coerceAtMost(1f)

    return Brush.horizontalGradient(
        colorStops = arrayOf(
            0f to activeColor,
            fadeStart to activeColor,
            progress to activeColor.copy(alpha = 0.8f),
            fadeEnd to inactiveColor,
            1f to inactiveColor
        ),
        startX = 0f,
        endX = width
    )
}

/**
 * Interpolate between two colors.
 */
private fun lerpColor(start: Color, end: Color, fraction: Float): Color {
    return Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = start.alpha + (end.alpha - start.alpha) * fraction
    )
}