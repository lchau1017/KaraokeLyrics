package com.karaokelyrics.app.presentation.features.settings.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.domain.usecase.ObserveUserSettingsUseCase
import com.karaokelyrics.app.domain.usecase.UpdateUserSettingsUseCase
import com.karaokelyrics.app.presentation.features.settings.mapper.SettingsUiMapper.toColorArgb
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Settings screen.
 * Single Responsibility: Only manages user settings.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val observeUserSettingsUseCase: ObserveUserSettingsUseCase,
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase
) : ViewModel() {

    val settings: StateFlow<UserSettings> = observeUserSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings()
        )

    fun updateLyricsColor(color: Color) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateLyricsColor(color.toColorArgb())
        }
    }

    fun updateBackgroundColor(color: Color) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateBackgroundColor(color.toColorArgb())
        }
    }

    fun updateFontSize(fontSize: FontSize) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateFontSize(fontSize)
        }
    }

    fun updateAnimationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateAnimationsEnabled(enabled)
        }
    }

    fun updateBlurEffectEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateBlurEffectEnabled(enabled)
        }
    }

    fun updateCharacterAnimationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateCharacterAnimationsEnabled(enabled)
        }
    }

    fun updateDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateDarkMode(isDark)
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            updateUserSettingsUseCase.resetToDefaults()
        }
    }
}