package com.kyrics.rendering.layout

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable

/**
 * Calculates text layout information for karaoke lines.
 * Handles word wrapping, alignment, and positioning.
 */
object TextLayoutCalculator {

    /**
     * Calculate complete layout information for a karaoke line
     */
    fun calculateLayout(line: KyricsLine, textMeasurer: TextMeasurer, textStyle: TextStyle, maxWidth: Float): LayoutInfo {
        val lines = mutableListOf<LineLayoutData>()
        var currentLineContent = mutableListOf<SyllableLayoutData>()
        var currentLineWidth = 0f

        // Measure each syllable and arrange into lines
        line.syllables.forEach { syllable ->
            val syllableLayout = textMeasurer.measure(syllable.content, textStyle)
            val syllableWidth = syllableLayout.size.width.toFloat()

            // Check if we need to wrap to next line
            if (currentLineWidth + syllableWidth > maxWidth && currentLineContent.isNotEmpty()) {
                lines.add(LineLayoutData(currentLineContent))
                currentLineContent = mutableListOf()
                currentLineWidth = 0f
            }

            // Add syllable to current line
            currentLineContent.add(
                SyllableLayoutData(
                    syllable = syllable,
                    xOffset = currentLineWidth,
                    width = syllableWidth
                )
            )
            currentLineWidth += syllableWidth

            // Add space after syllable (except last)
            if (syllable != line.syllables.last()) {
                val spaceLayout = textMeasurer.measure(" ", textStyle)
                currentLineWidth += spaceLayout.size.width
            }
        }

        // Add remaining content
        if (currentLineContent.isNotEmpty()) {
            lines.add(LineLayoutData(currentLineContent))
        }

        // Calculate line height
        val sampleLayout = textMeasurer.measure("Sample", textStyle)
        val lineHeight = sampleLayout.size.height.toFloat()

        // Apply text alignment
        applyTextAlignment(lines, maxWidth, textStyle.textAlign ?: TextAlign.Start)

        return LayoutInfo(
            lines = lines,
            lineHeight = lineHeight,
            totalHeight = lineHeight * lines.size * 1.2f // Add line spacing
        )
    }

    private fun applyTextAlignment(lines: MutableList<LineLayoutData>, maxWidth: Float, textAlign: TextAlign) {
        lines.forEach { line ->
            val totalLineWidth = line.getTotalWidth()
            val alignmentOffset = when (textAlign) {
                TextAlign.Center -> (maxWidth - totalLineWidth) / 2f
                TextAlign.End, TextAlign.Right -> maxWidth - totalLineWidth
                else -> 0f
            }

            // Apply offset to all syllables in line
            if (alignmentOffset > 0) {
                line.syllables.forEach { syllable ->
                    syllable.xOffset += alignmentOffset
                }
            }
        }
    }

    /**
     * Contains layout information for the entire text
     */
    data class LayoutInfo(val lines: List<LineLayoutData>, val lineHeight: Float, val totalHeight: Float)

    /**
     * Layout data for a single line of text
     */
    data class LineLayoutData(val syllables: List<SyllableLayoutData>) {
        fun getTotalWidth(): Float = syllables.lastOrNull()?.let {
            it.xOffset + it.width
        } ?: 0f
    }

    /**
     * Layout data for a single syllable
     */
    data class SyllableLayoutData(val syllable: KyricsSyllable, var xOffset: Float, val width: Float)
}
