package com.karaokelyrics.demo.domain.repository

import com.karaokelyrics.demo.domain.model.DemoSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing demo settings.
 */
interface DemoSettingsRepository {

    /**
     * Get the current demo settings as a Flow.
     */
    fun getSettings(): Flow<DemoSettings>

    /**
     * Update the demo settings.
     */
    suspend fun updateSettings(settings: DemoSettings)

    /**
     * Reset settings to default values.
     */
    suspend fun resetSettings()
}
