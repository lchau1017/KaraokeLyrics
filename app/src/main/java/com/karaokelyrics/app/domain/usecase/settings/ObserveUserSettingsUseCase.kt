package com.karaokelyrics.app.domain.usecase.settings

import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing user settings
 * Single Responsibility: Provide settings updates
 */
class ObserveUserSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<UserSettings> {
        return settingsRepository.observeUserSettings()
    }
}