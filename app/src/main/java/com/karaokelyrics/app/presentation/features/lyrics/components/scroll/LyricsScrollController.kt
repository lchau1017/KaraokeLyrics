package com.karaokelyrics.app.presentation.features.lyrics.components.scroll

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf

/**
 * Handles auto-scrolling logic for lyrics.
 * Single Responsibility: Scroll management only.
 */
class LyricsScrollController(
    private val listState: LazyListState,
    private val offsetPx: Int
) {
    private val scrollInCode = mutableStateOf(false)

    suspend fun scrollToLine(lineIndex: Int) {
        if (lineIndex < 0 || scrollInCode.value || listState.isScrollInProgress) return

        val items = listState.layoutInfo.visibleItemsInfo
        val targetItem = items.firstOrNull { it.index == lineIndex }

        if (targetItem != null) {
            // Calculate scroll offset to center the item with offset
            val viewportStart = listState.layoutInfo.viewportStartOffset
            val targetOffset = targetItem.offset - viewportStart - offsetPx

            if (kotlin.math.abs(targetOffset) > 10) { // Only scroll if offset is significant
                try {
                    scrollInCode.value = true
                    listState.animateScrollBy(
                        targetOffset.toFloat(),
                        animationSpec = tween(400, easing = EaseOut)
                    )
                } finally {
                    scrollInCode.value = false
                }
            }
        } else {
            // Item not visible, scroll to it
            try {
                scrollInCode.value = true
                listState.animateScrollToItem(lineIndex, -offsetPx)
            } finally {
                scrollInCode.value = false
            }
        }
    }
}

@Composable
fun rememberLyricsScrollController(
    listState: LazyListState,
    offsetPx: Int
): LyricsScrollController {
    return remember(listState, offsetPx) {
        LyricsScrollController(listState, offsetPx)
    }
}