package com.karaokelyrics.demo.presentation.viewmodel

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaokelyrics.demo.data.datasource.DemoLyricsDataSource
import com.karaokelyrics.demo.domain.model.DemoSettings
import com.karaokelyrics.demo.domain.usecase.GetDemoSettingsUseCase
import com.karaokelyrics.demo.domain.usecase.UpdateDemoSettingsUseCase
import com.karaokelyrics.ui.core.config.AnimationConfig
import com.karaokelyrics.ui.core.config.ColorConfig
import com.karaokelyrics.ui.core.config.EffectsConfig
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.config.LayoutConfig
import com.karaokelyrics.ui.core.config.LibraryPresets
import com.karaokelyrics.ui.core.config.ViewerConfig
import com.karaokelyrics.ui.core.config.ViewerType
import com.karaokelyrics.ui.core.config.VisualConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DemoViewModel @Inject constructor(
    private val getDemoSettingsUseCase: GetDemoSettingsUseCase,
    private val updateDemoSettingsUseCase: UpdateDemoSettingsUseCase,
    private val demoLyricsDataSource: DemoLyricsDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(DemoState.Initial)
    val state: StateFlow<DemoState> = _state.asStateFlow()

    private val _effect = Channel<DemoEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var playbackJob: Job? = null

    init {
        loadInitialData()
        observeSettings()
    }

    private fun loadInitialData() {
        val demoLines = demoLyricsDataSource.getDemoLyrics()
        _state.update { it.copy(demoLines = demoLines) }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            getDemoSettingsUseCase().collect { settings ->
                _state.update { currentState ->
                    currentState.copy(
                        settings = settings,
                        libraryConfig = buildLibraryConfig(settings)
                    )
                }
            }
        }
    }

    fun onIntent(intent: DemoIntent) {
        when (intent) {
            // Playback
            is DemoIntent.TogglePlayPause -> togglePlayPause()
            is DemoIntent.Reset -> reset()
            is DemoIntent.Seek -> seek(intent.timeMs)
            is DemoIntent.UpdateTime -> updateTime(intent.timeMs)

            // Line selection
            is DemoIntent.SelectLine -> selectLine(intent.index)

            // Viewer type
            is DemoIntent.SelectViewerType -> updateViewerType(intent.index)

            // Color picker
            is DemoIntent.ShowColorPicker -> showColorPicker(intent.target)
            is DemoIntent.DismissColorPicker -> dismissColorPicker()
            is DemoIntent.UpdateColor -> updateColor(intent.target, intent.color)

            // Font settings
            is DemoIntent.UpdateFontSize -> updateFontSize(intent.size)
            is DemoIntent.UpdateFontWeight -> updateFontWeight(intent.weight)
            is DemoIntent.UpdateFontFamily -> updateFontFamily(intent.family)
            is DemoIntent.UpdateTextAlign -> updateTextAlign(intent.align)

            // Layout
            is DemoIntent.UpdateLineSpacing -> updateLineSpacing(intent.spacing)

            // Visual effects
            is DemoIntent.ToggleGradient -> toggleGradient(intent.enabled)
            is DemoIntent.UpdateGradientAngle -> updateGradientAngle(intent.angle)
            is DemoIntent.ToggleBlur -> toggleBlur(intent.enabled)
            is DemoIntent.UpdateBlurIntensity -> updateBlurIntensity(intent.intensity)

            // Character animations
            is DemoIntent.ToggleCharAnimation -> toggleCharAnimation(intent.enabled)
            is DemoIntent.UpdateCharMaxScale -> updateCharMaxScale(intent.scale)
            is DemoIntent.UpdateCharFloatOffset -> updateCharFloatOffset(intent.offset)
            is DemoIntent.UpdateCharRotation -> updateCharRotation(intent.degrees)

            // Line animations
            is DemoIntent.ToggleLineAnimation -> toggleLineAnimation(intent.enabled)
            is DemoIntent.UpdateLineScaleOnPlay -> updateLineScaleOnPlay(intent.scale)

            // Pulse
            is DemoIntent.TogglePulse -> togglePulse(intent.enabled)
            is DemoIntent.UpdatePulseMinScale -> updatePulseMinScale(intent.scale)
            is DemoIntent.UpdatePulseMaxScale -> updatePulseMaxScale(intent.scale)

            // Presets
            is DemoIntent.LoadPreset -> loadPreset(intent.presetName)
        }
    }

    // Playback handlers
    private fun togglePlayPause() {
        val newIsPlaying = !_state.value.isPlaying
        _state.update { it.copy(isPlaying = newIsPlaying) }

        if (newIsPlaying) {
            startPlaybackTimer()
        } else {
            stopPlaybackTimer()
        }
    }

    private fun startPlaybackTimer() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (_state.value.isPlaying) {
                delay(100)
                val currentTime = _state.value.currentTimeMs
                val newTime = if (currentTime + 100 > DemoLyricsDataSource.TOTAL_DURATION_MS) {
                    0L
                } else {
                    currentTime + 100
                }
                _state.update { it.copy(currentTimeMs = newTime) }
                if (newTime == 0L) {
                    _state.update { it.copy(selectedLineIndex = 0) }
                } else {
                    updateSelectedLineFromTime(newTime)
                }
            }
        }
    }

    private fun stopPlaybackTimer() {
        playbackJob?.cancel()
        playbackJob = null
    }

    private fun reset() {
        stopPlaybackTimer()
        _state.update {
            it.copy(
                isPlaying = false,
                currentTimeMs = 0L,
                selectedLineIndex = 0
            )
        }
    }

    private fun seek(timeMs: Long) {
        _state.update { it.copy(currentTimeMs = timeMs) }
        updateSelectedLineFromTime(timeMs)
    }

    private fun updateTime(timeMs: Long) {
        val newTime = if (timeMs > DemoLyricsDataSource.TOTAL_DURATION_MS) 0L else timeMs
        _state.update { it.copy(currentTimeMs = newTime) }
        if (newTime == 0L) {
            _state.update { it.copy(selectedLineIndex = 0) }
        } else {
            updateSelectedLineFromTime(newTime)
        }
    }

    private fun updateSelectedLineFromTime(timeMs: Long) {
        val currentState = _state.value
        val currentLineIndex = currentState.demoLines.indexOfFirst { line ->
            timeMs >= line.start && timeMs <= line.end
        }
        if (currentLineIndex >= 0) {
            _state.update { it.copy(selectedLineIndex = currentLineIndex) }
        }
    }

    private fun selectLine(index: Int) {
        _state.update { it.copy(selectedLineIndex = index) }
    }

    // Settings update helpers
    private fun updateSettings(transform: (DemoSettings) -> DemoSettings) {
        viewModelScope.launch {
            val newSettings = transform(_state.value.settings)
            updateDemoSettingsUseCase(newSettings)
        }
    }

    private fun updateViewerType(index: Int) {
        updateSettings { it.copy(viewerTypeIndex = index) }
    }

    // Color picker handlers
    private fun showColorPicker(target: ColorPickerTarget) {
        _state.update { it.copy(showColorPicker = target) }
    }

    private fun dismissColorPicker() {
        _state.update { it.copy(showColorPicker = null) }
    }

    private fun updateColor(target: ColorPickerTarget, color: Color) {
        updateSettings { settings ->
            when (target) {
                ColorPickerTarget.SUNG_COLOR -> settings.copy(sungColor = color)
                ColorPickerTarget.UNSUNG_COLOR -> settings.copy(unsungColor = color)
                ColorPickerTarget.ACTIVE_COLOR -> settings.copy(activeColor = color)
                ColorPickerTarget.BACKGROUND_COLOR -> settings.copy(backgroundColor = color)
            }
        }
        _state.update { it.copy(showColorPicker = null) }
    }

    // Font handlers
    private fun updateFontSize(size: Float) {
        updateSettings { it.copy(fontSize = size) }
    }

    private fun updateFontWeight(weight: androidx.compose.ui.text.font.FontWeight) {
        updateSettings { it.copy(fontWeight = weight) }
    }

    private fun updateFontFamily(family: androidx.compose.ui.text.font.FontFamily) {
        updateSettings { it.copy(fontFamily = family) }
    }

    private fun updateTextAlign(align: androidx.compose.ui.text.style.TextAlign) {
        updateSettings { it.copy(textAlign = align) }
    }

    // Layout handlers
    private fun updateLineSpacing(spacing: Float) {
        updateSettings { it.copy(lineSpacing = spacing) }
    }

    // Visual effects handlers
    private fun toggleGradient(enabled: Boolean) {
        updateSettings { it.copy(gradientEnabled = enabled) }
    }

    private fun updateGradientAngle(angle: Float) {
        updateSettings { it.copy(gradientAngle = angle) }
    }

    private fun toggleBlur(enabled: Boolean) {
        updateSettings { it.copy(blurEnabled = enabled) }
    }

    private fun updateBlurIntensity(intensity: Float) {
        updateSettings { it.copy(blurIntensity = intensity) }
    }

    // Character animation handlers
    private fun toggleCharAnimation(enabled: Boolean) {
        updateSettings { it.copy(charAnimEnabled = enabled) }
    }

    private fun updateCharMaxScale(scale: Float) {
        updateSettings { it.copy(charMaxScale = scale) }
    }

    private fun updateCharFloatOffset(offset: Float) {
        updateSettings { it.copy(charFloatOffset = offset) }
    }

    private fun updateCharRotation(degrees: Float) {
        updateSettings { it.copy(charRotationDegrees = degrees) }
    }

    // Line animation handlers
    private fun toggleLineAnimation(enabled: Boolean) {
        updateSettings { it.copy(lineAnimEnabled = enabled) }
    }

    private fun updateLineScaleOnPlay(scale: Float) {
        updateSettings { it.copy(lineScaleOnPlay = scale) }
    }

    // Pulse handlers
    private fun togglePulse(enabled: Boolean) {
        updateSettings { it.copy(pulseEnabled = enabled) }
    }

    private fun updatePulseMinScale(scale: Float) {
        updateSettings { it.copy(pulseMinScale = scale) }
    }

    private fun updatePulseMaxScale(scale: Float) {
        updateSettings { it.copy(pulseMaxScale = scale) }
    }

    // Preset handler
    private fun loadPreset(presetName: String) {
        viewModelScope.launch {
            val preset = when (presetName) {
                "Classic" -> LibraryPresets.Classic
                "Neon" -> LibraryPresets.Neon
                "Minimal" -> LibraryPresets.Minimal
                "Rainbow" -> LibraryPresets.Rainbow
                "Fire" -> LibraryPresets.Fire
                "Ocean" -> LibraryPresets.Ocean
                "Retro" -> LibraryPresets.Retro
                "Elegant" -> LibraryPresets.Elegant
                "Party" -> LibraryPresets.Party
                "Matrix" -> LibraryPresets.Matrix
                else -> null
            }

            preset?.let { config ->
                val newSettings = _state.value.settings.copy(
                    fontSize = config.visual.fontSize.value,
                    fontWeight = config.visual.fontWeight,
                    sungColor = config.visual.playedTextColor,
                    unsungColor = config.visual.upcomingTextColor,
                    activeColor = config.visual.playingTextColor,
                    backgroundColor = config.visual.backgroundColor,
                    charAnimEnabled = config.animation.enableCharacterAnimations,
                    lineAnimEnabled = config.animation.enableLineAnimations,
                    pulseEnabled = config.animation.enablePulse,
                    pulseMinScale = config.animation.pulseMinScale,
                    pulseMaxScale = config.animation.pulseMaxScale,
                    gradientEnabled = config.visual.gradientEnabled,
                    blurEnabled = config.effects.enableBlur,
                    blurIntensity = config.effects.blurIntensity
                )
                updateDemoSettingsUseCase(newSettings)
                _effect.send(DemoEffect.PresetLoaded)
            }
        }
    }

    private fun buildLibraryConfig(settings: DemoSettings): KaraokeLibraryConfig {
        return KaraokeLibraryConfig(
            visual = VisualConfig(
                fontSize = settings.fontSize.sp,
                fontWeight = settings.fontWeight,
                fontFamily = settings.fontFamily,
                textAlign = settings.textAlign,
                playingTextColor = settings.activeColor,
                playedTextColor = settings.sungColor,
                upcomingTextColor = settings.unsungColor,
                backgroundColor = settings.backgroundColor,
                gradientEnabled = settings.gradientEnabled,
                gradientAngle = settings.gradientAngle,
                colors = ColorConfig(
                    sung = settings.sungColor,
                    unsung = settings.unsungColor,
                    active = settings.activeColor
                )
            ),
            animation = AnimationConfig(
                enableCharacterAnimations = settings.charAnimEnabled,
                characterMaxScale = settings.charMaxScale,
                characterFloatOffset = settings.charFloatOffset,
                characterRotationDegrees = settings.charRotationDegrees,
                characterAnimationDuration = 800f,
                enableLineAnimations = settings.lineAnimEnabled,
                lineScaleOnPlay = settings.lineScaleOnPlay,
                lineAnimationDuration = 700f,
                enablePulse = settings.pulseEnabled,
                pulseMinScale = settings.pulseMinScale,
                pulseMaxScale = settings.pulseMaxScale
            ),
            effects = EffectsConfig(
                enableBlur = settings.blurEnabled,
                blurIntensity = settings.blurIntensity,
                upcomingLineBlur = (3 * settings.blurIntensity).dp,
                distantLineBlur = (6 * settings.blurIntensity).dp
            ),
            layout = LayoutConfig(
                viewerConfig = ViewerConfig(
                    type = getViewerType(settings.viewerTypeIndex)
                ),
                lineSpacing = settings.lineSpacing.dp,
                containerPadding = PaddingValues(8.dp)
            )
        )
    }

    private fun getViewerType(index: Int): ViewerType {
        return when (index) {
            0 -> ViewerType.CENTER_FOCUSED
            1 -> ViewerType.SMOOTH_SCROLL
            2 -> ViewerType.STACKED
            3 -> ViewerType.HORIZONTAL_PAGED
            4 -> ViewerType.WAVE_FLOW
            5 -> ViewerType.SPIRAL
            6 -> ViewerType.CAROUSEL_3D
            7 -> ViewerType.SPLIT_DUAL
            8 -> ViewerType.ELASTIC_BOUNCE
            9 -> ViewerType.FADE_THROUGH
            10 -> ViewerType.RADIAL_BURST
            11 -> ViewerType.FLIP_CARD
            else -> ViewerType.CENTER_FOCUSED
        }
    }
}
