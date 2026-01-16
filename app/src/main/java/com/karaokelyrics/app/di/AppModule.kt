package com.karaokelyrics.app.di

import android.content.Context
import com.karaokelyrics.app.data.repository.LyricsRepositoryImpl
import com.karaokelyrics.app.data.repository.PlayerRepositoryImpl
import com.karaokelyrics.app.data.repository.SettingsRepositoryImpl
import com.karaokelyrics.app.domain.repository.LyricsRepository
import com.karaokelyrics.app.domain.repository.PlayerRepository
import com.karaokelyrics.app.domain.repository.SettingsRepository
import com.karaokelyrics.app.domain.usecase.DetermineAnimationTypeUseCase
import com.karaokelyrics.app.domain.usecase.GroupSyllablesIntoWordsUseCase
import com.karaokelyrics.app.domain.usecase.CoordinatePlaybackSyncUseCase
import com.karaokelyrics.app.domain.usecase.SyncLyricsUseCase
import com.karaokelyrics.app.domain.usecase.ParseTtmlUseCase
import com.karaokelyrics.app.domain.usecase.ProcessLyricsDataUseCase
import com.karaokelyrics.app.domain.usecase.LoadLyricsUseCase
import com.karaokelyrics.app.domain.usecase.ObserveUserSettingsUseCase
import com.karaokelyrics.app.domain.usecase.UpdateUserSettingsUseCase
import com.karaokelyrics.app.domain.usecase.theme.GetCurrentThemeColorsUseCase
import com.karaokelyrics.app.domain.usecase.animation.DetermineAnimationConfigUseCase
import com.karaokelyrics.app.data.parser.TtmlXmlParser
import com.karaokelyrics.app.data.parser.TimeFormatParser
import com.karaokelyrics.app.data.factory.LyricsFactory
import com.karaokelyrics.app.presentation.features.lyrics.handler.LyricsHandler
import com.karaokelyrics.app.presentation.features.lyrics.handler.PlayerHandler
import com.karaokelyrics.app.presentation.features.lyrics.handler.SettingsHandler
import com.karaokelyrics.app.presentation.animation.AnimationStrategyRegistry
import com.karaokelyrics.app.presentation.animation.strategy.*
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
    fun provideDetermineAnimationTypeUseCase(): DetermineAnimationTypeUseCase {
        return DetermineAnimationTypeUseCase()
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

    // Parser components
    @Provides
    fun provideTimeFormatParser(): TimeFormatParser {
        return TimeFormatParser()
    }

    @Provides
    fun provideTtmlXmlParser(
        timeFormatParser: TimeFormatParser
    ): TtmlXmlParser {
        return TtmlXmlParser(timeFormatParser)
    }

    @Provides
    fun provideLyricsFactory(): LyricsFactory {
        return LyricsFactory()
    }

    @Provides
    fun provideParseTtmlUseCase(
        ttmlXmlParser: TtmlXmlParser,
        lyricsFactory: LyricsFactory
    ): ParseTtmlUseCase {
        return ParseTtmlUseCase(ttmlXmlParser, lyricsFactory)
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

    // Theme and animation use cases
    @Provides
    fun provideGetCurrentThemeColorsUseCase(): GetCurrentThemeColorsUseCase {
        return GetCurrentThemeColorsUseCase()
    }

    @Provides
    fun provideDetermineAnimationConfigUseCase(): DetermineAnimationConfigUseCase {
        return DetermineAnimationConfigUseCase()
    }

    // Presentation Handlers
    @Provides
    fun provideLyricsHandler(
        loadLyricsUseCase: LoadLyricsUseCase,
        syncLyricsUseCase: SyncLyricsUseCase
    ): LyricsHandler {
        return LyricsHandler(loadLyricsUseCase, syncLyricsUseCase)
    }

    @Provides
    fun providePlayerHandler(
        playerRepository: PlayerRepository
    ): PlayerHandler {
        return PlayerHandler(playerRepository)
    }

    @Provides
    fun provideSettingsHandler(
        observeUserSettingsUseCase: ObserveUserSettingsUseCase,
        updateUserSettingsUseCase: UpdateUserSettingsUseCase,
        getCurrentThemeColorsUseCase: GetCurrentThemeColorsUseCase
    ): SettingsHandler {
        return SettingsHandler(
            observeUserSettingsUseCase,
            updateUserSettingsUseCase,
            getCurrentThemeColorsUseCase
        )
    }

    // Animation Strategies
    @Provides
    fun provideBounceAnimationStrategy(): BounceAnimationStrategy {
        return BounceAnimationStrategy()
    }

    @Provides
    fun provideSwellAnimationStrategy(): SwellAnimationStrategy {
        return SwellAnimationStrategy()
    }

    @Provides
    fun provideDipAndRiseAnimationStrategy(): DipAndRiseAnimationStrategy {
        return DipAndRiseAnimationStrategy()
    }

    @Provides
    fun provideSimpleAnimationStrategy(): SimpleAnimationStrategy {
        return SimpleAnimationStrategy()
    }

    @Provides
    @Singleton
    fun provideAnimationStrategyRegistry(
        bounceStrategy: BounceAnimationStrategy,
        swellStrategy: SwellAnimationStrategy,
        dipAndRiseStrategy: DipAndRiseAnimationStrategy,
        simpleStrategy: SimpleAnimationStrategy
    ): AnimationStrategyRegistry {
        return AnimationStrategyRegistry(
            bounceStrategy,
            swellStrategy,
            dipAndRiseStrategy,
            simpleStrategy
        )
    }

    // No presentation managers needed - clean architecture!
}