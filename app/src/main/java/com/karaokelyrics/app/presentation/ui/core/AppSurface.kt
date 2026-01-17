package com.karaokelyrics.app.presentation.ui.core

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Reusable Surface component with ViewData styling
 */
@Composable
fun AppSurface(viewData: SurfaceViewData, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier,
        color = viewData.backgroundColor ?: MaterialTheme.colorScheme.surface,
        contentColor = viewData.contentColor ?: MaterialTheme.colorScheme.onSurface,
        shape = viewData.shape,
        shadowElevation = viewData.elevation,
        tonalElevation = viewData.tonalElevation,
        border = viewData.border
    ) {
        content()
    }
}

/**
 * Card surface variant
 */
@Composable
fun AppCard(modifier: Modifier = Modifier, viewData: SurfaceViewData = SurfaceViewData.card(), content: @Composable () -> Unit) {
    AppSurface(
        viewData = viewData,
        modifier = modifier,
        content = content
    )
}

/**
 * Elevated surface variant
 */
@Composable
fun AppElevatedSurface(
    modifier: Modifier = Modifier,
    viewData: SurfaceViewData = SurfaceViewData.elevated(),
    content: @Composable () -> Unit
) {
    AppSurface(
        viewData = viewData,
        modifier = modifier,
        content = content
    )
}
