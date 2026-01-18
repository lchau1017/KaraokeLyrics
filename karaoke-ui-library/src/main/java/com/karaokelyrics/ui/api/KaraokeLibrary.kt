package com.karaokelyrics.ui.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.karaokelyrics.ui.core.config.KaraokeConfigBuilder
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.config.LibraryPresets
import com.karaokelyrics.ui.core.config.karaokeConfig
import com.karaokelyrics.ui.core.models.ISyncedLine

/**
 * Complete karaoke lyrics viewer with automatic scrolling and synchronization.
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
 * KaraokeLyricsViewer(
 *     lines = yourLyrics,
 *     currentTimeMs = playerPosition
 * )
 *
 * // With a preset
 * KaraokeLyricsViewer(
 *     lines = yourLyrics,
 *     currentTimeMs = playerPosition,
 *     config = KaraokePresets.Neon
 * )
 *
 * // With custom configuration using DSL
 * KaraokeLyricsViewer(
 *     lines = yourLyrics,
 *     currentTimeMs = playerPosition,
 *     config = karaokeConfig {
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
 * @param lines List of synchronized lines to display. Use [KaraokeLine] or implement [ISyncedLine].
 * @param currentTimeMs Current playback time in milliseconds.
 * @param config Configuration for visual, animation, and behavior. Use [karaokeConfig] DSL or [KaraokePresets].
 * @param modifier Modifier for the composable.
 * @param onLineClick Optional callback when a line is clicked. Receives the line and its index.
 *
 * @see karaokeConfig for creating custom configurations
 * @see KaraokePresets for predefined styles
 */
@Composable
fun KaraokeLyricsViewer(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig = KaraokeLibraryConfig.Default,
    modifier: Modifier = Modifier,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null
) {
    com.karaokelyrics.ui.components.KaraokeLyricsViewer(
        lines = lines,
        currentTimeMs = currentTimeMs,
        config = config,
        modifier = modifier,
        onLineClick = onLineClick
    )
}

/**
 * Creates a karaoke lyrics viewer with inline configuration using DSL.
 *
 * Example:
 * ```kotlin
 * KaraokeLyricsViewer(
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
fun KaraokeLyricsViewer(
    lines: List<ISyncedLine>,
    currentTimeMs: Int,
    modifier: Modifier = Modifier,
    onLineClick: ((ISyncedLine, Int) -> Unit)? = null,
    configBuilder: KaraokeConfigBuilder.() -> Unit
) {
    val config = karaokeConfig(configBuilder)
    com.karaokelyrics.ui.components.KaraokeLyricsViewer(
        lines = lines,
        currentTimeMs = currentTimeMs,
        config = config,
        modifier = modifier,
        onLineClick = onLineClick
    )
}

/**
 * Predefined karaoke configurations for common use cases.
 *
 * Available presets:
 * - [Classic] - Simple and clean karaoke style
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
 * KaraokeLyricsViewer(
 *     lines = lyrics,
 *     currentTimeMs = position,
 *     config = KaraokePresets.Neon
 * )
 * ```
 */
object KaraokePresets {
    /** Classic karaoke style - simple and clean */
    val Classic = LibraryPresets.Classic

    /** Neon style with gradient effects */
    val Neon = LibraryPresets.Neon

    /** Rainbow gradient style */
    val Rainbow = LibraryPresets.Rainbow

    /** Fire effect style with warm colors */
    val Fire = LibraryPresets.Fire

    /** Ocean/Water style with cool colors */
    val Ocean = LibraryPresets.Ocean

    /** Retro 80s style */
    val Retro = LibraryPresets.Retro

    /** Minimal style - clean and simple */
    val Minimal = LibraryPresets.Minimal

    /** Elegant style with subtle effects */
    val Elegant = LibraryPresets.Elegant

    /** Party mode with all effects maxed out */
    val Party = LibraryPresets.Party

    /** Matrix/Cyber style */
    val Matrix = LibraryPresets.Matrix

    /** All available presets as name-config pairs */
    val all = LibraryPresets.allPresets
}
