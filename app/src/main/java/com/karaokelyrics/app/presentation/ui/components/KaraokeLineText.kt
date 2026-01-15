package com.karaokelyrics.app.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.karaokelyrics.app.domain.model.karaoke.KaraokeAlignment
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.presentation.ui.components.animation.AnimationCalculator
import com.karaokelyrics.app.presentation.ui.components.animation.rememberAnimationStateManager
import com.karaokelyrics.app.presentation.ui.components.rendering.GradientBrushFactory
import com.karaokelyrics.app.presentation.ui.components.rendering.SyllableRenderer
import com.karaokelyrics.app.presentation.ui.utils.*

/**
 * Refactored karaoke text component with better separation of concerns
 */
@Composable
fun KaraokeLineText(
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
    // Create a key that changes when colors change to force recomposition of cached states
    val colorKey = remember(activeColor, inactiveColor) {
        "${activeColor.value}-${inactiveColor.value}"
    }

    val animationStateManager = rememberAnimationStateManager()
    val syllableRenderer = remember { SyllableRenderer() }

    // Clear animations when colors change to prevent stale color caching
    LaunchedEffect(colorKey) {
        animationStateManager.clearAllAnimations()
    }

    // Determine text direction and alignment
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

    // Clean up old animations periodically
    LaunchedEffect(currentPosition) {
        if (currentPosition % 5000 == 0) { // Every 5 seconds
            animationStateManager.clearOldAnimations(currentPosition)
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
        // Include enableCharacterAnimations in cache key so layout recalculates when toggled
        val syllableLayouts = remember(line.syllables, textStyle, line.isAccompaniment, enableCharacterAnimations) {
            measureSyllablesAndDetermineAnimation(
                syllables = line.syllables,
                textMeasurer = textMeasurer,
                style = textStyle,
                isAccompanimentLine = line.isAccompaniment,
                spaceWidth = spaceWidth,
                enableCharacterAnimations = enableCharacterAnimations
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
            // Draw each row
            finalLayouts.forEachIndexed { _, rowLayouts ->
                drawKaraokeRow(
                    rowLayouts = rowLayouts,
                    currentTimeMs = currentPosition,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    enableCharacterAnimations = enableCharacterAnimations,
                    enableBlurEffect = enableBlurEffect,
                    isRtl = isRtl,
                    animationStateManager = animationStateManager,
                    syllableRenderer = syllableRenderer
                )
            }
        }
    }
}

/**
 * Extension function to draw a row of karaoke text
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawKaraokeRow(
    rowLayouts: List<SyllableLayout>,
    currentTimeMs: Int,
    activeColor: Color,
    inactiveColor: Color,
    enableCharacterAnimations: Boolean,
    enableBlurEffect: Boolean,
    isRtl: Boolean,
    animationStateManager: com.karaokelyrics.app.presentation.ui.components.animation.AnimationStateManager,
    syllableRenderer: SyllableRenderer
) {
    // Create gradient for this row
    val gradientBrush = GradientBrushFactory.createKaraokeGradient(
        lineLayouts = rowLayouts,
        currentTimeMs = currentTimeMs,
        isRtl = isRtl,
        activeColor = activeColor,
        inactiveColor = inactiveColor
    )

    rowLayouts.forEachIndexed { index, syllableLayout ->
        val isActive = currentTimeMs >= syllableLayout.syllable.start &&
                      currentTimeMs < syllableLayout.syllable.end
        val isPast = currentTimeMs >= syllableLayout.syllable.end
        val isFuture = currentTimeMs < syllableLayout.syllable.start

        // Check if parent line is active
        val parentLineActive = rowLayouts.any { layout ->
            currentTimeMs >= layout.syllable.start && currentTimeMs < layout.syllable.end
        }

        val drawColor = when {
            isActive -> activeColor
            isPast && parentLineActive -> activeColor
            else -> inactiveColor
        }

        val shouldBlur = enableBlurEffect && isFuture

        // Get animation state
        val syllableKey = animationStateManager.createSyllableKey(
            syllableLayout.syllable.start,
            syllableLayout.syllable.end,
            syllableLayout.syllable.content
        )

        val animationStartTime = animationStateManager.getAnimationStartTime(
            syllableKey, currentTimeMs, isActive
        )

        when {
            // Character animation for special words
            enableCharacterAnimations &&
            syllableLayout.useAwesomeAnimation &&
            syllableLayout.wordAnimInfo != null -> {
                drawCharacterAnimation(
                    syllableLayout = syllableLayout,
                    currentTimeMs = currentTimeMs,
                    drawColor = drawColor,
                    enableBlurEffect = shouldBlur,
                    animationStartTime = animationStartTime
                )
            }
            // Simple animation
            else -> {
                syllableRenderer.drawSimpleSyllable(
                    scope = this,
                    syllableLayout = syllableLayout,
                    currentTimeMs = currentTimeMs,
                    drawColor = drawColor,
                    rowLayouts = rowLayouts,
                    index = index,
                    enableBlurEffect = shouldBlur,
                    animationStartTime = animationStartTime
                )
            }
        }
    }
}

/**
 * Draw character animation with proper timing
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCharacterAnimation(
    syllableLayout: SyllableLayout,
    currentTimeMs: Int,
    drawColor: Color,
    enableBlurEffect: Boolean,
    animationStartTime: Int?
) {
    val wordAnimInfo = syllableLayout.wordAnimInfo ?: return
    val charLayouts = syllableLayout.charLayouts ?: emptyList()
    val charBounds = syllableLayout.charOriginalBounds ?: emptyList()

    syllableLayout.syllable.content.forEachIndexed { charIndex, _ ->
        val singleCharLayoutResult = charLayouts.getOrNull(charIndex) ?: return@forEachIndexed
        val charBox = charBounds.getOrNull(charIndex) ?: return@forEachIndexed

        val absoluteCharIndex = syllableLayout.charOffsetInWord + charIndex
        val numCharsInWord = wordAnimInfo.wordContent.length

        // Calculate animation timing
        val timing = AnimationCalculator.calculateCharacterTiming(
            characterIndex = absoluteCharIndex,
            totalCharacters = numCharsInWord,
            wordStartTime = wordAnimInfo.wordStartTime.toInt(),
            wordEndTime = wordAnimInfo.wordEndTime.toInt(),
            wordDuration = wordAnimInfo.wordDuration.toFloat(),
            currentTimeMs = currentTimeMs,
            animationStartTime = animationStartTime
        )

        // Calculate animation effects
        val effects = AnimationCalculator.calculateCharacterEffects(
            progress = timing.awesomeProgress,
            shouldAnimate = timing.shouldAnimate,
            wordDuration = wordAnimInfo.wordDuration.toFloat(),
            numCharsInWord = numCharsInWord
        )

        // Apply effects
        val centeredOffsetX = (charBox.width - singleCharLayoutResult.size.width) / 2f
        val xPos = syllableLayout.position.x + charBox.left + centeredOffsetX
        val yPos = syllableLayout.position.y + charBox.top + effects.floatOffset

        // Combine blur effects
        val unplayedBlur = if (enableBlurEffect) 20f else 0f  // Increased for consistency
        val blurRadius = maxOf(effects.blurRadius, unplayedBlur)

        val shadow = if (blurRadius > 0) {
            Shadow(
                color = drawColor.copy(0.4f),
                offset = Offset(0f, 0f),
                blurRadius = blurRadius
            )
        } else null

        val renderer = SyllableRenderer()
        renderer.drawAnimatedCharacter(
            scope = this,
            textLayoutResult = singleCharLayoutResult,
            position = Offset(xPos, yPos),
            color = drawColor,
            scale = effects.scale,
            pivot = syllableLayout.wordPivot,
            shadow = shadow
        )
    }
}