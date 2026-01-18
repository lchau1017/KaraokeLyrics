package com.karaokelyrics.app.presentation.features.lyrics.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.karaokelyrics.app.domain.model.ISyncedLine
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.kyrics.KyricsViewer
import com.kyrics.config.KyricsConfig
import com.kyrics.models.ISyncedLine as LibraryISyncedLine
import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable

/**
 * Simplified KaraokeLyricsView that uses the Kyrics library.
 * Acts as a thin wrapper to integrate the library with the app.
 */
@Composable
fun KaraokeLyricsView(
    lyrics: SyncedLyrics?,
    currentTimeMs: Int,
    libraryConfig: KyricsConfig,
    onLineClicked: (ISyncedLine) -> Unit,
    modifier: Modifier = Modifier
) {
    lyrics?.let {
        // Convert app's ISyncedLine to library's ISyncedLine
        val libraryLines = it.lines.map { line ->
            line.toLibraryLine()
        }

        KyricsViewer(
            lines = libraryLines,
            currentTimeMs = currentTimeMs,
            config = libraryConfig,
            modifier = modifier.fillMaxSize(),
            onLineClick = { _: LibraryISyncedLine, index: Int ->
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
private fun ISyncedLine.toLibraryLine(): LibraryISyncedLine {
    val appLine = this

    // If it's a KyricsLine (app's version), convert it properly
    if (appLine is com.karaokelyrics.app.domain.model.KyricsLine) {
        return KyricsLine(
            syllables = appLine.syllables.map { syllable ->
                KyricsSyllable(
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
    return object : LibraryISyncedLine {
        override val start: Int = appLine.start
        override val end: Int = appLine.end
        override fun getContent(): String = appLine.content
    }
}
