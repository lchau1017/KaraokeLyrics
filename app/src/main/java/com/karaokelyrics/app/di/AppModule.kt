package com.karaokelyrics.app.di

import android.content.Context
import com.karaokelyrics.app.data.repository.LyricsRepositoryImpl
import com.karaokelyrics.app.data.repository.PlayerRepositoryImpl
import com.karaokelyrics.app.data.repository.SettingsRepositoryImpl
import com.karaokelyrics.app.domain.repository.LyricsRepository
import com.karaokelyrics.app.domain.repository.PlayerRepository
import com.karaokelyrics.app.domain.repository.SettingsRepository
import com.karaokelyrics.app.presentation.shared.animation.AnimationDecisionCalculator
import com.karaokelyrics.app.domain.usecase.GroupSyllablesIntoWordsUseCase
import com.karaokelyrics.app.domain.usecase.CoordinatePlaybackSyncUseCase
import com.karaokelyrics.app.domain.usecase.SyncLyricsUseCase
import com.karaokelyrics.app.domain.usecase.ParseTtmlUseCase
import com.karaokelyrics.app.domain.usecase.ProcessLyricsDataUseCase
import com.karaokelyrics.app.domain.usecase.LoadLyricsUseCase
import com.karaokelyrics.app.domain.usecase.ObserveUserSettingsUseCase
import com.karaokelyrics.app.domain.usecase.UpdateUserSettingsUseCase
// No managers needed anymore!
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLyricsRepository(
        @ApplicationContext context: Context
    ): LyricsRepository = LyricsRepositoryImpl(context)

    @Provides
    @Singleton
    fun providePlayerRepository(
        @ApplicationContext context: Context
    ): PlayerRepository = PlayerRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository = SettingsRepositoryImpl(context)

    // Domain Use Cases
    @Provides
    fun provideGroupSyllablesIntoWordsUseCase(): GroupSyllablesIntoWordsUseCase {
        return GroupSyllablesIntoWordsUseCase()
    }

    @Provides
    fun provideAnimationDecisionCalculator(): AnimationDecisionCalculator {
        return AnimationDecisionCalculator()
    }

    // Presentation Helpers removed - now created locally in components
    // TextCharacteristicsProcessor and TextLayoutCalculator no longer needed in DI

    @Provides
    fun provideSyncLyricsUseCase(): SyncLyricsUseCase {
        return SyncLyricsUseCase()
    }

    @Provides
    fun provideCoordinatePlaybackSyncUseCase(
        playerRepository: PlayerRepository,
        syncLyricsUseCase: SyncLyricsUseCase
    ): CoordinatePlaybackSyncUseCase {
        return CoordinatePlaybackSyncUseCase(playerRepository, syncLyricsUseCase)
    }

    @Provides
    fun provideParseTtmlUseCase(): ParseTtmlUseCase {
        return ParseTtmlUseCase()
    }

    @Provides
    fun provideProcessLyricsDataUseCase(): ProcessLyricsDataUseCase {
        return ProcessLyricsDataUseCase()
    }

    @Provides
    fun provideLoadLyricsUseCase(
        lyricsRepository: LyricsRepository,
        parseTtmlUseCase: ParseTtmlUseCase,
        processLyricsDataUseCase: ProcessLyricsDataUseCase
    ): LoadLyricsUseCase {
        return LoadLyricsUseCase(lyricsRepository, parseTtmlUseCase, processLyricsDataUseCase)
    }

    @Provides
    fun provideObserveUserSettingsUseCase(
        settingsRepository: SettingsRepository
    ): ObserveUserSettingsUseCase {
        return ObserveUserSettingsUseCase(settingsRepository)
    }

    @Provides
    fun provideUpdateUserSettingsUseCase(
        settingsRepository: SettingsRepository
    ): UpdateUserSettingsUseCase {
        return UpdateUserSettingsUseCase(settingsRepository)
    }

    // No presentation managers needed - clean architecture!
}