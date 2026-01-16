package com.karaokelyrics.app.presentation.features.lyrics.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.karaokelyrics.app.domain.model.ISyncedLine

/**
 * Single Source of Truth for lyrics rendering.
 * Following industry best practices from Google, Spotify, and Netflix.
 * 
 * This model contains ALL data needed to render a lyrics line,
 * eliminating the need for separate config objects.
 */
@Immutable
data class LyricsRenderModel(
    // Core data
    val line: ISyncedLine,
    val index: Int,
    
    // Timing context (replaces animation state)
    val timing: TimingContext,
    
    // Visual configuration (merged from config + visual state)
    val visual: VisualConfig,
    
    // Interaction hints
    val interaction: InteractionHints,
    
    // Render instructions (pre-calculated, no logic in views)
    val renderInstructions: RenderInstructions
)

/**
 * Timing context for the line.
 * Contains all time-related information.
 */
@Immutable
data class TimingContext(
    val currentTimeMs: Int,
    val lineStartMs: Int,
    val lineEndMs: Int,
    val progress: Float, // 0.0 to 1.0
    val state: TimingState,
    val distanceFromActive: Int // How many lines away from active
)

enum class TimingState {
    UPCOMING,   // Not yet playing
    ACTIVE,     // Currently playing
    RECENT,     // Just finished (within fade window)
    PAST        // Finished playing
}

/**
 * Visual configuration - single source for all visual properties.
 * Combines what was in KaraokeConfig and VisualState.
 */
@Immutable
data class VisualConfig(
    // Base properties
    val textStyle: TextStyle,
    val textColor: Color,
    
    // Calculated properties (no alpha modification)
    val opacity: Float,
    val scale: Float,
    val blur: Float,
    
    // Animation settings
    val enableCharacterAnimations: Boolean,
    val characterFloatOffset: Float,
    val characterWaveDelay: Float,
    
    // Karaoke-specific
    val activeCharacterColor: Color,
    val inactiveCharacterColor: Color
)

/**
 * Interaction hints for user actions.
 */
@Immutable
data class InteractionHints(
    val isClickable: Boolean,
    val isFocused: Boolean,
    val isHighlighted: Boolean,
    val clickAction: ClickAction = ClickAction.SEEK
)

enum class ClickAction {
    SEEK,
    NONE,
    CUSTOM
}

/**
 * Pre-calculated render instructions.
 * Views should only read these values, never calculate.
 */
@Immutable
data class RenderInstructions(
    val shouldAnimate: Boolean,
    val animationDuration: Int,
    val zIndex: Float, // Layering for 3D effect
    val horizontalOffset: Float, // For slide animations
    val verticalOffset: Float
)

/**
 * Container for all render models with metadata.
 * This is the single source of truth for the entire lyrics display.
 */
@Immutable
data class LyricsRenderState(
    val models: List<LyricsRenderModel>,
    val scrollTarget: ScrollTarget?,
    val globalConfig: GlobalRenderConfig
)

/**
 * Scroll target information.
 */
@Immutable
data class ScrollTarget(
    val index: Int,
    val offset: Int,
    val animated: Boolean
)

/**
 * Global configuration that applies to all lines.
 */
@Immutable
data class GlobalRenderConfig(
    val lineSpacing: Float,
    val horizontalPadding: Float,
    val verticalPadding: Float,
    val scrollBehavior: ScrollBehavior,
    val renderMode: RenderMode
)

enum class ScrollBehavior {
    SMOOTH,
    INSTANT,
    SPRING
}

enum class RenderMode {
    KARAOKE,        // Character-by-character
    SIMPLE,         // Line-by-line
    WORD_BY_WORD   // Word highlighting
}