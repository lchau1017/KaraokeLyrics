package com.karaokelyrics.app.presentation.ui.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.karaokelyrics.app.domain.model.karaoke.KaraokeAlignment
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.presentation.ui.utils.DipAndRise
import com.karaokelyrics.app.presentation.ui.utils.Swell
import com.karaokelyrics.app.presentation.ui.utils.Bounce
import com.karaokelyrics.app.presentation.ui.utils.isPunctuation
import com.karaokelyrics.app.presentation.ui.utils.isRtl
import com.karaokelyrics.app.presentation.ui.utils.SyllableLayout
import com.karaokelyrics.app.presentation.ui.utils.measureSyllablesAndDetermineAnimation
import com.karaokelyrics.app.presentation.ui.utils.calculateGreedyWrappedLines
import com.karaokelyrics.app.presentation.ui.utils.calculateStaticLineLayout

@Composable
fun KaraokeLineTextMultiLine(
    line: KaraokeLine,
    currentPosition: Int,
    textStyle: TextStyle,
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.White.copy(alpha = 0.3f),
    modifier: Modifier = Modifier,
    enableCharacterAnimations: Boolean = true,
    enableBlurEffect: Boolean = true
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val isRtl = remember(line.syllables) {
        line.syllables.any { it.content.isRtl() }
    }

    val isRightAligned = remember(line.alignment, isRtl) {
        when (line.alignment) {
            KaraokeAlignment.Start, KaraokeAlignment.Unspecified -> isRtl
            KaraokeAlignment.End -> !isRtl
            KaraokeAlignment.Center -> false
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        contentAlignment = when {
            line.alignment == KaraokeAlignment.Center -> Alignment.Center
            isRightAligned -> Alignment.CenterEnd
            else -> Alignment.CenterStart
        }
    ) {
        val availableWidthPx = with(density) { maxWidth.toPx() }
        val lineHeight = with(density) { (textStyle.fontSize.toPx() * 1.5f) }

        // Measure space width
        val spaceWidth = remember(textMeasurer, textStyle) {
            textMeasurer.measure(" ", textStyle).size.width.toFloat()
        }

        // Measure and layout syllables
        val syllableLayouts = remember(line.syllables, textStyle, line.isAccompaniment) {
            measureSyllablesAndDetermineAnimation(
                syllables = line.syllables,
                textMeasurer = textMeasurer,
                style = textStyle,
                isAccompanimentLine = line.isAccompaniment,
                spaceWidth = spaceWidth
            )
        }

        // Calculate wrapped lines
        val wrappedLines = remember(syllableLayouts, availableWidthPx) {
            calculateGreedyWrappedLines(
                syllableLayouts = syllableLayouts,
                availableWidthPx = availableWidthPx,
                textMeasurer = textMeasurer,
                style = textStyle
            )
        }

        // Calculate final layout with positions
        val finalLayouts = remember(wrappedLines, availableWidthPx, lineHeight, isRtl, isRightAligned) {
            calculateStaticLineLayout(
                wrappedLines = wrappedLines,
                isLineRightAligned = isRightAligned,
                canvasWidth = availableWidthPx,
                lineHeight = lineHeight,
                isRtl = isRtl
            )
        }

        val totalHeight = wrappedLines.size.toFloat() * lineHeight

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(density) { totalHeight.toDp() })
        ) {
            // Create gradient brush for karaoke effect
            val gradientBrush = createLineGradientBrush(
                lineLayouts = finalLayouts.flatten(),
                currentTimeMs = currentPosition,
                isRtl = isRtl
            )

            // Draw each row
            finalLayouts.forEachIndexed { rowIndex, rowLayouts ->
                drawKaraokeRow(
                    rowLayouts = rowLayouts,
                    currentTimeMs = currentPosition,
                    gradientBrush = gradientBrush,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    enableCharacterAnimations = enableCharacterAnimations,
                    enableBlurEffect = enableBlurEffect
                )
            }
        }
    }
}

private fun DrawScope.drawKaraokeRow(
    rowLayouts: List<SyllableLayout>,
    currentTimeMs: Int,
    gradientBrush: Brush,
    activeColor: Color,
    inactiveColor: Color,
    enableCharacterAnimations: Boolean,
    enableBlurEffect: Boolean
) {
    rowLayouts.forEachIndexed { index, syllableLayout ->
        val syllableProgress = syllableLayout.syllable.progress(currentTimeMs)
        val isActive = currentTimeMs >= syllableLayout.syllable.start &&
                      currentTimeMs < syllableLayout.syllable.end
        val isPast = currentTimeMs >= syllableLayout.syllable.end
        val isFuture = currentTimeMs < syllableLayout.syllable.start

        // Only show as active if we're within the parent line's time range
        // This prevents words from staying bright after the line finishes
        val parentLineActive = rowLayouts.any { layout ->
            currentTimeMs >= layout.syllable.start && currentTimeMs < layout.syllable.end
        }

        val drawColor = when {
            isActive -> activeColor // Currently being sung
            isPast && parentLineActive -> activeColor   // Already sung but line still active
            else -> inactiveColor   // Not yet sung or line is done
        }

        // Only blur future lyrics, not current or past
        val shouldBlur = enableBlurEffect && isFuture

        when {
            // Character animation for awesome words
            enableCharacterAnimations && syllableLayout.useAwesomeAnimation && syllableLayout.wordAnimInfo != null -> {
                drawCharacterAnimation(
                    syllableLayout = syllableLayout,
                    currentTimeMs = currentTimeMs,
                    drawColor = drawColor,
                    enableBlurEffect = shouldBlur,
                    isActive = isActive,
                    parentLineActive = parentLineActive
                )
            }
            // Simple animation
            else -> {
                drawSimpleAnimation(
                    syllableLayout = syllableLayout,
                    currentTimeMs = currentTimeMs,
                    drawColor = drawColor,
                    rowLayouts = rowLayouts,
                    index = index,
                    enableBlurEffect = shouldBlur,
                    isActive = isActive,
                    parentLineActive = parentLineActive
                )
            }
        }
    }
}

private fun DrawScope.drawCharacterAnimation(
    syllableLayout: SyllableLayout,
    currentTimeMs: Int,
    drawColor: Color,
    enableBlurEffect: Boolean = false,
    isActive: Boolean = false,
    parentLineActive: Boolean = false
) {
    val wordAnimInfo = syllableLayout.wordAnimInfo ?: return
    val fastCharAnimationThresholdMs = 200f
    val awesomeDuration = wordAnimInfo.wordDuration * 0.8f

    val charLayouts = syllableLayout.charLayouts ?: emptyList()
    val charBounds = syllableLayout.charOriginalBounds ?: emptyList()

    syllableLayout.syllable.content.forEachIndexed { charIndex, _ ->
        val singleCharLayoutResult = charLayouts.getOrNull(charIndex) ?: return@forEachIndexed
        val charBox = charBounds.getOrNull(charIndex) ?: return@forEachIndexed

        val absoluteCharIndex = syllableLayout.charOffsetInWord + charIndex
        val numCharsInWord = wordAnimInfo.wordContent.length
        val earliestStartTime = wordAnimInfo.wordStartTime
        val latestStartTime = wordAnimInfo.wordEndTime - awesomeDuration.toLong()

        val charRatio = if (numCharsInWord > 1) {
            absoluteCharIndex.toFloat() / (numCharsInWord - 1)
        } else {
            0.5f
        }

        val awesomeStartTime = (earliestStartTime + (latestStartTime - earliestStartTime) * charRatio).toLong()

        // Only animate if this specific character's animation window is active
        val timeSinceStart = currentTimeMs - awesomeStartTime

        // Check if we're within this specific character's animation window
        val isCharAnimationActive = timeSinceStart >= 0 && timeSinceStart <= awesomeDuration

        // Strict check: only animate if this exact syllable is currently active or just finished
        // This prevents other lines with similar timing from animating
        val isSyllableActive = currentTimeMs >= syllableLayout.syllable.start &&
                              currentTimeMs <= syllableLayout.syllable.end + 500 // Small buffer for animation completion

        // Additional check: make sure we're not animating if a different word is playing
        val isCorrectWord = wordAnimInfo.wordStartTime <= currentTimeMs &&
                           wordAnimInfo.wordEndTime + 500 >= currentTimeMs

        val shouldAnimate = isCharAnimationActive && isSyllableActive && isCorrectWord

        val awesomeProgress = when {
            shouldAnimate -> {
                ((currentTimeMs - awesomeStartTime).toFloat() / awesomeDuration).coerceIn(0f, 1f)
            }
            timeSinceStart > awesomeDuration && isSyllableActive -> {
                1f // Animation completed but syllable still active
            }
            else -> {
                0f // Animation not started or wrong syllable
            }
        }

        // Improved animation with smoother curves
        val floatOffset = if (shouldAnimate && awesomeProgress < 1f) {
            // Increased float effect for more dramatic animation
            6f * DipAndRise(
                dip = ((0.6 * (wordAnimInfo.wordDuration - fastCharAnimationThresholdMs * numCharsInWord) / 1000))
                    .coerceIn(0.0, 0.6)
            ).transform(1.0f - awesomeProgress)
        } else {
            0f // No floating effect when not animating
        }

        val scale = if (shouldAnimate && awesomeProgress < 1f) {
            // Slightly more pronounced scale effect
            1f + Swell(
                (0.15 * (wordAnimInfo.wordDuration - fastCharAnimationThresholdMs * numCharsInWord) / 1000)
                    .coerceIn(0.0, 0.15)
            ).transform(awesomeProgress)
        } else {
            1f // No scaling when not animating
        }

        val centeredOffsetX = (charBox.width - singleCharLayoutResult.size.width) / 2f
        val xPos = syllableLayout.position.x + charBox.left + centeredOffsetX
        val yPos = syllableLayout.position.y + charBox.top + floatOffset

        // Apply blur for unplayed text or animation blur
        val animationBlur = if (shouldAnimate && awesomeProgress < 1f) {
            // Enhanced blur effect during animation
            12f * Bounce.transform(awesomeProgress)
        } else {
            0f
        }
        val unplayedBlur = if (enableBlurEffect) 8f else 0f
        val blurRadius = maxOf(animationBlur, unplayedBlur)

        val shadow = Shadow(
            color = drawColor.copy(0.4f),
            offset = Offset(0f, 0f),
            blurRadius = blurRadius
        )

        withTransform({ scale(scale = scale, pivot = syllableLayout.wordPivot) }) {
            drawText(
                textLayoutResult = singleCharLayoutResult,
                color = drawColor,
                topLeft = Offset(xPos, yPos),
                shadow = shadow
            )
        }
    }
}

private fun DrawScope.drawSimpleAnimation(
    syllableLayout: SyllableLayout,
    currentTimeMs: Int,
    drawColor: Color,
    rowLayouts: List<SyllableLayout>,
    index: Int,
    enableBlurEffect: Boolean = false,
    isActive: Boolean = false,
    parentLineActive: Boolean = false
) {
    val driverLayout = if (syllableLayout.syllable.content.trim().isPunctuation()) {
        var searchIndex = index - 1
        while (searchIndex >= 0) {
            val candidate = rowLayouts[searchIndex]
            if (!candidate.syllable.content.trim().isPunctuation()) {
                break
            }
            searchIndex--
        }
        if (searchIndex < 0) syllableLayout else rowLayouts[searchIndex]
    } else {
        syllableLayout
    }

    // Only animate if this syllable is active or was recently active (within animation duration)
    val animationFixedDuration = 700f
    val timeSinceStart = currentTimeMs - driverLayout.syllable.start

    // Only animate if we're within the animation window AND the parent line is active
    val shouldAnimate = parentLineActive && timeSinceStart >= 0 && timeSinceStart <= animationFixedDuration
    val animationProgress = if (shouldAnimate) {
        (timeSinceStart / animationFixedDuration).coerceIn(0f, 1f)
    } else {
        1f // No animation, fully settled
    }

    val maxOffsetY = 4f
    val floatCurveValue = if (shouldAnimate) {
        CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f).transform(1f - animationProgress)
    } else {
        0f // No floating effect for inactive syllables
    }
    val floatOffset = maxOffsetY * floatCurveValue

    val finalPosition = syllableLayout.position.copy(
        y = syllableLayout.position.y + floatOffset
    )

    // Apply blur effect for unplayed text
    if (enableBlurEffect) {
        val shadow = Shadow(
            color = drawColor.copy(alpha = 0.4f),
            offset = Offset(0f, 0f),
            blurRadius = 8f
        )
        drawText(
            textLayoutResult = syllableLayout.textLayoutResult,
            color = drawColor,
            topLeft = finalPosition,
            shadow = shadow
        )
    } else {
        drawText(
            textLayoutResult = syllableLayout.textLayoutResult,
            color = drawColor,
            topLeft = finalPosition
        )
    }
}

private fun createLineGradientBrush(
    lineLayouts: List<SyllableLayout>,
    currentTimeMs: Int,
    isRtl: Boolean
): Brush {
    val activeColor = Color.White
    val inactiveColor = Color.White.copy(alpha = 0.3f)

    if (lineLayouts.isEmpty()) {
        return Brush.horizontalGradient(colors = listOf(inactiveColor, inactiveColor))
    }

    val totalMinX = lineLayouts.minOf { it.position.x }
    val totalMaxX = lineLayouts.maxOf { it.position.x + it.width }
    val totalWidth = totalMaxX - totalMinX

    if (totalWidth <= 0f) {
        val isFinished = currentTimeMs >= lineLayouts.last().syllable.end
        val color = if (isFinished) activeColor else inactiveColor
        return Brush.horizontalGradient(listOf(color, color))
    }

    val firstSyllableStart = lineLayouts.first().syllable.start
    val lastSyllableEnd = lineLayouts.last().syllable.end

    // Before the line starts - all inactive
    if (currentTimeMs < firstSyllableStart) {
        return Brush.horizontalGradient(listOf(inactiveColor, inactiveColor))
    }

    // After the line ends - all inactive (not active anymore)
    if (currentTimeMs >= lastSyllableEnd) {
        return Brush.horizontalGradient(listOf(inactiveColor, inactiveColor))
    }

    // Find the currently active syllable
    val activeSyllableLayout = lineLayouts.find {
        currentTimeMs >= it.syllable.start && currentTimeMs < it.syllable.end
    }

    val currentPixelPosition = when {
        activeSyllableLayout != null -> {
            // We're in a syllable - calculate position within it
            val syllableProgress = activeSyllableLayout.syllable.progress(currentTimeMs)
            if (isRtl) {
                activeSyllableLayout.position.x + activeSyllableLayout.width * (1f - syllableProgress)
            } else {
                activeSyllableLayout.position.x + activeSyllableLayout.width * syllableProgress
            }
        }
        else -> {
            // Between syllables - find the last completed one
            val lastFinished = lineLayouts.lastOrNull { currentTimeMs >= it.syllable.end }
            if (lastFinished != null) {
                // Position at the end of the last finished syllable
                if (isRtl) {
                    lastFinished.position.x
                } else {
                    lastFinished.position.x + lastFinished.width
                }
            } else {
                // No syllable finished yet
                if (isRtl) totalMaxX else totalMinX
            }
        }
    }

    val progress = (currentPixelPosition - totalMinX) / totalWidth
    val fadeWidth = 0.02f // Sharper transition

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
        startX = totalMinX,
        endX = totalMaxX
    )
}