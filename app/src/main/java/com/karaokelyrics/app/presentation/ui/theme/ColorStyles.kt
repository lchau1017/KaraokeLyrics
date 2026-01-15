package com.karaokelyrics.app.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Centralized color styles for consistent theming across the app
 */
object ColorStyles {

    // Text Colors
    @Composable
    fun primaryText() = MaterialTheme.colorScheme.onSurface

    @Composable
    fun secondaryText() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun disabledText() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)

    @Composable
    fun hintText() = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

    // Surface Colors
    @Composable
    fun primarySurface() = MaterialTheme.colorScheme.surface

    @Composable
    fun secondarySurface() = MaterialTheme.colorScheme.surfaceVariant

    @Composable
    fun elevatedSurface() = MaterialTheme.colorScheme.surface

    // Interactive Colors
    @Composable
    fun selectedContainer() = MaterialTheme.colorScheme.secondaryContainer

    @Composable
    fun unselectedContainer() = MaterialTheme.colorScheme.surface

    @Composable
    fun selectedText() = MaterialTheme.colorScheme.onSecondaryContainer

    @Composable
    fun unselectedText() = MaterialTheme.colorScheme.onSurfaceVariant

    // Border Colors
    @Composable
    fun activeBorder() = MaterialTheme.colorScheme.primary

    @Composable
    fun inactiveBorder() = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

    // Control Colors
    @Composable
    fun controlBackground() = MaterialTheme.colorScheme.surface

    @Composable
    fun controlIcon() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun controlIconSelected() = MaterialTheme.colorScheme.primary

    // Switch Colors
    @Composable
    fun switchThumbOn() = MaterialTheme.colorScheme.primary

    @Composable
    fun switchTrackOn() = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)

    @Composable
    fun switchThumbOff() = MaterialTheme.colorScheme.outline

    @Composable
    fun switchTrackOff() = MaterialTheme.colorScheme.surfaceVariant

    // Slider Colors
    @Composable
    fun sliderThumb() = MaterialTheme.colorScheme.primary

    @Composable
    fun sliderActiveTrack() = MaterialTheme.colorScheme.primary

    @Composable
    fun sliderInactiveTrack() = MaterialTheme.colorScheme.surfaceVariant

    // Button Colors
    @Composable
    fun primaryButton() = MaterialTheme.colorScheme.primary

    @Composable
    fun onPrimaryButton() = MaterialTheme.colorScheme.onPrimary

    @Composable
    fun errorButton() = MaterialTheme.colorScheme.error

    @Composable
    fun errorButtonBorder() = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)

    // Chip Colors
    @Composable
    fun chipBackground() = MaterialTheme.colorScheme.surface

    @Composable
    fun chipBackgroundSelected() = MaterialTheme.colorScheme.primaryContainer

    @Composable
    fun chipText() = MaterialTheme.colorScheme.onSurface

    @Composable
    fun chipTextSelected() = MaterialTheme.colorScheme.onPrimaryContainer

    // Bottom Sheet Colors
    @Composable
    fun bottomSheetBackground() = MaterialTheme.colorScheme.surface

    @Composable
    fun bottomSheetDragHandle() = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)

    // Section Colors
    @Composable
    fun sectionTitle() = MaterialTheme.colorScheme.primary

    // Disabled Colors
    @Composable
    fun disabledBackground() = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    @Composable
    fun disabledBorder() = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
}