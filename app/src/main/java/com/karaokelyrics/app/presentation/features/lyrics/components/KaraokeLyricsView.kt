package com.karaokelyrics.app.presentation.features.lyrics.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.karaokelyrics.app.domain.model.ISyncedLine
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.ui.api.KaraokeLibrary
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig

/**
 * Simplified KaraokeLyricsView that uses the karaoke-ui-library.
 * Acts as a thin wrapper to integrate the library with the app.
 */
@Composable
fun KaraokeLyricsView(
    lyrics: SyncedLyrics?,
    currentTimeMs: Int,
    libraryConfig: KaraokeLibraryConfig,
    onLineClicked: (ISyncedLine) -> Unit,
    modifier: Modifier = Modifier
) {
    lyrics?.let {
        // Convert app's ISyncedLine to library's ISyncedLine
        val libraryLines = it.lines.map { line ->
            line.toLibraryLine()
        }

        KaraokeLibrary.KaraokeLyricsViewer(
            lines = libraryLines,
            currentTimeMs = currentTimeMs,
            config = libraryConfig,
            modifier = modifier.fillMaxSize(),
            onLineClick = { line, index ->
                // Find the original line and trigger callback
                it.lines.getOrNull(index)?.let { originalLine ->
                    onLineClicked(originalLine)
                }
            }
        )
    }
}

/**
 * Extension function to convert app's ISyncedLine to library's ISyncedLine.
 * Since they have the same structure, we can use a simple adapter.
 */
private fun ISyncedLine.toLibraryLine(): com.karaokelyrics.ui.core.models.ISyncedLine {
    val appLine = this

    // If it's a KaraokeLine, convert it properly
    if (appLine is com.karaokelyrics.app.domain.model.KaraokeLine) {
        return com.karaokelyrics.ui.core.models.KaraokeLine(
            syllables = appLine.syllables.map { syllable ->
                com.karaokelyrics.ui.core.models.KaraokeSyllable(
                    content = syllable.content,
                    start = syllable.start,
                    end = syllable.end
                )
            },
            start = appLine.start,
            end = appLine.end,
            metadata = appLine.metadata
        )
    }

    // For simple lines, create a wrapper
    return object : com.karaokelyrics.ui.core.models.ISyncedLine {
        override val start: Int = appLine.start
        override val end: Int = appLine.end
        override fun getContent(): String = appLine.content
    }
}
