package com.karaokelyrics.app.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.karaokelyrics.app.presentation.ui.theme.ColorStyles

@Composable
fun ColorSwatch(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        color = Color.Transparent,
        border = BorderStroke(
            width = if (isSelected) 3.dp else 1.dp,
            color = if (isSelected) {
                ColorStyles.activeBorder()
            } else {
                ColorStyles.inactiveBorder()
            }
        ),
        shape = CircleShape
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