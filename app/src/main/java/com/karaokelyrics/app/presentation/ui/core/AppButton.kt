package com.karaokelyrics.app.presentation.ui.core

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Reusable Button component with ViewData styling
 */
@Composable
fun AppButton(
    viewData: ButtonViewData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding
) {
    val backgroundColor = viewData.backgroundColor ?: Color.Transparent
    val contentColor = viewData.contentColor ?: MaterialTheme.colorScheme.onSurface

    if (viewData.border != null || backgroundColor == Color.Transparent) {
        // Outlined or text button
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            enabled = viewData.enabled,
            shape = viewData.shape,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = backgroundColor,
                contentColor = contentColor,
                disabledContainerColor = backgroundColor.copy(alpha = 0.12f),
                disabledContentColor = contentColor.copy(alpha = 0.38f)
            ),
            border = viewData.border,
            contentPadding = contentPadding
        ) {
            Text(
                text = viewData.text,
                style = viewData.textStyle ?: MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    } else {
        // Filled button
        Button(
            onClick = onClick,
            modifier = modifier,
            enabled = viewData.enabled,
            shape = viewData.shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = contentColor,
                disabledContainerColor = backgroundColor.copy(alpha = 0.12f),
                disabledContentColor = contentColor.copy(alpha = 0.38f)
            ),
            contentPadding = contentPadding
        ) {
            Text(
                text = viewData.text,
                style = viewData.textStyle ?: MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Text button variant
 */
@Composable
fun AppTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentColor: Color = MaterialTheme.colorScheme.primary
) {
    AppButton(
        viewData = ButtonViewData.text(text, enabled).copy(
            contentColor = contentColor
        ),
        onClick = onClick,
        modifier = modifier
    )
}

/**
 * Primary button variant
 */
@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    AppButton(
        viewData = ButtonViewData.primary(text, enabled),
        onClick = onClick,
        modifier = modifier
    )
}

/**
 * Outlined button variant
 */
@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color = MaterialTheme.colorScheme.outline
) {
    AppButton(
        viewData = ButtonViewData.outlined(text, enabled, borderColor),
        onClick = onClick,
        modifier = modifier
    )
}