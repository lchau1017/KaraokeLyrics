package com.karaokelyrics.app.presentation.features.settings.viewdata

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/**
 * ViewData for SettingsBottomSheet styling
 */
data class SettingsBottomSheetViewData(
    val containerColor: Color,
    val contentColor: Color,
    val dragHandleColor: Color,
    val titleStyle: TextStyle,
    val titleColor: Color,
    val sectionTitleStyle: TextStyle,
    val sectionTitleColor: Color,
    val labelStyle: TextStyle,
    val labelColor: Color,
    val secondaryLabelColor: Color,
    val disabledTextColor: Color,
    val switchColors: SwitchColorsViewData,
    val errorButtonColor: Color,
    val errorButtonBorderColor: Color,
    val surfaceColor: Color,
    val surfaceVariantColor: Color
) {
    companion object {
        @Composable
        fun default() = SettingsBottomSheetViewData(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            dragHandleColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            titleStyle = MaterialTheme.typography.headlineSmall,
            titleColor = MaterialTheme.colorScheme.onSurface,
            sectionTitleStyle = MaterialTheme.typography.titleMedium,
            sectionTitleColor = MaterialTheme.colorScheme.primary,
            labelStyle = MaterialTheme.typography.bodyLarge,
            labelColor = MaterialTheme.colorScheme.onSurface,
            secondaryLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            switchColors = SwitchColorsViewData.default(),
            errorButtonColor = MaterialTheme.colorScheme.error,
            errorButtonBorderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
            surfaceColor = MaterialTheme.colorScheme.surface,
            surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    }
}

data class SwitchColorsViewData(
    val checkedThumbColor: Color,
    val checkedTrackColor: Color,
    val uncheckedThumbColor: Color,
    val uncheckedTrackColor: Color,
    val disabledCheckedThumbColor: Color,
    val disabledCheckedTrackColor: Color,
    val disabledUncheckedThumbColor: Color,
    val disabledUncheckedTrackColor: Color
) {
    companion object {
        @Composable
        fun default() = SwitchColorsViewData(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledCheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledCheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f),
            disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledUncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f)
        )
    }
}
