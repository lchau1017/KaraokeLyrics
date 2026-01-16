package com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.renderer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.karaokelyrics.app.presentation.shared.layout.TextLayoutCalculationUtil.SyllableLayout
import com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.animation.CharacterAnimationCalculator
import com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.syllable.SyllableStateCalculator
import com.karaokelyrics.app.presentation.shared.animation.AnimationStateManager
import com.karaokelyrics.app.presentation.shared.rendering.GradientBrushFactory
import com.karaokelyrics.app.presentation.shared.rendering.SyllableRenderer

/**
 * Renders a row of karaoke syllables.
 * Single Responsibility: Row rendering only.
 */
class KaraokeRowRenderer(
    private val syllableRenderer: SyllableRenderer,
    private val animationStateManager: AnimationStateManager
) {
    private val characterAnimationRenderer = CharacterAnimationRenderer(
        syllableRenderer,
        CharacterAnimationCalculator()
    )

    fun DrawScope.renderRow(
        rowLayouts: List<SyllableLayout>,
        currentTimeMs: Int,
        activeColor: Color,
        inactiveColor: Color,
        enableCharacterAnimations: Boolean,
        enableBlurEffect: Boolean,
        isRtl: Boolean
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
            val syllableState = SyllableStateCalculator.calculateSyllableState(
                syllableLayout = syllableLayout,
                currentTimeMs = currentTimeMs,
                rowLayouts = rowLayouts,
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                enableBlurEffect = enableBlurEffect
            )

            // Get animation state
            val syllableKey = animationStateManager.createSyllableKey(
                syllableLayout.syllable.start,
                syllableLayout.syllable.end,
                syllableLayout.syllable.content
            )

            val animationStartTime = animationStateManager.getAnimationStartTime(
                syllableKey, currentTimeMs, syllableState.isActive
            )

            renderSyllable(
                syllableLayout = syllableLayout,
                syllableState = syllableState,
                currentTimeMs = currentTimeMs,
                index = index,
                rowLayouts = rowLayouts,
                animationStartTime = animationStartTime,
                enableCharacterAnimations = enableCharacterAnimations
            )
        }
    }

    private fun DrawScope.renderSyllable(
        syllableLayout: SyllableLayout,
        syllableState: SyllableStateCalculator.SyllableState,
        currentTimeMs: Int,
        index: Int,
        rowLayouts: List<SyllableLayout>,
        animationStartTime: Int?,
        enableCharacterAnimations: Boolean
    ) {
        when {
            // Character animation for special words
            enableCharacterAnimations &&
            syllableLayout.useAwesomeAnimation &&
            syllableLayout.wordAnimInfo != null -> {
                with(characterAnimationRenderer) {
                    renderCharacterAnimation(
                        syllableLayout = syllableLayout,
                        currentTimeMs = currentTimeMs,
                        drawColor = syllableState.drawColor,
                        enableBlurEffect = syllableState.shouldBlur,
                        animationStartTime = animationStartTime
                    )
                }
            }
            // Simple animation
            else -> {
                syllableRenderer.drawSimpleSyllable(
                    scope = this,
                    syllableLayout = syllableLayout,
                    currentTimeMs = currentTimeMs,
                    drawColor = syllableState.drawColor,
                    rowLayouts = rowLayouts,
                    index = index,
                    enableBlurEffect = syllableState.shouldBlur,
                    animationStartTime = animationStartTime
                )
            }
        }
    }
}