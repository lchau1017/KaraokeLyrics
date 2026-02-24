package com.karaokelyrics.app.presentation.features.settings.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.domain.usecase.ObserveUserSettingsUseCase
import com.karaokelyrics.app.domain.usecase.UpdateUserSettingsUseCase
import com.karaokelyrics.app.presentation.features.settings.intent.SettingsIntent
import com.karaokelyrics.app.presentation.features.settings.mapper.SettingsUiMapper.toColorArgb
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * MVI ViewModel for Settings following Clean Architecture.
 * Single Responsibility: Manages settings state and handles intents.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val observeUserSettingsUseCase: ObserveUserSettingsUseCase,
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase
) : ViewModel() {

    data class SettingsState(val settings: UserSettings = UserSettings(), val isLoading: Boolean = false)

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private val _intents = Channel<SettingsIntent>(Channel.BUFFERED)

    init {
        observeSettings()
        processIntents()
    }

    fun handleIntent(intent: SettingsIntent) {
        viewModelScope.launch {
            _intents.send(intent)
        }
    }

    private fun processIntents() {
        viewModelScope.launch {
            _intents.receiveAsFlow().collect { intent ->
                when (intent) {
                    is SettingsIntent.UpdateLyricsColor -> updateLyricsColor(intent.color)
                    is SettingsIntent.UpdateBackgroundColor -> updateBackgroundColor(intent.color)
                    is SettingsIntent.UpdateFontSize -> updateFontSize(intent.fontSize)
                    is SettingsIntent.UpdateAnimationsEnabled -> updateAnimationsEnabled(intent.enabled)
                    is SettingsIntent.UpdateBlurEffectEnabled -> updateBlurEffectEnabled(intent.enabled)
                    is SettingsIntent.UpdateCharacterAnimationsEnabled -> updateCharacterAnimationsEnabled(intent.enabled)
                    is SettingsIntent.UpdateDarkMode -> updateDarkMode(intent.isDark)
                    is SettingsIntent.ResetToDefaults -> resetToDefaults()
                }
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            observeUserSettingsUseCase().collect { settings ->
                _state.update { it.copy(settings = settings) }
            }
        }
    }

    private suspend fun updateLyricsColor(color: Color) {
        updateUserSettingsUseCase.updateLyricsColor(color.toColorArgb())
    }

    private suspend fun updateBackgroundColor(color: Color) {
        updateUserSettingsUseCase.updateBackgroundColor(color.toColorArgb())
    }

    private suspend fun updateFontSize(fontSize: FontSize) {
        updateUserSettingsUseCase.updateFontSize(fontSize)
    }

    private suspend fun updateAnimationsEnabled(enabled: Boolean) {
        updateUserSettingsUseCase.updateAnimationsEnabled(enabled)
    }

    private suspend fun updateBlurEffectEnabled(enabled: Boolean) {
        updateUserSettingsUseCase.updateBlurEffectEnabled(enabled)
    }

    private suspend fun updateCharacterAnimationsEnabled(enabled: Boolean) {
        updateUserSettingsUseCase.updateCharacterAnimationsEnabled(enabled)
    }

    private suspend fun updateDarkMode(isDark: Boolean) {
        updateUserSettingsUseCase.updateDarkMode(isDark)
    }

    private suspend fun resetToDefaults() {
        updateUserSettingsUseCase.resetToDefaults()
    }
}
