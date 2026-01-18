package com.kyrics.demo.domain.usecase

import com.kyrics.demo.domain.model.DemoSettings
import com.kyrics.demo.domain.repository.DemoSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving demo settings.
 */
class GetDemoSettingsUseCase @Inject constructor(
    private val repository: DemoSettingsRepository
) {
    operator fun invoke(): Flow<DemoSettings> = repository.getSettings()
}
