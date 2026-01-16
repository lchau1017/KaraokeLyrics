package com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.state

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Immutable state for karaoke line rendering.
 * Single Responsibility: State management only.
 */
@Immutable
data class KaraokeLineState(
    val activeColor: Color,
    val inactiveColor: Color,
    val enableCharacterAnimations: Boolean,
    val enableBlurEffect: Boolean,
    val isRtl: Boolean
) {
    val visualKey: String
        get() = "${activeColor.value}-${inactiveColor.value}-$enableBlurEffect"
}