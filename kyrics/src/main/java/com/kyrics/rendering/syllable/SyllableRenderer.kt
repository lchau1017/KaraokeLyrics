package com.kyrics.rendering.syllable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import com.kyrics.config.KyricsConfig
import com.kyrics.models.KyricsLine
import com.kyrics.rendering.character.CharacterRenderer
import com.kyrics.rendering.layout.TextLayoutCalculator

/**
 * Composable responsible for rendering karaoke syllables with proper layout and timing.
 * Delegates character-level rendering to CharacterRenderer.
 */
@Composable
fun SyllableRenderer(
    line: KyricsLine,
    currentTimeMs: Int,
    config: KyricsConfig,
    textStyle: TextStyle,
    baseColor: Color,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

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
                .height(
                    with(density) {
                        (layoutInfo.totalHeight).toDp()
                    }
                )
        ) {
            // Render each line of text
            layoutInfo.lines.forEachIndexed { lineIndex, lineData ->
                val yPosition = lineIndex * layoutInfo.lineHeight

                // Render each syllable in the line
                lineData.syllables.forEach { syllableData ->
                    // Render each character in the syllable
                    CharacterRenderer.renderSyllableCharacters(
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
