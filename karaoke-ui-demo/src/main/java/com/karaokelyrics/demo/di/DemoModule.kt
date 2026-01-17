package com.karaokelyrics.demo.di

import com.karaokelyrics.demo.data.datasource.DemoLyricsDataSource
import com.karaokelyrics.demo.data.repository.DemoSettingsRepositoryImpl
import com.karaokelyrics.demo.domain.repository.DemoSettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DemoModule {

    @Provides
    @Singleton
    fun provideDemoSettingsRepository(): DemoSettingsRepository =
        DemoSettingsRepositoryImpl()

    @Provides
    @Singleton
    fun provideDemoLyricsDataSource(): DemoLyricsDataSource =
        DemoLyricsDataSource()
}
