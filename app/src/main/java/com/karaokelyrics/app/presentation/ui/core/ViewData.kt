package com.karaokelyrics.app.presentation.ui.core

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * View data class for text styling
 */
data class TextViewData(
    val text: String,
    val style: TextStyle? = null,
    val color: Color? = null,
    val fontSize: TextUnit? = null,
    val fontWeight: FontWeight? = null,
    val maxLines: Int? = null
) {
    companion object {
        @Composable
        fun default(text: String) = TextViewData(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        @Composable
        fun title(text: String) = TextViewData(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        @Composable
        fun subtitle(text: String) = TextViewData(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        @Composable
        fun label(text: String) = TextViewData(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * View data class for button styling
 */
data class ButtonViewData(
    val text: String,
    val enabled: Boolean = true,
    val backgroundColor: Color? = null,
    val contentColor: Color? = null,
    val shape: Shape = RoundedCornerShape(8.dp),
    val elevation: Dp = 0.dp,
    val border: BorderStroke? = null,
    val textStyle: TextStyle? = null
) {
    companion object {
        @Composable
        fun primary(text: String, enabled: Boolean = true) = ButtonViewData(
            text = text,
            enabled = enabled,
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            textStyle = MaterialTheme.typography.labelLarge
        )

        @Composable
        fun secondary(text: String, enabled: Boolean = true) = ButtonViewData(
            text = text,
            enabled = enabled,
            backgroundColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            textStyle = MaterialTheme.typography.labelLarge
        )

        @Composable
        fun outlined(
            text: String,
            enabled: Boolean = true,
            borderColor: Color = MaterialTheme.colorScheme.outline
        ) = ButtonViewData(
            text = text,
            enabled = enabled,
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            border = BorderStroke(1.dp, borderColor),
            textStyle = MaterialTheme.typography.labelLarge
        )

        @Composable
        fun text(text: String, enabled: Boolean = true) = ButtonViewData(
            text = text,
            enabled = enabled,
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            textStyle = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * View data class for chip styling
 */
data class ChipViewData(
    val text: String,
    val selected: Boolean = false,
    val enabled: Boolean = true,
    val backgroundColor: Color? = null,
    val selectedBackgroundColor: Color? = null,
    val contentColor: Color? = null,
    val selectedContentColor: Color? = null,
    val shape: Shape = RoundedCornerShape(20.dp),
    val border: BorderStroke? = null,
    val selectedBorder: BorderStroke? = null,
    val elevation: Dp = 0.dp,
    val selectedElevation: Dp = 2.dp
) {
    companion object {
        @Composable
        fun default(
            text: String,
            selected: Boolean = false,
            enabled: Boolean = true
        ) = ChipViewData(
            text = text,
            selected = selected,
            enabled = enabled,
            backgroundColor = MaterialTheme.colorScheme.surface,
            selectedBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            border = if (!selected) BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) else null
        )

        @Composable
        fun filter(
            text: String,
            selected: Boolean = false
        ) = ChipViewData(
            text = text,
            selected = selected,
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            selectedBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/**
 * View data class for surface styling
 */
data class SurfaceViewData(
    val backgroundColor: Color? = null,
    val contentColor: Color? = null,
    val shape: Shape = RoundedCornerShape(0.dp),
    val elevation: Dp = 0.dp,
    val tonalElevation: Dp = 0.dp,
    val border: BorderStroke? = null
) {
    companion object {
        @Composable
        fun default() = SurfaceViewData(
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )

        @Composable
        fun elevated(elevation: Dp = 2.dp) = SurfaceViewData(
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            elevation = elevation
        )

        @Composable
        fun card(shape: Shape = RoundedCornerShape(12.dp)) = SurfaceViewData(
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            shape = shape,
            tonalElevation = 1.dp
        )
    }
}

/**
 * View data class for icon button styling
 */
data class IconButtonViewData(
    val enabled: Boolean = true,
    val backgroundColor: Color? = null,
    val contentColor: Color? = null,
    val size: Dp = 48.dp,
    val iconSize: Dp = 24.dp
) {
    companion object {
        @Composable
        fun default(enabled: Boolean = true) = IconButtonViewData(
            enabled = enabled,
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        )

        @Composable
        fun filled(enabled: Boolean = true) = IconButtonViewData(
            enabled = enabled,
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )

        @Composable
        fun tonal(enabled: Boolean = true) = IconButtonViewData(
            enabled = enabled,
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}