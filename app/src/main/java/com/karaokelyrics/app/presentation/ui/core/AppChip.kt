package com.karaokelyrics.app.presentation.ui.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Reusable Chip component with ViewData styling
 */
@Composable
fun AppChip(
    viewData: ChipViewData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (viewData.selected) {
        viewData.selectedBackgroundColor ?: MaterialTheme.colorScheme.primaryContainer
    } else {
        viewData.backgroundColor ?: MaterialTheme.colorScheme.surface
    }

    val contentColor = if (viewData.selected) {
        viewData.selectedContentColor ?: MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        viewData.contentColor ?: MaterialTheme.colorScheme.onSurface
    }

    val border = if (viewData.selected) {
        viewData.selectedBorder
    } else {
        viewData.border
    }

    val elevation = if (viewData.selected) {
        viewData.selectedElevation
    } else {
        viewData.elevation
    }

    Surface(
        modifier = modifier
            .clickable(enabled = viewData.enabled) { onClick() },
        color = backgroundColor,
        contentColor = contentColor,
        shape = viewData.shape,
        tonalElevation = elevation,
        border = border
    ) {
        Text(
            text = viewData.text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (viewData.selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

/**
 * Selection chip variant
 */
@Composable
fun AppSelectionChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    AppChip(
        viewData = ChipViewData.default(text, selected, enabled),
        onClick = onClick,
        modifier = modifier
    )
}

/**
 * Filter chip variant
 */
@Composable
fun AppFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppChip(
        viewData = ChipViewData.filter(text, selected),
        onClick = onClick,
        modifier = modifier
    )
}