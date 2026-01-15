package com.karaokelyrics.app.di

import android.content.Context
import com.karaokelyrics.app.data.repository.LyricsRepositoryImpl
import com.karaokelyrics.app.data.repository.PlayerRepositoryImpl
import com.karaokelyrics.app.domain.repository.LyricsRepository
import com.karaokelyrics.app.domain.repository.PlayerRepository
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
}