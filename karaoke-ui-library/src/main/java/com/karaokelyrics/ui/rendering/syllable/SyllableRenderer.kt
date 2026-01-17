package com.karaokelyrics.ui.rendering.syllable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.KaraokeLine
import com.karaokelyrics.ui.rendering.character.CharacterRenderer
import com.karaokelyrics.ui.rendering.layout.TextLayoutCalculator

/**
 * Composable responsible for rendering karaoke syllables with proper layout and timing.
 * Delegates character-level rendering to CharacterRenderer.
 */
@Composable
fun SyllableRenderer(
    line: KaraokeLine,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig,
    textStyle: TextStyle,
    baseColor: Color,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val characterRenderer = remember { CharacterRenderer() }

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        val maxWidthPx = with(density) { maxWidth.toPx() }

        // Calculate layout information
        val layoutInfo = remember(line, textStyle, maxWidthPx) {
            TextLayoutCalculator.calculateLayout(
                line = line,
                textMeasurer = textMeasurer,
                textStyle = textStyle,
                maxWidth = maxWidthPx
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(density) {
                    (layoutInfo.totalHeight).toDp()
                })
        ) {
            // Render each line of text
            layoutInfo.lines.forEachIndexed { lineIndex, lineData ->
                val yPosition = lineIndex * layoutInfo.lineHeight

                // Render each syllable in the line
                lineData.syllables.forEach { syllableData ->
                    // Render each character in the syllable
                    characterRenderer.renderSyllableCharacters(
                        drawScope = this,
                        syllable = syllableData.syllable,
                        xOffset = syllableData.xOffset,
                        yOffset = yPosition,
                        currentTimeMs = currentTimeMs,
                        config = config,
                        textStyle = textStyle,
                        baseColor = baseColor,
                        textMeasurer = textMeasurer
                    )
                }
            }
        }
    }
}