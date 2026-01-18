package com.karaokelyrics.demo.presentation.viewmodel

import androidx.compose.runtime.Immutable
import com.karaokelyrics.demo.domain.model.DemoSettings
import com.kyrics.config.KyricsConfig
import com.kyrics.models.KyricsLine

/**
 * Immutable UI state for the Demo screen.
 */
@Immutable
data class DemoState(
    val settings: DemoSettings = DemoSettings.Default,
    val isPlaying: Boolean = false,
    val currentTimeMs: Long = 0L,
    val selectedLineIndex: Int = 0,
    val showColorPicker: ColorPickerTarget? = null,
    val demoLines: List<KyricsLine> = emptyList(),
    val libraryConfig: KyricsConfig = KyricsConfig.Default
) {
    companion object {
        val Initial = DemoState()
    }
}

/**
 * Represents the target color being edited in the color picker.
 */
enum class ColorPickerTarget {
    SUNG_COLOR,
    UNSUNG_COLOR,
    ACTIVE_COLOR,
    BACKGROUND_COLOR
}
