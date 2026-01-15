package com.karaokelyrics.app.presentation.ui.components.rendering

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import com.karaokelyrics.app.presentation.ui.components.animation.AnimationCalculator
import com.karaokelyrics.app.presentation.ui.utils.SyllableLayout
import com.karaokelyrics.app.presentation.ui.utils.isPunctuation
import androidx.compose.animation.core.CubicBezierEasing

/**
 * Handles rendering of individual syllables with various effects
 */
class SyllableRenderer {

    /**
     * Render a syllable with simple animation
     */
    fun drawSimpleSyllable(
        scope: DrawScope,
        syllableLayout: SyllableLayout,
        currentTimeMs: Int,
        drawColor: Color,
        rowLayouts: List<SyllableLayout>,
        index: Int,
        enableBlurEffect: Boolean,
        animationStartTime: Int?
    ) = with(scope) {
        // Find driver layout for punctuation
        val driverLayout = findDriverLayout(syllableLayout, rowLayouts, index)

        val progress = AnimationCalculator.calculateSimpleAnimationProgress(
            driverLayout.syllable.start,
            currentTimeMs,
            animationStartTime
        )

        // Calculate float effect
        val maxOffsetY = 4f
        val floatCurveValue = if (progress < 1f) {
            CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f).transform(1f - progress)
        } else {
            0f
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

    /**
     * Render a character with animation
     */
    fun drawAnimatedCharacter(
        scope: DrawScope,
        textLayoutResult: TextLayoutResult,
        position: Offset,
        color: Color,
        scale: Float,
        pivot: Offset,
        shadow: Shadow? = null
    ) = with(scope) {
        if (scale != 1f) {
            withTransform({ scale(scale = scale, pivot = pivot) }) {
                drawText(
                    textLayoutResult = textLayoutResult,
                    color = color,
                    topLeft = position,
                    shadow = shadow
                )
            }
        } else {
            drawText(
                textLayoutResult = textLayoutResult,
                color = color,
                topLeft = position,
                shadow = shadow
            )
        }
    }

    private fun findDriverLayout(
        syllableLayout: SyllableLayout,
        rowLayouts: List<SyllableLayout>,
        index: Int
    ): SyllableLayout {
        if (!syllableLayout.syllable.content.trim().isPunctuation()) {
            return syllableLayout
        }

        // For punctuation, find the previous non-punctuation syllable
        var searchIndex = index - 1
        while (searchIndex >= 0) {
            val candidate = rowLayouts[searchIndex]
            if (!candidate.syllable.content.trim().isPunctuation()) {
                return candidate
            }
            searchIndex--
        }

        return syllableLayout
    }
}