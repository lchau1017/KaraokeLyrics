package com.karaokelyrics.app.di

import android.content.Context
import com.karaokelyrics.app.data.repository.LyricsRepositoryImpl
import com.karaokelyrics.app.data.repository.SettingsRepositoryImpl
import com.karaokelyrics.app.domain.repository.LyricsRepository
import com.karaokelyrics.app.domain.repository.SettingsRepository
import com.karaokelyrics.app.domain.usecase.LoadLyricsUseCase
import com.karaokelyrics.app.domain.usecase.ObserveUserSettingsUseCase
import com.karaokelyrics.app.domain.usecase.ParseTtmlUseCase
import com.karaokelyrics.app.domain.usecase.ProcessLyricsDataUseCase
import com.karaokelyrics.app.domain.usecase.SyncLyricsUseCase
import com.karaokelyrics.app.domain.usecase.UpdateUserSettingsUseCase
import com.karaokelyrics.app.presentation.features.lyrics.coordinator.PlaybackSyncCoordinator
import com.karaokelyrics.app.presentation.player.MediaPlayerController
import com.karaokelyrics.app.presentation.player.PlayerController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Data Sources
    @Provides
    @Singleton
    fun provideAssetDataSource(@ApplicationContext context: Context): com.karaokelyrics.app.data.source.local.AssetDataSource =
        com.karaokelyrics.app.data.source.local.AssetDataSource(context)

    @Provides
    @Singleton
    fun provideMediaContentProvider(): com.karaokelyrics.app.data.source.local.MediaContentProvider =
        com.karaokelyrics.app.data.source.local.MediaContentProvider()

    @Provides
    @Singleton
    fun providePreferencesDataSource(@ApplicationContext context: Context): com.karaokelyrics.app.data.source.local.PreferencesDataSource =
        com.karaokelyrics.app.data.source.local.PreferencesDataSource(context)

    // Repositories
    @Provides
    @Singleton
    fun provideLyricsRepository(
        assetDataSource: com.karaokelyrics.app.data.source.local.AssetDataSource,
        mediaContentProvider: com.karaokelyrics.app.data.source.local.MediaContentProvider
    ): LyricsRepository = LyricsRepositoryImpl(assetDataSource, mediaContentProvider)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        preferencesDataSource: com.karaokelyrics.app.data.source.local.PreferencesDataSource
    ): SettingsRepository = SettingsRepositoryImpl(preferencesDataSource)

    @Provides
    @Singleton
    fun providePlayerController(@ApplicationContext context: Context): PlayerController = MediaPlayerController(context)

    // Domain Use Cases

    @Provides
    fun provideSyncLyricsUseCase(): SyncLyricsUseCase = SyncLyricsUseCase()

    @Provides
    fun providePlaybackSyncCoordinator(playerController: PlayerController, syncLyricsUseCase: SyncLyricsUseCase): PlaybackSyncCoordinator =
        PlaybackSyncCoordinator(playerController, syncLyricsUseCase)

    @Provides
    @Singleton
    fun provideTtmlParser(): com.karaokelyrics.app.domain.parser.TtmlParser = com.karaokelyrics.app.data.parser.TtmlParserImpl()

    @Provides
    fun provideParseTtmlUseCase(ttmlParser: com.karaokelyrics.app.domain.parser.TtmlParser): ParseTtmlUseCase = ParseTtmlUseCase(ttmlParser)

    @Provides
    fun provideProcessLyricsDataUseCase(): ProcessLyricsDataUseCase = ProcessLyricsDataUseCase()

    @Provides
    fun provideLoadLyricsUseCase(
        lyricsRepository: LyricsRepository,
        parseTtmlUseCase: ParseTtmlUseCase,
        processLyricsDataUseCase: ProcessLyricsDataUseCase
    ): LoadLyricsUseCase = LoadLyricsUseCase(lyricsRepository, parseTtmlUseCase, processLyricsDataUseCase)

    @Provides
    fun provideObserveUserSettingsUseCase(settingsRepository: SettingsRepository): ObserveUserSettingsUseCase =
        ObserveUserSettingsUseCase(settingsRepository)

    @Provides
    fun provideUpdateUserSettingsUseCase(settingsRepository: SettingsRepository): UpdateUserSettingsUseCase =
        UpdateUserSettingsUseCase(settingsRepository)

    @Provides
    fun provideGetDefaultMediaContentUseCase(
        lyricsRepository: LyricsRepository
    ): com.karaokelyrics.app.domain.usecase.GetDefaultMediaContentUseCase =
        com.karaokelyrics.app.domain.usecase.GetDefaultMediaContentUseCase(lyricsRepository)

    @Provides
    fun provideGetAvailableMediaContentUseCase(
        lyricsRepository: LyricsRepository
    ): com.karaokelyrics.app.domain.usecase.GetAvailableMediaContentUseCase =
        com.karaokelyrics.app.domain.usecase.GetAvailableMediaContentUseCase(lyricsRepository)

    // No presentation managers needed - clean architecture!
}
