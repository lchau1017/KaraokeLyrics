package com.karaokelyrics.app.presentation.features.lyrics.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.presentation.mapper.toKyricsLine
import com.kyrics.KyricsViewer
import com.kyrics.config.KyricsConfig

/**
 * KaraokeLyricsView that uses the Kyrics library for rendering.
 * Maps domain models to library models at the presentation boundary.
 */
@Composable
fun KaraokeLyricsView(
    lyrics: SyncedLyrics?,
    currentTimeMs: Int,
    libraryConfig: KyricsConfig,
    onLineClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    lyrics?.let { syncedLyrics ->
        val libraryLines = syncedLyrics.lines.map { it.toKyricsLine() }
        KyricsViewer(
            lines = libraryLines,
            currentTimeMs = currentTimeMs,
            config = libraryConfig,
            modifier = modifier.fillMaxSize(),
            onLineClick = { line, _ ->
                val index = libraryLines.indexOf(line)
                if (index >= 0) onLineClicked(index)
            }
        )
    }
}
