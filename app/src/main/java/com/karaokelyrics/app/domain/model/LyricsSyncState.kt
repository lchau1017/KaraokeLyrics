package com.karaokelyrics.app.domain.model

data class LyricsSyncState(
    val currentLineIndex: Int = -1,
    val lineProgress: Float = 0f,
    val currentSyllableIndex: Int = -1,
    val activeSyllableProgress: Float = 0f,
    val nextLineStartTime: Int? = null,
    val allFocusedLineIndices: List<Int> = emptyList()
)