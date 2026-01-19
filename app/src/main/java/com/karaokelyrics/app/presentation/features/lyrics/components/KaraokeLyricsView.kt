package com.karaokelyrics.app.presentation.features.lyrics.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.kyrics.KyricsViewer
import com.kyrics.config.KyricsConfig
import com.kyrics.models.SyncedLine

/**
 * KaraokeLyricsView that uses the Kyrics library directly.
 * No conversion needed since we use library models throughout.
 */
@Composable
fun KaraokeLyricsView(
    lyrics: SyncedLyrics?,
    currentTimeMs: Int,
    libraryConfig: KyricsConfig,
    onLineClicked: (SyncedLine) -> Unit,
    modifier: Modifier = Modifier
) {
    lyrics?.let {
        KyricsViewer(
            lines = it.lines,
            currentTimeMs = currentTimeMs,
            config = libraryConfig,
            modifier = modifier.fillMaxSize(),
            onLineClick = { line: SyncedLine, _: Int ->
                onLineClicked(line)
            }
        )
    }
}
