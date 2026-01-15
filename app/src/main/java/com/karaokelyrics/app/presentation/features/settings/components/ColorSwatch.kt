package com.karaokelyrics.app.presentation.features.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.karaokelyrics.app.presentation.ui.core.AppSurface
import com.karaokelyrics.app.presentation.ui.core.SurfaceViewData

@Composable
fun ColorSwatch(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedBorderColor: Color = MaterialTheme.colorScheme.primary,
    unselectedBorderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
) {
    val surfaceViewData = SurfaceViewData(
        backgroundColor = Color.Transparent,
        shape = CircleShape,
        border = BorderStroke(
            width = if (isSelected) 3.dp else 1.dp,
            color = if (isSelected) selectedBorderColor else unselectedBorderColor
        )
    )

    AppSurface(
        viewData = surfaceViewData,
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isSelected) 3.dp else 1.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}