package com.karaokelyrics.app.presentation.features.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.karaokelyrics.app.presentation.ui.core.AppText
import com.karaokelyrics.app.presentation.ui.core.TextViewData

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colorScheme.primary,
    subtitleColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
    indicatorColor: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            AppLogo(
                titleColor = titleColor,
                subtitleColor = subtitleColor
            )
            LoadingIndicator(
                color = indicatorColor
            )
        }
    }
}

@Composable
private fun AppLogo(titleColor: Color, subtitleColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppText(
            viewData = TextViewData(
                text = "KaraokeLyrics",
                style = MaterialTheme.typography.displayMedium,
                color = titleColor,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        )
        AppText(
            viewData = TextViewData(
                text = "Sing Along",
                style = MaterialTheme.typography.titleMedium,
                color = subtitleColor
            )
        )
    }
}

@Composable
private fun LoadingIndicator(color: Color) {
    CircularProgressIndicator(
        color = color,
        modifier = Modifier.size(48.dp)
    )
}
