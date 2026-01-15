package com.karaokelyrics.app.di

import com.karaokelyrics.app.domain.usecase.lyrics.DetermineAnimationTypeUseCase
import com.karaokelyrics.app.domain.usecase.lyrics.GroupSyllablesIntoWordsUseCase
import com.karaokelyrics.app.presentation.ui.manager.LyricsLayoutManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Dependency injection module for presentation layer
 */
@Module
@InstallIn(ViewModelComponent::class)
object PresentationModule {

    @Provides
    @ViewModelScoped
    fun provideLyricsLayoutManager(
        groupSyllablesIntoWordsUseCase: GroupSyllablesIntoWordsUseCase,
        determineAnimationTypeUseCase: DetermineAnimationTypeUseCase
    ): LyricsLayoutManager {
        return LyricsLayoutManager(
            groupSyllablesIntoWordsUseCase,
            determineAnimationTypeUseCase
        )
    }
}