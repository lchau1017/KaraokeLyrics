package com.kyrics.demo.data.repository

import com.kyrics.demo.domain.model.DemoSettings
import com.kyrics.demo.domain.repository.DemoSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of DemoSettingsRepository.
 * Currently stores settings in memory. Can be extended to use DataStore for persistence.
 */
@Singleton
class DemoSettingsRepositoryImpl @Inject constructor() : DemoSettingsRepository {

    private val _settings = MutableStateFlow(DemoSettings.Default)

    override fun getSettings(): Flow<DemoSettings> = _settings.asStateFlow()

    override suspend fun updateSettings(settings: DemoSettings) {
        _settings.value = settings
    }

    override suspend fun resetSettings() {
        _settings.value = DemoSettings.Default
    }
}
