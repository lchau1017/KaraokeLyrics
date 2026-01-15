package com.karaokelyrics.app.di

import android.content.Context
import com.karaokelyrics.app.data.repository.LyricsRepositoryImpl
import com.karaokelyrics.app.data.repository.PlayerRepositoryImpl
import com.karaokelyrics.app.domain.repository.LyricsRepository
import com.karaokelyrics.app.domain.repository.PlayerRepository
import com.karaokelyrics.app.domain.usecase.CalculateTextLayoutUseCase
import com.karaokelyrics.app.domain.usecase.DetermineAnimationTypeUseCase
import com.karaokelyrics.app.domain.usecase.GroupSyllablesIntoWordsUseCase
import com.karaokelyrics.app.domain.usecase.ProcessTextCharacteristicsUseCase
import com.karaokelyrics.app.domain.usecase.CoordinatePlaybackSyncUseCase
import com.karaokelyrics.app.domain.usecase.SyncLyricsUseCase
import com.karaokelyrics.app.presentation.ui.manager.LyricsLayoutManager
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

    // Domain Use Cases
    @Provides
    fun provideGroupSyllablesIntoWordsUseCase(): GroupSyllablesIntoWordsUseCase {
        return GroupSyllablesIntoWordsUseCase()
    }

    @Provides
    fun provideDetermineAnimationTypeUseCase(): DetermineAnimationTypeUseCase {
        return DetermineAnimationTypeUseCase()
    }

    @Provides
    fun provideProcessTextCharacteristicsUseCase(
        groupSyllablesIntoWordsUseCase: GroupSyllablesIntoWordsUseCase,
        determineAnimationTypeUseCase: DetermineAnimationTypeUseCase
    ): ProcessTextCharacteristicsUseCase {
        return ProcessTextCharacteristicsUseCase(
            groupSyllablesIntoWordsUseCase,
            determineAnimationTypeUseCase
        )
    }

    @Provides
    fun provideCalculateTextLayoutUseCase(
        processTextCharacteristicsUseCase: ProcessTextCharacteristicsUseCase
    ): CalculateTextLayoutUseCase {
        return CalculateTextLayoutUseCase(processTextCharacteristicsUseCase)
    }

    @Provides
    fun provideCoordinatePlaybackSyncUseCase(
        playerRepository: PlayerRepository,
        syncLyricsUseCase: SyncLyricsUseCase
    ): CoordinatePlaybackSyncUseCase {
        return CoordinatePlaybackSyncUseCase(playerRepository, syncLyricsUseCase)
    }

    // Presentation Managers
    @Provides
    fun provideLyricsLayoutManager(
        calculateTextLayoutUseCase: CalculateTextLayoutUseCase
    ): LyricsLayoutManager {
        return LyricsLayoutManager(calculateTextLayoutUseCase)
    }
}