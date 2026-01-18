package com.kyrics.demo.domain.usecase

import com.kyrics.demo.domain.model.DemoSettings
import com.kyrics.demo.domain.repository.DemoSettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving demo settings.
 */
class GetDemoSettingsUseCase @Inject constructor(private val repository: DemoSettingsRepository) {
    operator fun invoke(): Flow<DemoSettings> = repository.getSettings()
}
