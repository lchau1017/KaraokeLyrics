package com.karaokelyrics.app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.karaokelyrics.app.data.datasource.local.LyricsLocalDataSource
import com.karaokelyrics.app.data.datasource.local.PlayerLocalDataSource
import com.karaokelyrics.app.data.datasource.local.SettingsLocalDataSource
import com.karaokelyrics.app.data.repository.LyricsRepositoryImpl
import com.karaokelyrics.app.data.repository.PlayerRepositoryImpl
import com.karaokelyrics.app.data.repository.SettingsRepositoryImpl
import com.karaokelyrics.app.domain.repository.LyricsRepository
import com.karaokelyrics.app.domain.repository.PlayerRepository
import com.karaokelyrics.app.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module for data layer
 * Open/Closed Principle: Open for extension through interfaces
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindLyricsRepository(
        lyricsRepositoryImpl: LyricsRepositoryImpl
    ): LyricsRepository

    @Binds
    abstract fun bindPlayerRepository(
        playerRepositoryImpl: PlayerRepositoryImpl
    ): PlayerRepository

    @Binds
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

    companion object {
        @Provides
        @Singleton
        fun provideSettingsLocalDataSource(
            dataStore: DataStore<Preferences>
        ): SettingsLocalDataSource {
            return SettingsLocalDataSource(dataStore)
        }
    }
}