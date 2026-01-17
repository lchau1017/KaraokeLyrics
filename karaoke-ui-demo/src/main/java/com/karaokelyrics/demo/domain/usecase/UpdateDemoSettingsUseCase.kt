package com.karaokelyrics.demo.domain.usecase

import com.karaokelyrics.demo.domain.model.DemoSettings
import com.karaokelyrics.demo.domain.repository.DemoSettingsRepository
import javax.inject.Inject

/**
 * Use case for updating demo settings.
 */
class UpdateDemoSettingsUseCase @Inject constructor(
    private val repository: DemoSettingsRepository
) {
    suspend operator fun invoke(settings: DemoSettings) {
        repository.updateSettings(settings)
    }
}
