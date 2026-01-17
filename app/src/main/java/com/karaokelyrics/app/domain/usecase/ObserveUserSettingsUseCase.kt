package com.karaokelyrics.app.domain.usecase

import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Domain use case for observing user settings changes
 */
class ObserveUserSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<UserSettings> {
        return settingsRepository.getUserSettings()
    }
}