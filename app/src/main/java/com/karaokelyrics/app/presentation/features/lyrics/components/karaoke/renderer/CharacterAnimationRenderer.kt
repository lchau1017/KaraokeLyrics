package com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.karaokelyrics.app.data.util.TextLayoutCalculationUtil.SyllableLayout
import com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.animation.CharacterAnimationCalculator
import com.karaokelyrics.app.presentation.shared.rendering.SyllableRenderer

/**
 * Renders character animations for special words.
 * Single Responsibility: Character animation rendering only.
 */
class CharacterAnimationRenderer(
    private val syllableRenderer: SyllableRenderer,
    private val animationCalculator: CharacterAnimationCalculator
) {

    fun DrawScope.renderCharacterAnimation(
        syllableLayout: SyllableLayout,
        currentTimeMs: Int,
        drawColor: Color,
        enableBlurEffect: Boolean,
        animationStartTime: Int?
    ) {
        val wordAnimInfo = syllableLayout.wordAnimInfo ?: return
        val charLayouts = syllableLayout.charLayouts ?: return
        val charBounds = syllableLayout.charOriginalBounds ?: return

        syllableLayout.syllable.content.forEachIndexed { charIndex, _ ->
            val singleCharLayoutResult = charLayouts.getOrNull(charIndex) ?: return@forEachIndexed
            val charBox = charBounds.getOrNull(charIndex) ?: return@forEachIndexed

            // Calculate animation state for this character
            val animationState = animationCalculator.calculateCharacterAnimation(
                syllableLayout = syllableLayout,
                characterIndex = charIndex,
                currentTimeMs = currentTimeMs,
                animationStartTime = animationStartTime
            ) ?: return@forEachIndexed

            // Calculate character position
            val centeredOffsetX = (charBox.width - singleCharLayoutResult.size.width) / 2f
            val xPos = syllableLayout.position.x + charBox.left + centeredOffsetX
            val yPos = syllableLayout.position.y + charBox.top + animationState.effects.floatOffset

            // Combine blur effects
            val unplayedBlur = if (enableBlurEffect && !animationState.timing.shouldAnimate) 20f else 0f
            val blurRadius = maxOf(animationState.effects.blurRadius, unplayedBlur)

            val shadow = if (blurRadius > 0) {
                Shadow(
                    color = drawColor.copy(alpha = 0.4f),
                    offset = Offset(0f, 0f),
                    blurRadius = blurRadius
                )
            } else null

            // Render the animated character
            syllableRenderer.drawAnimatedCharacter(
                scope = this,
                textLayoutResult = singleCharLayoutResult,
                position = Offset(xPos, yPos),
                color = drawColor,
                scale = animationState.effects.scale,
                pivot = syllableLayout.wordPivot,
                shadow = shadow
            )
        }
    }
}