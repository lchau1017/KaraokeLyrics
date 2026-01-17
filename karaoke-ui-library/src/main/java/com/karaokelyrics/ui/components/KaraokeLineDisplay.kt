package com.karaokelyrics.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    // Reset animation manager when config changes
    val animationManager = remember(config.animation) { AnimationStateManager() }
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

    // Get pulse animation for active lines
    val pulseScale = if (config.animation.enablePulse) {
        animationManager.animatePulse(
            enabled = isPlaying,
            minScale = config.animation.pulseMinScale,
            maxScale = config.animation.pulseMaxScale,
            duration = config.animation.pulseDuration
        )
    } else 1f

    // Get shimmer effect for active lines
    val shimmerProgress = if (config.animation.enableShimmer) {
        animationManager.animateShimmer(
            enabled = isPlaying,
            duration = config.animation.shimmerDuration
        )
    } else 0f

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
            .scale(lineAnimationState.scale * pulseScale)
            .alpha(opacity)
            .then(
                if (config.behavior.enableLineClick && onLineClick != null) {
                    Modifier.clickable { onLineClick(line) }
                } else {
                    Modifier
                }
            ),
        contentAlignment = when (config.visual.textAlign) {
            TextAlign.Start, TextAlign.Left -> Alignment.CenterStart
            TextAlign.End, TextAlign.Right -> Alignment.CenterEnd
            else -> Alignment.Center
        }
    ) {
        when (line) {
            is KaraokeLine -> {
                // Render karaoke line with syllables
                KaraokeSyllableRenderer(
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
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun KaraokeSyllableRenderer(
    line: KaraokeLine,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig,
    textStyle: TextStyle,
    baseColor: Color,
    shimmerProgress: Float = 0f
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    // Reset animation managers when config changes
    val animationManager = remember(config.animation) { AnimationStateManager() }
    val characterAnimator = remember(config.animation) { CharacterAnimationCalculator() }

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val maxWidthPx = with(density) { maxWidth.toPx() }

        // Calculate the total text to get dimensions
        val fullText = line.syllables.joinToString("") { it.content }

        // Calculate how many lines we need based on actual available width
        val wrappedContent = remember(fullText, textStyle, maxWidthPx) {
            calculateWrappedContentWithMaxWidth(line, textMeasurer, textStyle, maxWidthPx.toInt())
        }

        val textLayoutResult = remember(fullText, textStyle) {
            textMeasurer.measure(
                text = fullText,
                style = textStyle
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(density) {
                    val lineHeight = textLayoutResult.size.height
                    (wrappedContent.lineCount * lineHeight * 1.2f).toDp()
                })
    ) {
        val lineHeight = textLayoutResult.size.height.toFloat()
        val availableWidth = size.width

        // Calculate lines first to handle alignment
        val lines = mutableListOf<List<Pair<com.karaokelyrics.ui.core.models.KaraokeSyllable, Float>>>()
        var currentLineContent = mutableListOf<Pair<com.karaokelyrics.ui.core.models.KaraokeSyllable, Float>>()
        var currentLineWidth = 0f

        line.syllables.forEach { syllable ->
            val syllableLayout = textMeasurer.measure(syllable.content, textStyle)
            val syllableWidth = syllableLayout.size.width

            if (currentLineWidth + syllableWidth > availableWidth && currentLineContent.isNotEmpty()) {
                lines.add(currentLineContent)
                currentLineContent = mutableListOf()
                currentLineWidth = 0f
            }

            currentLineContent.add(syllable to currentLineWidth)
            currentLineWidth += syllableWidth

            if (syllable != line.syllables.last()) {
                val spaceLayout = textMeasurer.measure(" ", textStyle)
                currentLineWidth += spaceLayout.size.width
            }
        }
        if (currentLineContent.isNotEmpty()) {
            lines.add(currentLineContent)
        }

        // Now render with proper alignment
        var currentY = 0f

        lines.forEach { lineContent ->
            val totalLineWidth = lineContent.lastOrNull()?.let { (syl, offset) ->
                val syllableLayout = textMeasurer.measure(syl.content, textStyle)
                offset + syllableLayout.size.width
            } ?: 0f

            // Calculate starting X based on text alignment
            val startX = when (textStyle.textAlign) {
                TextAlign.Center -> (availableWidth - totalLineWidth) / 2f
                TextAlign.End, TextAlign.Right -> availableWidth - totalLineWidth
                else -> 0f
            }

            lineContent.forEach { (syllable, offsetInLine) ->
                var charX = startX + offsetInLine

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

                    // Calculate character color (can't use Composable here in Canvas)
                    val charColor = when {
                        currentTimeMs > charEndTime -> config.visual.playedTextColor
                        currentTimeMs >= charStartTime -> {
                            // Interpolate color during playing
                            val progress = if (charEndTime > charStartTime) {
                                ((currentTimeMs - charStartTime).toFloat() / (charEndTime - charStartTime))
                                    .coerceIn(0f, 1f)
                            } else 1f
                            com.karaokelyrics.ui.components.lerpColor(baseColor, config.visual.playingTextColor, progress)
                        }
                        else -> baseColor
                    }

                    // We'll apply shimmer as a gradient overlay instead of color modification

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
                                pivot = Offset(charX + charLayout.size.width / 2f, currentY + charLayout.size.height / 2f)
                            ) {
                                rotate(
                                    degrees = animState.rotation,
                                    pivot = Offset(charX + charLayout.size.width / 2f, currentY + charLayout.size.height / 2f)
                                ) {
                                    // Draw shadow if enabled
                                    if (config.visual.shadowEnabled) {
                                        drawText(
                                            textLayoutResult = charLayout,
                                            color = config.visual.shadowColor.copy(alpha = 0.3f),
                                            topLeft = Offset(
                                                charX + animState.offset.x + config.visual.shadowOffset.x,
                                                currentY + animState.offset.y + config.visual.shadowOffset.y
                                            )
                                        )
                                    }

                                    // Calculate character progress for effects
                                    val charProgress = if (charEndTime > charStartTime && currentTimeMs >= charStartTime) {
                                        ((currentTimeMs - charStartTime).toFloat() / (charEndTime - charStartTime))
                                            .coerceIn(0f, 1f)
                                    } else 0f

                                    // Draw glow layers if enabled
                                    if (config.visual.glowEnabled && charProgress > 0f) {
                                        // Draw multiple layers for glow effect
                                        val glowColor = config.visual.glowColor
                                        // Outer glow layer
                                        drawText(
                                            textLayoutResult = charLayout,
                                            color = glowColor.copy(alpha = 0.2f),
                                            topLeft = Offset(
                                                charX + animState.offset.x - 2,
                                                currentY + animState.offset.y - 2
                                            )
                                        )
                                        drawText(
                                            textLayoutResult = charLayout,
                                            color = glowColor.copy(alpha = 0.2f),
                                            topLeft = Offset(
                                                charX + animState.offset.x + 2,
                                                currentY + animState.offset.y + 2
                                            )
                                        )
                                        // Middle glow layer
                                        drawText(
                                            textLayoutResult = charLayout,
                                            color = glowColor.copy(alpha = 0.3f),
                                            topLeft = Offset(
                                                charX + animState.offset.x - 1,
                                                currentY + animState.offset.y - 1
                                            )
                                        )
                                        drawText(
                                            textLayoutResult = charLayout,
                                            color = glowColor.copy(alpha = 0.3f),
                                            topLeft = Offset(
                                                charX + animState.offset.x + 1,
                                                currentY + animState.offset.y + 1
                                            )
                                        )
                                    }

                                    // Apply gradient or shimmer if enabled
                                    when {
                                        config.animation.enableShimmer && shimmerProgress > 0f -> {
                                            // Use shimmer gradient for active characters
                                            val shimmerGradient = GradientFactory.createShimmerGradient(
                                                progress = (charX / size.width + shimmerProgress) % 1f,
                                                baseColor = charColor,
                                                shimmerColor = Color(
                                                    red = minOf(1f, charColor.red + 0.3f),
                                                    green = minOf(1f, charColor.green + 0.3f),
                                                    blue = minOf(1f, charColor.blue + 0.3f),
                                                    alpha = charColor.alpha
                                                ),
                                                width = charLayout.size.width.toFloat()
                                            )
                                            drawText(
                                                textLayoutResult = charLayout,
                                                brush = shimmerGradient,
                                                topLeft = Offset(charX + animState.offset.x, currentY + animState.offset.y)
                                            )
                                        }
                                        config.visual.gradientEnabled && charProgress > 0f -> {
                                            // Select gradient based on type
                                            val gradient = when (config.visual.gradientType) {
                                                com.karaokelyrics.ui.core.config.GradientType.PROGRESS -> {
                                                    GradientFactory.createProgressGradient(
                                                        progress = charProgress,
                                                        baseColor = baseColor,
                                                        highlightColor = config.visual.colors.active,
                                                        width = charLayout.size.width.toFloat()
                                                    )
                                                }
                                                com.karaokelyrics.ui.core.config.GradientType.MULTI_COLOR -> {
                                                    val colors = config.visual.playingGradientColors.takeIf { it.size > 1 }
                                                        ?: listOf(config.visual.colors.active, config.visual.colors.sung)
                                                    GradientFactory.createMultiColorGradient(
                                                        colors = colors,
                                                        angle = config.visual.gradientAngle,
                                                        width = charLayout.size.width.toFloat(),
                                                        height = charLayout.size.height.toFloat()
                                                    )
                                                }
                                                com.karaokelyrics.ui.core.config.GradientType.PRESET -> {
                                                    val presetColors = when (config.visual.gradientPreset) {
                                                        com.karaokelyrics.ui.core.config.GradientPreset.RAINBOW -> GradientFactory.Presets.Rainbow
                                                        com.karaokelyrics.ui.core.config.GradientPreset.SUNSET -> GradientFactory.Presets.Sunset
                                                        com.karaokelyrics.ui.core.config.GradientPreset.OCEAN -> GradientFactory.Presets.Ocean
                                                        com.karaokelyrics.ui.core.config.GradientPreset.FIRE -> GradientFactory.Presets.Fire
                                                        com.karaokelyrics.ui.core.config.GradientPreset.NEON -> GradientFactory.Presets.Neon
                                                        null -> listOf(config.visual.colors.active, config.visual.colors.sung)
                                                    }
                                                    GradientFactory.createMultiColorGradient(
                                                        colors = presetColors,
                                                        angle = config.visual.gradientAngle,
                                                        width = charLayout.size.width.toFloat(),
                                                        height = charLayout.size.height.toFloat()
                                                    )
                                                }
                                                else -> {
                                                    // Default linear gradient
                                                    GradientFactory.createLinearGradient(
                                                        colors = listOf(config.visual.colors.active, config.visual.colors.sung),
                                                        angle = config.visual.gradientAngle,
                                                        width = charLayout.size.width.toFloat(),
                                                        height = charLayout.size.height.toFloat()
                                                    )
                                                }
                                            }
                                            drawText(
                                                textLayoutResult = charLayout,
                                                brush = gradient,
                                                topLeft = Offset(charX + animState.offset.x, currentY + animState.offset.y)
                                            )
                                        }
                                        else -> {
                                            drawText(
                                                textLayoutResult = charLayout,
                                                color = charColor,
                                                topLeft = Offset(charX + animState.offset.x, currentY + animState.offset.y)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Draw without animation but with effects if enabled

                        // Draw shadow if enabled
                        if (config.visual.shadowEnabled) {
                            drawText(
                                textLayoutResult = charLayout,
                                color = config.visual.shadowColor.copy(alpha = 0.3f),
                                topLeft = Offset(
                                    charX + config.visual.shadowOffset.x,
                                    currentY + config.visual.shadowOffset.y
                                )
                            )
                        }

                        // Calculate character progress for effects
                        val charProgress = if (charEndTime > charStartTime && currentTimeMs >= charStartTime) {
                            ((currentTimeMs - charStartTime).toFloat() / (charEndTime - charStartTime))
                                .coerceIn(0f, 1f)
                        } else 0f

                        // Draw glow if enabled
                        if (config.visual.glowEnabled && charProgress > 0f) {
                            val glowColor = config.visual.glowColor
                            drawText(
                                textLayoutResult = charLayout,
                                color = glowColor.copy(alpha = 0.2f),
                                topLeft = Offset(charX - 2, currentY - 2)
                            )
                            drawText(
                                textLayoutResult = charLayout,
                                color = glowColor.copy(alpha = 0.2f),
                                topLeft = Offset(charX + 2, currentY + 2)
                            )
                        }

                        // Draw main text with appropriate effect
                        when {
                            config.animation.enableShimmer && shimmerProgress > 0f -> {
                                val shimmerGradient = GradientFactory.createShimmerGradient(
                                    progress = (charX / size.width + shimmerProgress) % 1f,
                                    baseColor = charColor,
                                    shimmerColor = Color(
                                        red = minOf(1f, charColor.red + 0.3f),
                                        green = minOf(1f, charColor.green + 0.3f),
                                        blue = minOf(1f, charColor.blue + 0.3f),
                                        alpha = charColor.alpha
                                    ),
                                    width = charLayout.size.width.toFloat()
                                )
                                drawText(
                                    textLayoutResult = charLayout,
                                    brush = shimmerGradient,
                                    topLeft = Offset(charX, currentY)
                                )
                            }
                            config.visual.gradientEnabled && charProgress > 0f -> {
                                val progressGradient = GradientFactory.createProgressGradient(
                                    progress = charProgress,
                                    baseColor = baseColor,
                                    highlightColor = config.visual.colors.active,
                                    width = charLayout.size.width.toFloat()
                                )
                                drawText(
                                    textLayoutResult = charLayout,
                                    brush = progressGradient,
                                    topLeft = Offset(charX, currentY)
                                )
                            }
                            else -> {
                                drawText(
                                    textLayoutResult = charLayout,
                                    color = charColor,
                                    topLeft = Offset(charX, currentY)
                                )
                            }
                        }
                    }

                    charX += charLayout.size.width
                }
            }

            // Move to next line
            currentY += lineHeight
        }
    }
    }
}

/**
 * Data class to hold wrapped content information.
 */
private data class WrappedContent(
    val lineCount: Int
)

/**
 * Calculate wrapped content based on actual available width.
 */
private fun calculateWrappedContentWithMaxWidth(
    line: KaraokeLine,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    textStyle: TextStyle,
    maxWidth: Int
): WrappedContent {
    val availableWidth = maxWidth.toFloat()

    var lineCount = 1
    var currentX = 0f

    line.syllables.forEach { syllable ->
        val syllableText = syllable.content
        val syllableLayout = textMeasurer.measure(syllableText, textStyle)

        // Check if syllable fits on current line
        if (currentX + syllableLayout.size.width > availableWidth && currentX > 0) {
            lineCount++
            currentX = 0f
        }

        currentX += syllableLayout.size.width

        // Add space after syllable if not last
        if (syllable != line.syllables.last()) {
            val spaceLayout = textMeasurer.measure(" ", textStyle)
            currentX += spaceLayout.size.width
        }
    }

    return WrappedContent(lineCount)
}

/**
 * Calculate wrapped content based on screen width and font size.
 * @deprecated Use calculateWrappedContentWithMaxWidth instead
 */
private fun calculateWrappedContent(
    line: KaraokeLine,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    textStyle: TextStyle,
    density: androidx.compose.ui.unit.Density
): WrappedContent {
    // Use actual screen width from density
    val screenWidthDp = 400.dp // Conservative estimate for mobile
    val availableWidth = with(density) { screenWidthDp.toPx() }

    var lineCount = 1
    var currentX = 0f

    line.syllables.forEach { syllable ->
        val syllableText = syllable.content
        val syllableLayout = textMeasurer.measure(syllableText, textStyle)

        // Check if syllable fits on current line
        if (currentX + syllableLayout.size.width > availableWidth && currentX > 0) {
            lineCount++
            currentX = 0f
        }

        currentX += syllableLayout.size.width

        // Add space after syllable if not last
        if (syllable != line.syllables.last()) {
            val spaceLayout = textMeasurer.measure(" ", textStyle)
            currentX += spaceLayout.size.width
        }
    }

    return WrappedContent(lineCount)
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