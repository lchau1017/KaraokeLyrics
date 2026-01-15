package com.karaokelyrics.app.domain.usecase.settings

import com.karaokelyrics.app.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use case for resetting settings to defaults
 * Single Responsibility: Reset all user preferences to default values
 */
class ResetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke() {
        settingsRepository.resetToDefaults()
    }
}