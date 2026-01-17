package com.karaokelyrics.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.ISyncedLine

/**
 * State holder for karaoke viewer UI state.
 * This is the single source of truth for all UI state in the karaoke library.
 *
 * Instead of scattering state across multiple viewers and components,
 * this holder centralizes state management and provides a clean API
 * for updating state based on time changes.
 *
 * Usage:
 * ```
 * val stateHolder = rememberKaraokeStateHolder(config)
 *
 * LaunchedEffect(currentTimeMs) {
 *     stateHolder.updateTime(currentTimeMs)
 * }
 *
 * KaraokeLyricsViewer(
 *     stateHolder = stateHolder,
 *     ...
 * )
 * ```
 */
@Stable
class KaraokeStateHolder(initialConfig: KaraokeLibraryConfig, private val calculator: KaraokeStateCalculator = KaraokeStateCalculator()) {
    private val _uiState = mutableStateOf(KaraokeUiState())
    private var _config = initialConfig

    /**
     * Current UI state. Observe this in Composables to react to state changes.
     */
    val uiState: State<KaraokeUiState> = _uiState

    /**
     * Current configuration. Can be used by viewers for additional settings.
     */
    val currentConfig: KaraokeLibraryConfig get() = _config

    /**
     * Update the configuration. This will recalculate state with the new config.
     *
     * @param config New configuration to apply
     */
    fun updateConfig(config: KaraokeLibraryConfig) {
        if (_config == config) return
        _config = config
        // Recalculate state with new config if we have lines
        val currentState = _uiState.value
        if (currentState.lines.isNotEmpty()) {
            _uiState.value = calculator.calculateState(
                lines = currentState.lines,
                currentTimeMs = currentState.currentTimeMs,
                config = config
            )
        }
    }

    /**
     * Set the lines to display. Call this when lyrics are loaded.
     *
     * @param lines List of synchronized lines
     */
    fun setLines(lines: List<ISyncedLine>) {
        _uiState.value = calculator.calculateState(
            lines = lines,
            currentTimeMs = _uiState.value.currentTimeMs,
            config = _config
        )
    }

    /**
     * Update the current playback time. Call this on each time tick.
     * This will recalculate all line states based on the new time.
     *
     * @param currentTimeMs Current playback time in milliseconds
     */
    fun updateTime(currentTimeMs: Int) {
        val currentState = _uiState.value
        if (currentState.currentTimeMs == currentTimeMs) {
            return // No change needed
        }

        _uiState.value = calculator.calculateState(
            lines = currentState.lines,
            currentTimeMs = currentTimeMs,
            config = _config
        )
    }

    /**
     * Update both lines and time simultaneously.
     * Useful when switching to new content.
     *
     * @param lines List of synchronized lines
     * @param currentTimeMs Current playback time in milliseconds
     */
    fun update(lines: List<ISyncedLine>, currentTimeMs: Int) {
        _uiState.value = calculator.calculateState(
            lines = lines,
            currentTimeMs = currentTimeMs,
            config = _config
        )
    }

    /**
     * Reset the state holder to initial state.
     */
    fun reset() {
        _uiState.value = KaraokeUiState()
    }

    /**
     * Get the current line index, if any line is currently playing.
     */
    val currentLineIndex: Int?
        get() = _uiState.value.currentLineIndex

    /**
     * Get the currently playing line, if any.
     */
    val currentLine: ISyncedLine?
        get() = _uiState.value.currentLine

    /**
     * Check if the state has been initialized with lines.
     */
    val isInitialized: Boolean
        get() = _uiState.value.isInitialized
}

/**
 * Creates and remembers a [KaraokeStateHolder] instance.
 *
 * The state holder is remembered across recompositions and survives
 * config changes. Config updates are handled internally via [KaraokeStateHolder.updateConfig].
 *
 * @param config Library configuration for visual/animation settings
 * @return A remembered state holder instance
 */
@Composable
fun rememberKaraokeStateHolder(config: KaraokeLibraryConfig = KaraokeLibraryConfig.Default): KaraokeStateHolder {
    val stateHolder = remember {
        KaraokeStateHolder(config)
    }
    // Update config when it changes without recreating the state holder
    LaunchedEffect(config) {
        stateHolder.updateConfig(config)
    }
    return stateHolder
}

/**
 * Creates and remembers a [KaraokeStateHolder] instance with initial lines.
 *
 * @param lines Initial lines to display
 * @param config Library configuration for visual/animation settings
 * @return A remembered state holder instance initialized with lines
 */
@Composable
fun rememberKaraokeStateHolder(lines: List<ISyncedLine>, config: KaraokeLibraryConfig = KaraokeLibraryConfig.Default): KaraokeStateHolder {
    val stateHolder = remember {
        KaraokeStateHolder(config).also { holder ->
            holder.setLines(lines)
        }
    }
    // Update config when it changes without recreating the state holder
    LaunchedEffect(config) {
        stateHolder.updateConfig(config)
    }
    return stateHolder
}
