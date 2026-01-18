package com.kyrics.demo.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kyrics.demo.presentation.screen.components.ColorPickerDialog
import com.kyrics.demo.presentation.screen.components.SettingsPanel
import com.kyrics.demo.presentation.viewmodel.ColorPickerTarget
import com.kyrics.demo.presentation.viewmodel.DemoIntent
import com.kyrics.demo.presentation.viewmodel.DemoState
import com.kyrics.KyricsViewer
import com.kyrics.models.ISyncedLine

/**
 * Main demo screen composable - stateless.
 * Receives state and dispatches intents to the ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoScreen(
    state: DemoState,
    onIntent: (DemoIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Kyrics Demo") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top - Display area (1/3 of screen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.33f)
                    .background(state.settings.backgroundColor)
            ) {
                KyricsViewer(
                    lines = state.demoLines,
                    currentTimeMs = state.currentTimeMs.toInt(),
                    config = state.libraryConfig,
                    modifier = Modifier.fillMaxSize(),
                    onLineClick = { _: ISyncedLine, index: Int ->
                        onIntent(DemoIntent.SelectLine(index))
                    }
                )
            }

            // Bottom - Controls (2/3 of screen, scrollable)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.67f)
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                SettingsPanel(
                    state = state,
                    onIntent = onIntent,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Color picker dialog
    state.showColorPicker?.let { target ->
        val currentColor = when (target) {
            ColorPickerTarget.SUNG_COLOR -> state.settings.sungColor
            ColorPickerTarget.UNSUNG_COLOR -> state.settings.unsungColor
            ColorPickerTarget.ACTIVE_COLOR -> state.settings.activeColor
            ColorPickerTarget.BACKGROUND_COLOR -> state.settings.backgroundColor
        }

        ColorPickerDialog(
            currentColor = currentColor,
            onColorSelected = { color ->
                onIntent(DemoIntent.UpdateColor(target, color))
            },
            onDismiss = { onIntent(DemoIntent.DismissColorPicker) }
        )
    }
}
