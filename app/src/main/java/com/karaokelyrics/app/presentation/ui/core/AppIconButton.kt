package com.karaokelyrics.app.presentation.ui.core

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Reusable IconButton component with ViewData styling
 */
@Composable
fun AppIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    viewData: IconButtonViewData,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    if (viewData.backgroundColor != null && viewData.backgroundColor != Color.Transparent) {
        FilledIconButton(
            onClick = onClick,
            modifier = modifier.size(viewData.size),
            enabled = viewData.enabled,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = viewData.backgroundColor,
                contentColor = viewData.contentColor ?: MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = viewData.backgroundColor.copy(alpha = 0.12f),
                disabledContentColor = (viewData.contentColor ?: MaterialTheme.colorScheme.onPrimary).copy(alpha = 0.38f)
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(viewData.iconSize)
            )
        }
    } else {
        IconButton(
            onClick = onClick,
            modifier = modifier.size(viewData.size),
            enabled = viewData.enabled,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = viewData.contentColor ?: MaterialTheme.colorScheme.onSurface,
                disabledContentColor = (viewData.contentColor ?: MaterialTheme.colorScheme.onSurface).copy(alpha = 0.38f)
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(viewData.iconSize)
            )
        }
    }
}

/**
 * Standard icon button
 */
@Composable
fun AppIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    enabled: Boolean = true,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    AppIconButton(
        icon = icon,
        onClick = onClick,
        viewData = IconButtonViewData.default(enabled).copy(
            contentColor = contentColor
        ),
        modifier = modifier,
        contentDescription = contentDescription
    )
}

/**
 * Filled icon button
 */
@Composable
fun AppFilledIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    enabled: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    AppIconButton(
        icon = icon,
        onClick = onClick,
        viewData = IconButtonViewData.filled(enabled).copy(
            backgroundColor = backgroundColor,
            contentColor = contentColor
        ),
        modifier = modifier,
        contentDescription = contentDescription
    )
}
