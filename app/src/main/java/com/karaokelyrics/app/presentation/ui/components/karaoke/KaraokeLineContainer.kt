package com.karaokelyrics.app.presentation.ui.components.karaoke

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.karaokelyrics.app.domain.model.karaoke.KaraokeAlignment
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.presentation.ui.utils.TextUtils.isRtl
import com.karaokelyrics.app.data.util.TextLayoutCalculationUtil
import com.karaokelyrics.app.data.util.TextLayoutCalculationUtil.LineLayout
import com.karaokelyrics.app.data.util.TextLayoutCalculationUtil.SyllableLayout
import com.karaokelyrics.app.presentation.ui.helper.TextCharacteristicsProcessor
import com.karaokelyrics.app.domain.usecase.GroupSyllablesIntoWordsUseCase
import com.karaokelyrics.app.domain.usecase.DetermineAnimationTypeUseCase
import com.karaokelyrics.app.presentation.ui.components.animation.AnimationCalculator
import com.karaokelyrics.app.presentation.ui.components.animation.rememberAnimationStateManager
import com.karaokelyrics.app.presentation.ui.components.rendering.GradientBrushFactory
import com.karaokelyrics.app.presentation.ui.components.rendering.SyllableRenderer

/**
 * STATEFUL container for karaoke line with full animation support.
 */
@Composable
fun KaraokeLineContainer(
    line: KaraokeLine,
    currentTimeMs: Int,
    textStyle: TextStyle,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier,
    enableCharacterAnimations: Boolean = true,
    enableBlurEffect: Boolean = true
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val animationStateManager = rememberAnimationStateManager()
    val syllableRenderer = remember { SyllableRenderer() }

    // Key for cache invalidation when visual settings change
    val visualKey = remember(activeColor, inactiveColor, enableBlurEffect) {
        "${activeColor.value}-${inactiveColor.value}-$enableBlurEffect"
    }

    // Clear animations when visual settings change
    LaunchedEffect(visualKey) {
        animationStateManager.clearAllAnimations()
    }

    // Determine text direction
    val isRtl = remember(line.syllables) {
        line.syllables.any { it.content.isRtl() }
    }

    val alignment = remember(line.alignment, isRtl) {
        when (line.alignment) {
            KaraokeAlignment.Center -> Alignment.Center
            KaraokeAlignment.Start, KaraokeAlignment.Unspecified -> {
                if (isRtl) Alignment.CenterEnd else Alignment.CenterStart
            }
            KaraokeAlignment.End -> {
                if (isRtl) Alignment.CenterStart else Alignment.CenterEnd
            }
        }
    }

    // Clean up old animations periodically
    LaunchedEffect(currentTimeMs) {
        if (currentTimeMs % 5000 == 0) {
            animationStateManager.clearOldAnimations(currentTimeMs)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        contentAlignment = alignment
    ) {
        val availableWidthPx = with(density) { maxWidth.toPx() }
        val lineHeight = with(density) { textStyle.fontSize.toPx() * 1.5f }

        // Calculate layout (memoized)
        val layout = rememberLineLayout(
            line = line,
            textStyle = textStyle,
            availableWidthPx = availableWidthPx,
            lineHeight = lineHeight,
            textMeasurer = textMeasurer,
            enableCharacterAnimations = enableCharacterAnimations,
            isRtl = isRtl
        )

        // Render with full animation support
        // Key the Canvas to force recomposition when blur setting changes
        key(enableBlurEffect) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { layout.totalHeight.toDp() })
            ) {
            // Draw each row with animations
            layout.syllableLayouts.forEachIndexed { _, rowLayouts ->
                drawKaraokeRow(
                    rowLayouts = rowLayouts,
                    currentTimeMs = currentTimeMs,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    enableCharacterAnimations = enableCharacterAnimations,
                    enableBlurEffect = enableBlurEffect,
                    isRtl = isRtl,
                    animationStateManager = animationStateManager,
                    syllableRenderer = syllableRenderer
                )
            }
            } // End of Canvas
        } // End of key block
    }
}

/**
 * Extension function to draw a row of karaoke text with animations
 */
private fun DrawScope.drawKaraokeRow(
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
                    animationStartTime = animationStartTime,
                    syllableRenderer = syllableRenderer
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
private fun DrawScope.drawCharacterAnimation(
    syllableLayout: SyllableLayout,
    currentTimeMs: Int,
    drawColor: Color,
    enableBlurEffect: Boolean,
    animationStartTime: Int?,
    syllableRenderer: SyllableRenderer
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
        val unplayedBlur = if (enableBlurEffect) 20f else 0f
        val blurRadius = maxOf(effects.blurRadius, unplayedBlur)

        val shadow = if (blurRadius > 0) {
            Shadow(
                color = drawColor.copy(0.4f),
                offset = Offset(0f, 0f),
                blurRadius = blurRadius
            )
        } else null

        syllableRenderer.drawAnimatedCharacter(
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

/**
 * Memoized layout calculation
 */
@Composable
private fun rememberLineLayout(
    line: KaraokeLine,
    textStyle: TextStyle,
    availableWidthPx: Float,
    lineHeight: Float,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    enableCharacterAnimations: Boolean,
    isRtl: Boolean
): LineLayout {
    return remember(line, textStyle, availableWidthPx, lineHeight, enableCharacterAnimations) {
        // Create use cases locally - they're stateless
        val groupSyllablesUseCase = GroupSyllablesIntoWordsUseCase()
        val determineAnimationUseCase = DetermineAnimationTypeUseCase()
        val textProcessor = TextCharacteristicsProcessor(
            groupSyllablesUseCase,
            determineAnimationUseCase
        )

        calculateLayout(
            line = line,
            textMeasurer = textMeasurer,
            textStyle = textStyle,
            textProcessor = textProcessor,
            availableWidthPx = availableWidthPx,
            lineHeight = lineHeight,
            enableCharacterAnimations = enableCharacterAnimations,
            isRtl = isRtl
        )
    }
}

/**
 * Pure function to calculate layout
 */
private fun calculateLayout(
    line: KaraokeLine,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    textStyle: TextStyle,
    textProcessor: TextCharacteristicsProcessor,
    availableWidthPx: Float,
    lineHeight: Float,
    enableCharacterAnimations: Boolean,
    isRtl: Boolean
): LineLayout {
    // Process syllables
    val syllableLayouts = textProcessor.processSyllables(
        syllables = line.syllables,
        textMeasurer = textMeasurer,
        style = textStyle,
        enableCharacterAnimations = enableCharacterAnimations,
        isAccompanimentLine = line.isAccompaniment
    )

    // Wrap into lines
    val wrappedLines = TextLayoutCalculationUtil.calculateGreedyWrappedLines(
        syllableLayouts = syllableLayouts,
        availableWidthPx = availableWidthPx,
        textMeasurer = textMeasurer,
        style = textStyle
    )

    // Calculate final positions
    val isRightAligned = when (line.alignment) {
        KaraokeAlignment.Start -> isRtl
        KaraokeAlignment.End -> !isRtl
        else -> false
    }

    val positionedLayouts = TextLayoutCalculationUtil.calculateStaticLineLayout(
        wrappedLines = wrappedLines,
        isLineRightAligned = isRightAligned,
        canvasWidth = availableWidthPx,
        lineHeight = lineHeight,
        isRtl = isRtl
    )

    // Create LineLayout structure
    val totalHeight = positionedLayouts.size * lineHeight

    return LineLayout(
        syllableLayouts = positionedLayouts,
        totalHeight = totalHeight,
        lineHeight = lineHeight,
        rows = positionedLayouts.size
    )
}