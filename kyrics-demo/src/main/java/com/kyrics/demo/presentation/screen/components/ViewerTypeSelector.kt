package com.kyrics.demo.presentation.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Stateless viewer type selector composable.
 */
@Composable
fun ViewerTypeSelector(
    selectedIndex: Int,
    onSelectViewerType: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewerTypes = listOf(
        "Center", "Smooth", "Stacked", "H-Paged",
        "Wave", "Spiral", "3D-Carousel", "Split",
        "Bounce", "Fade", "Burst", "Flip"
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        itemsIndexed(viewerTypes) { index, name ->
            FilterChip(
                selected = selectedIndex == index,
                onClick = { onSelectViewerType(index) },
                label = { Text(name, fontSize = 10.sp) }
            )
        }
    }
}
