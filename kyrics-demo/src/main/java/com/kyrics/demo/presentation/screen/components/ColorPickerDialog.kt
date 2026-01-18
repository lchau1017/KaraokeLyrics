package com.kyrics.demo.presentation.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Stateless color picker dialog composable.
 */
@Composable
fun ColorPickerDialog(currentColor: Color, onColorSelected: (Color) -> Unit, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = modifier) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Select Color", style = MaterialTheme.typography.titleMedium)

                // Predefined colors
                val colors = listOf(
                    Color.White, Color.Black, Color.Red, Color.Green,
                    Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta,
                    Color.Gray, Color.LightGray, Color.DarkGray,
                    Color(0xFFFFD700), // Gold
                    Color(0xFFC0C0C0), // Silver
                    Color(0xFFFF6347), // Tomato
                    Color(0xFF00CED1), // Dark Turquoise
                    Color(0xFFFF1493) // Deep Pink
                )

                colors.chunked(4).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { color ->
                            ColorBox(
                                color = color,
                                isSelected = color == currentColor,
                                onClick = { onColorSelected(color) }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorBox(color: Color, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) Color.Blue else Color.Gray
            )
            .clickable(onClick = onClick)
    )
}
