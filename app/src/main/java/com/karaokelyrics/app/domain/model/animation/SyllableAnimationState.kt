package com.karaokelyrics.app.domain.model.animation

import androidx.compose.ui.geometry.Offset

/**
 * Animation-specific state for a syllable.
 * Separated from layout concerns following SRP.
 */
data class SyllableAnimationState(
    val config: AnimationConfig,
    val progress: Float,
    val pivot: Offset,
    val characterStates: List<CharacterAnimationState>? = null
)

/**
 * Animation state for individual characters.
 */
data class CharacterAnimationState(
    val character: Char,
    val index: Int,
    val offset: Offset,
    val rotation: Float = 0f,
    val scale: Float = 1f,
    val alpha: Float = 1f
)

/**
 * Animation state for an entire line.
 */
data class LineAnimationState(
    val syllableStates: List<SyllableAnimationState>,
    val lineProgress: Float,
    val isActive: Boolean
)