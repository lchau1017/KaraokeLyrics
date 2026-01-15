package com.karaokelyrics.app.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.karaokelyrics.app.presentation.ui.theme.ColorStyles

@Composable
fun ErrorScreen(
    errorMessage: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
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
            ErrorTitle()
            ErrorDescription(errorMessage)
            RetryButton(onRetry)
        }
    }
}

@Composable
private fun ErrorTitle() {
    Text(
        text = "Error loading lyrics",
        style = MaterialTheme.typography.headlineMedium,
        color = ColorStyles.primaryText(),
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ErrorDescription(message: String?) {
    Text(
        text = message ?: "Unknown error occurred",
        style = MaterialTheme.typography.bodyMedium,
        color = ColorStyles.secondaryText(),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun RetryButton(onRetry: () -> Unit) {
    Button(
        onClick = onRetry,
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorStyles.primaryButton(),
            contentColor = ColorStyles.onPrimaryButton()
        ),
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Text(
            text = "Retry",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}