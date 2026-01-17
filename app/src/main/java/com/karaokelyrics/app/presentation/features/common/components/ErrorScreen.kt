package com.karaokelyrics.app.presentation.features.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.karaokelyrics.app.presentation.ui.core.*

@Composable
fun ErrorScreen(
    errorMessage: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    descriptionColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    buttonViewData: ButtonViewData = ButtonViewData.primary("Retry")
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ErrorTitle(titleColor)
            ErrorDescription(errorMessage, descriptionColor)
            RetryButton(onRetry, buttonViewData)
        }
    }
}

@Composable
private fun ErrorTitle(
    color: Color
) {
    AppText(
        viewData = TextViewData(
            text = "Error loading lyrics",
            style = MaterialTheme.typography.headlineMedium,
            color = color,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        ),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ErrorDescription(
    message: String?,
    color: Color
) {
    AppText(
        viewData = TextViewData(
            text = message ?: "Unknown error occurred",
            style = MaterialTheme.typography.bodyMedium,
            color = color
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun RetryButton(
    onRetry: () -> Unit,
    buttonViewData: ButtonViewData
) {
    AppButton(
        viewData = buttonViewData,
        onClick = onRetry,
        modifier = Modifier.padding(top = 8.dp)
    )
}