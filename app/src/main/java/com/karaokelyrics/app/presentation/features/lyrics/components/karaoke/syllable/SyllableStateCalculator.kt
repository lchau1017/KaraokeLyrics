package com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.syllable

import androidx.compose.ui.graphics.Color
import com.karaokelyrics.app.presentation.shared.layout.TextLayoutCalculationUtil.SyllableLayout

/**
 * Calculates state for individual syllables.
 * Single Responsibility: Syllable state calculation only.
 */
object SyllableStateCalculator {

    data class SyllableState(
        val isActive: Boolean,
        val isPast: Boolean,
        val isFuture: Boolean,
        val parentLineActive: Boolean,
        val drawColor: Color,
        val shouldBlur: Boolean
    )

    fun calculateSyllableState(
        syllableLayout: SyllableLayout,
        currentTimeMs: Int,
        rowLayouts: List<SyllableLayout>,
        activeColor: Color,
        inactiveColor: Color,
        enableBlurEffect: Boolean
    ): SyllableState {
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

        return SyllableState(
            isActive = isActive,
            isPast = isPast,
            isFuture = isFuture,
            parentLineActive = parentLineActive,
            drawColor = drawColor,
            shouldBlur = shouldBlur
        )
    }
}