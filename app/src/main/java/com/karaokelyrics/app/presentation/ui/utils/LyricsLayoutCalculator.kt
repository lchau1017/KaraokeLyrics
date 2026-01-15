package com.karaokelyrics.app.presentation.ui.utils

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.TextLayoutResult

/**
 * Data models for lyrics layout
 */

@Stable
data class SyllableLayout(
    val syllable: com.karaokelyrics.app.domain.model.karaoke.KaraokeSyllable,
    val textLayoutResult: TextLayoutResult,
    val wordId: Int,
    val useAwesomeAnimation: Boolean,
    val width: Float = textLayoutResult.size.width.toFloat(),
    val position: Offset = Offset.Zero,
    val wordPivot: Offset = Offset.Zero,
    val wordAnimInfo: WordAnimationInfo? = null,
    val charOffsetInWord: Int = 0,
    val charLayouts: List<TextLayoutResult>? = null,
    val charOriginalBounds: List<Rect>? = null,
    val firstBaseline: Float = 0f,
)

@Stable
data class WordAnimationInfo(
    val wordStartTime: Long,
    val wordEndTime: Long,
    val wordContent: String,
    val wordDuration: Long = wordEndTime - wordStartTime
)

@Stable
data class WrappedLine(
    val syllables: List<SyllableLayout>,
    val totalWidth: Float
)

@Stable
data class LineLayout(
    val line: com.karaokelyrics.app.domain.model.karaoke.KaraokeLine,
    val wrappedLines: List<WrappedLine>,
    val syllableLayouts: List<List<SyllableLayout>>,
    val totalHeight: Float
)