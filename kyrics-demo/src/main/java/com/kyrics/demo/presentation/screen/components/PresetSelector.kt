package com.kyrics.demo.presentation.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Stateless preset selector composable.
 */
@Composable
fun PresetSelector(onSelectPreset: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // First row
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            PresetButton(
                name = "Classic",
                onClick = { onSelectPreset("Classic") },
                modifier = Modifier.weight(1f)
            )
            PresetButton(
                name = "Neon",
                onClick = { onSelectPreset("Neon") },
                modifier = Modifier.weight(1f)
            )
            PresetButton(
                name = "Minimal",
                onClick = { onSelectPreset("Minimal") },
                modifier = Modifier.weight(1f)
            )
        }

        // Second row
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            PresetButton(
                name = "Rainbow",
                onClick = { onSelectPreset("Rainbow") },
                modifier = Modifier.weight(1f)
            )
            PresetButton(
                name = "Fire",
                onClick = { onSelectPreset("Fire") },
                modifier = Modifier.weight(1f)
            )
            PresetButton(
                name = "Ocean",
                onClick = { onSelectPreset("Ocean") },
                modifier = Modifier.weight(1f)
            )
        }

        // Third row
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            PresetButton(
                name = "Retro",
                onClick = { onSelectPreset("Retro") },
                modifier = Modifier.weight(1f)
            )
            PresetButton(
                name = "Elegant",
                onClick = { onSelectPreset("Elegant") },
                modifier = Modifier.weight(1f)
            )
            PresetButton(
                name = "Party",
                onClick = { onSelectPreset("Party") },
                modifier = Modifier.weight(1f)
            )
        }

        // Fourth row
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            PresetButton(
                name = "Matrix",
                onClick = { onSelectPreset("Matrix") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PresetButton(name: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(name, fontSize = 10.sp)
    }
}
