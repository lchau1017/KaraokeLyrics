package com.kyrics

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kyrics.config.KyricsConfigBuilder
import com.kyrics.config.KyricsConfig
import com.kyrics.config.KyricsPresets as ConfigPresets
import com.kyrics.config.kyricsConfig
import com.kyrics.models.ISyncedLine

/**
 * Complete lyrics viewer with automatic scrolling and synchronization.
 *
 * This composable manages the entire lyrics display experience:
 * - Auto-scrolling to keep current line in view
 * - Distance-based visual effects (blur, opacity)
 * - Per-character and per-line animations
 * - Multiple viewer styles (scroll, stacked, carousel, etc.)
 *
 * ## Quick Start
 *
 * ```kotlin
 * // Simplest usage with defaults
 * KyricsViewer(
 *     lines = yourLyrics,
 *     currentTimeMs = playerPosition
 * )
 *
 * // With a preset
 * KyricsViewer(
 *     lines = yourLyrics,
 *     currentTimeMs = playerPosition,
 *     config = KyricsPresets.Neon
 * )
 *
 * // With custom configuration using DSL
 * KyricsViewer(
 *     lines = yourLyrics,
 *     currentTimeMs = playerPosition,
 *     config = kyricsConfig {
 *         colors {
 *             playing = Color.Yellow
 *             played = Color.Green
 *         }
 *         animations {
 *             characterAnimations = true
 *             characterScale = 1.2f
 *         }
 *     }
 * )
 * ```
 *
 * @param lines List of synchronized lines to display. Use [KyricsLine] or implement [ISyncedLine].
 * @param currentTimeMs Current playback time in milliseconds.
 * @param config Configuration for visual, animation, and behavior. Use [kyricsConfig] DSL or [KyricsPresets].
 * @param modifier Modifier for the composable.
 * @param onLineClick Optional callback when a line is clicked. Receives the line and its index.
 *
 * @see kyricsConfig for creating custom configurations
 * @see KyricsPresets for predefined styles
 */
@Composable
fun KyricsViewer(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    config: KyricsConfig = KyricsConfig.Default,
    modifier: Modifier = Modifier,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null
) {
    com.kyrics.components.KyricsViewer(
        lines = lines,
        currentTimeMs = currentTimeMs,
        config = config,
        modifier = modifier,
        onLineClick = onLineClick
    )
}

/**
 * Creates a lyrics viewer with inline configuration using DSL.
 *
 * Example:
 * ```kotlin
 * KyricsViewer(
 *     lines = lyrics,
 *     currentTimeMs = position
 * ) {
 *     colors {
 *         playing = Color.Yellow
 *         sung = Color.Green
 *     }
 *     animations {
 *         characterScale = 1.3f
 *     }
 * }
 * ```
 *
 * @param lines List of synchronized lines to display.
 * @param currentTimeMs Current playback time in milliseconds.
 * @param modifier Modifier for the composable.
 * @param onLineClick Optional callback when a line is clicked.
 * @param configBuilder DSL block to configure the viewer.
 */
@Composable
fun KyricsViewer(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    modifier: Modifier = Modifier,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    configBuilder: KyricsConfigBuilder.() -> Unit
) {
    val config = kyricsConfig(configBuilder)
    com.kyrics.components.KyricsViewer(
        lines = lines,
        currentTimeMs = currentTimeMs,
        config = config,
        modifier = modifier,
        onLineClick = onLineClick
    )
}

/**
 * Predefined configurations for common use cases.
 *
 * Available presets:
 * - [Classic] - Simple and clean style
 * - [Neon] - Vibrant neon colors with gradient effects
 * - [Rainbow] - Multi-color gradient animation
 * - [Fire] - Warm colors with flickering animation
 * - [Ocean] - Cool blue tones with wave-like motion
 * - [Retro] - 80s style with bold effects
 * - [Minimal] - Clean, no-frills design
 * - [Elegant] - Subtle gold/silver styling
 * - [Party] - Maximum effects for high energy
 * - [Matrix] - Green monospace cyber style
 *
 * Example:
 * ```kotlin
 * KyricsViewer(
 *     lines = lyrics,
 *     currentTimeMs = position,
 *     config = KyricsPresets.Neon
 * )
 * ```
 */
object KyricsPresets {
    /** Classic style - simple and clean */
    val Classic = ConfigPresets.Classic

    /** Neon style with gradient effects */
    val Neon = ConfigPresets.Neon

    /** Rainbow gradient style */
    val Rainbow = ConfigPresets.Rainbow

    /** Fire effect style with warm colors */
    val Fire = ConfigPresets.Fire

    /** Ocean/Water style with cool colors */
    val Ocean = ConfigPresets.Ocean

    /** Retro 80s style */
    val Retro = ConfigPresets.Retro

    /** Minimal style - clean and simple */
    val Minimal = ConfigPresets.Minimal

    /** Elegant style with subtle effects */
    val Elegant = ConfigPresets.Elegant

    /** Party mode with all effects maxed out */
    val Party = ConfigPresets.Party

    /** Matrix/Cyber style */
    val Matrix = ConfigPresets.Matrix

    /** All available presets as name-config pairs */
    val all = ConfigPresets.allPresets
}
