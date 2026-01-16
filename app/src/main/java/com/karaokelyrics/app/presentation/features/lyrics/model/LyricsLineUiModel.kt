package com.karaokelyrics.app.presentation.features.lyrics.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.karaokelyrics.app.domain.model.ISyncedLine

/**
 * UI Model containing all pre-calculated values for a lyrics line.
 * This follows the principle of "Smart ViewModels, Dumb Views".
 * All calculations are done in the ViewModel/Mapper layer.
 */
@Immutable
data class LyricsLineUiModel(
    val line: ISyncedLine,
    val index: Int,
    val visualState: VisualState,
    val animationState: AnimationState,
    val interactionState: InteractionState
)

/**
 * Visual properties for the line (pre-calculated)
 */
@Immutable
data class VisualState(
    val opacity: Float,
    val scale: Float,
    val blur: Float,
    val color: Color,
    val textStyle: TextStyle
)

/**
 * Animation state for the line
 */
@Immutable
data class AnimationState(
    val isActive: Boolean,
    val isPast: Boolean,
    val isUpcoming: Boolean,
    val progress: Float, // 0.0 to 1.0
    val distanceFromCurrent: Int,
    val timeSincePlayed: Long,
    val timeUntilPlay: Long
)

/**
 * Interaction state
 */
@Immutable
data class InteractionState(
    val isClickable: Boolean,
    val isHighlighted: Boolean,
    val isFocused: Boolean
)

/**
 * Container for all lyrics lines with metadata
 */
@Immutable
data class LyricsUiState(
    val lines: List<LyricsLineUiModel>,
    val focusedLineIndex: Int = -1,
    val allFocusedIndices: List<Int> = emptyList(),
    val scrollTargetIndex: Int? = null,
    val currentTimeMs: Int = 0
)