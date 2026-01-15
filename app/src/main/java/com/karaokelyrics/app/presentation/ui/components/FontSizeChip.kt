package com.karaokelyrics.app.presentation.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.presentation.ui.core.AppChip
import com.karaokelyrics.app.presentation.ui.core.ChipViewData

@Composable
fun FontSizeChip(
    fontSize: FontSize,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    chipViewData: ChipViewData = ChipViewData.default(
        text = fontSize.displayName,
        selected = isSelected
    )
) {
    AppChip(
        viewData = chipViewData.copy(
            text = fontSize.displayName,
            selected = isSelected
        ),
        onClick = onClick,
        modifier = modifier
    )
}