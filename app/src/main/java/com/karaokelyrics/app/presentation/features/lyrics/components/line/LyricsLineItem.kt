package com.karaokelyrics.app.presentation.features.lyrics.components.line

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.karaokelyrics.app.domain.model.ISyncedLine
import com.karaokelyrics.app.presentation.features.lyrics.model.alignment.KaraokeAlignment
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.domain.model.synced.SyncedLine
import com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.KaraokeLineText
import com.karaokelyrics.app.presentation.features.lyrics.config.KaraokeConfig

/**
 * Renders a single lyrics line item.
 * Single Responsibility: Line rendering only.
 */
@Composable
fun LyricsLineItem(
    line: ISyncedLine,
    currentTimeMs: Int,
    opacity: Float,
    scale: Float,
    blur: Float,
    textColor: Color,
    textStyle: TextStyle,
    enableCharacterAnimations: Boolean,
    onLineClicked: (ISyncedLine) -> Unit,
    modifier: Modifier = Modifier,
    config: KaraokeConfig = KaraokeConfig.Default
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = line.start > currentTimeMs, // Only future lines are clickable
                onClick = { onLineClicked(line) }
            )
            .graphicsLayer {
                alpha = opacity
                scaleX = scale
                scaleY = scale
                if (blur > 0) {
                    // Apply blur effect for distant upcoming lines
                    shadowElevation = blur
                }
            }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (line) {
            is KaraokeLine -> {
                KaraokeLineText(
                    line = line,
                    currentTimeMs = currentTimeMs,
                    textStyle = textStyle,
                    activeColor = textColor,
                    inactiveColor = textColor.copy(alpha = 0.3f),
                    enableCharacterAnimations = enableCharacterAnimations,
                    enableBlurEffect = blur > 0,
                    config = config
                )
            }
            is SyncedLine -> {
                // For non-karaoke lines, use simple text
                Text(
                    text = line.content,
                    style = textStyle.copy(color = textColor),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            else -> {
                // Fallback for unknown line types
                Text(
                    text = when (line) {
                        is ISyncedLine -> line.content
                        else -> ""
                    },
                    style = textStyle.copy(color = textColor),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Determines text alignment for a line.
 */
fun getLineAlignment(line: ISyncedLine): Alignment {
    return when (line) {
        is KaraokeLine -> {
            // Extract alignment from metadata if present
            val alignmentStr = line.metadata["alignment"] ?: "Center"
            when (alignmentStr) {
                "Start" -> Alignment.CenterStart
                "End" -> Alignment.CenterEnd
                else -> Alignment.Center
            }
        }
        else -> Alignment.Center
    }
}