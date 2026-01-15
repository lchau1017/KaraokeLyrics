package com.karaokelyrics.app.di

import com.karaokelyrics.app.domain.repository.LyricsRepository
import com.karaokelyrics.app.domain.repository.PlayerRepository
import com.karaokelyrics.app.domain.repository.SettingsRepository
import com.karaokelyrics.app.domain.usecase.SyncLyricsUseCase
import com.karaokelyrics.app.domain.usecase.lyrics.LoadLyricsFromAssetUseCase
import com.karaokelyrics.app.domain.usecase.lyrics.LoadLyricsFromFileUseCase
import com.karaokelyrics.app.domain.usecase.lyrics.ObserveCurrentLyricsUseCase
import com.karaokelyrics.app.domain.usecase.lyrics.SyncLyricsWithPlaybackUseCase
import com.karaokelyrics.app.domain.usecase.player.LoadMediaUseCase
import com.karaokelyrics.app.domain.usecase.player.ObservePlaybackPositionUseCase
import com.karaokelyrics.app.domain.usecase.player.ObservePlayerStateUseCase
import com.karaokelyrics.app.domain.usecase.player.PlayPauseUseCase
import com.karaokelyrics.app.domain.usecase.player.SeekToPositionUseCase
import com.karaokelyrics.app.domain.usecase.settings.ObserveUserSettingsUseCase
import com.karaokelyrics.app.domain.usecase.settings.ResetSettingsUseCase
import com.karaokelyrics.app.domain.usecase.settings.UpdateUserSettingsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module for domain layer use cases
 * Single Responsibility: Each use case has one specific task
 */
@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    // Player Use Cases
    @Provides
    @Singleton
    fun providePlayPauseUseCase(
        playerRepository: PlayerRepository
    ): PlayPauseUseCase = PlayPauseUseCase(playerRepository)

    @Provides
    @Singleton
    fun provideSeekToPositionUseCase(
        playerRepository: PlayerRepository
    ): SeekToPositionUseCase = SeekToPositionUseCase(playerRepository)

    @Provides
    @Singleton
    fun provideObservePlaybackPositionUseCase(
        playerRepository: PlayerRepository
    ): ObservePlaybackPositionUseCase = ObservePlaybackPositionUseCase(playerRepository)

    @Provides
    @Singleton
    fun provideObservePlayerStateUseCase(
        playerRepository: PlayerRepository
    ): ObservePlayerStateUseCase = ObservePlayerStateUseCase(playerRepository)

    @Provides
    @Singleton
    fun provideLoadMediaUseCase(
        playerRepository: PlayerRepository
    ): LoadMediaUseCase = LoadMediaUseCase(playerRepository)

    // Lyrics Use Cases
    @Provides
    @Singleton
    fun provideLoadLyricsFromAssetUseCase(
        lyricsRepository: LyricsRepository
    ): LoadLyricsFromAssetUseCase = LoadLyricsFromAssetUseCase(lyricsRepository)

    @Provides
    @Singleton
    fun provideLoadLyricsFromFileUseCase(
        lyricsRepository: LyricsRepository
    ): LoadLyricsFromFileUseCase = LoadLyricsFromFileUseCase(lyricsRepository)

    @Provides
    @Singleton
    fun provideObserveCurrentLyricsUseCase(
        lyricsRepository: LyricsRepository
    ): ObserveCurrentLyricsUseCase = ObserveCurrentLyricsUseCase(lyricsRepository)

    @Provides
    @Singleton
    fun provideSyncLyricsUseCase(): SyncLyricsUseCase = SyncLyricsUseCase()

    @Provides
    @Singleton
    fun provideSyncLyricsWithPlaybackUseCase(
        lyricsRepository: LyricsRepository,
        playerRepository: PlayerRepository,
        syncLyricsUseCase: SyncLyricsUseCase
    ): SyncLyricsWithPlaybackUseCase = SyncLyricsWithPlaybackUseCase(
        lyricsRepository,
        playerRepository,
        syncLyricsUseCase
    )

    // Settings Use Cases
    @Provides
    @Singleton
    fun provideObserveUserSettingsUseCase(
        settingsRepository: SettingsRepository
    ): ObserveUserSettingsUseCase = ObserveUserSettingsUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideUpdateUserSettingsUseCase(
        settingsRepository: SettingsRepository
    ): UpdateUserSettingsUseCase = UpdateUserSettingsUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideResetSettingsUseCase(
        settingsRepository: SettingsRepository
    ): ResetSettingsUseCase = ResetSettingsUseCase(settingsRepository)
}