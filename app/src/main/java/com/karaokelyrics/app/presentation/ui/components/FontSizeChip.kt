package com.karaokelyrics.app.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.presentation.ui.theme.ColorStyles

@Composable
fun FontSizeChip(
    fontSize: FontSize,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = if (isSelected) {
            ColorStyles.chipBackgroundSelected()
        } else {
            ColorStyles.chipBackground()
        },
        shape = RoundedCornerShape(20.dp),
        tonalElevation = if (isSelected) 2.dp else 0.dp,
        border = if (isSelected) {
            null
        } else {
            BorderStroke(1.dp, ColorStyles.inactiveBorder())
        }
    ) {
        Text(
            text = fontSize.displayName,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) {
                ColorStyles.chipTextSelected()
            } else {
                ColorStyles.chipText()
            }
        )
    }
}