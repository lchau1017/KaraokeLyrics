package com.kyrics.demo.domain.usecase

import com.kyrics.demo.domain.model.DemoSettings
import com.kyrics.demo.domain.repository.DemoSettingsRepository
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
