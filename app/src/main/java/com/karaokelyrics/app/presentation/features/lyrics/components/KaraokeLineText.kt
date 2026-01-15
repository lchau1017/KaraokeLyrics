package com.karaokelyrics.app.presentation.features.lyrics.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.presentation.features.lyrics.components.karaoke.KaraokeLineContainer

/**
 * Clean, refactored KaraokeLineText using proper state hoisting.
 *
 * This is just a thin wrapper that delegates to the proper architecture:
 * - KaraokeLineContainer handles state and calculations
 * - KaraokeLineDisplay handles pure presentation
 *
 * NO MANAGERS, NO INJECTED DEPENDENCIES, NO LAYOUT CALCULATORS!
 * Everything is self-contained and follows clean architecture.
 */
@Composable
fun KaraokeLineText(
    line: KaraokeLine,
    currentPosition: Int,
    textStyle: TextStyle,
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.White.copy(alpha = 0.3f),
    modifier: Modifier = Modifier,
    enableCharacterAnimations: Boolean = true,
    enableBlurEffect: Boolean = true
) {
    KaraokeLineContainer(
        line = line,
        currentTimeMs = currentPosition,
        textStyle = textStyle,
        activeColor = activeColor,
        inactiveColor = inactiveColor,
        modifier = modifier,
        enableCharacterAnimations = enableCharacterAnimations,
        enableBlurEffect = enableBlurEffect
    )
}