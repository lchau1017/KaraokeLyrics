package com.kyrics.demo.presentation.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kyrics.demo.data.datasource.DemoLyricsDataSource

/**
 * Stateless playback control panel composable.
 */
@Composable
fun ControlPanel(
    isPlaying: Boolean,
    currentTimeMs: Long,
    onPlayPause: () -> Unit,
    onReset: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("Playback", style = MaterialTheme.typography.titleMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onReset) {
                Text("Reset")
            }
            Button(onClick = onPlayPause) {
                Text(if (isPlaying) "Pause" else "Play")
            }
        }

        Slider(
            value = currentTimeMs.toFloat(),
            onValueChange = { onSeek(it.toLong()) },
            valueRange = 0f..DemoLyricsDataSource.TOTAL_DURATION_MS.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Time: ${currentTimeMs / 1000}s")
    }
}
