package com.kyrics.demo.di

import com.kyrics.demo.data.datasource.DemoLyricsDataSource
import com.kyrics.demo.data.repository.DemoSettingsRepositoryImpl
import com.kyrics.demo.domain.repository.DemoSettingsRepository
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
    fun provideDemoSettingsRepository(): DemoSettingsRepository = DemoSettingsRepositoryImpl()

    @Provides
    @Singleton
    fun provideDemoLyricsDataSource(): DemoLyricsDataSource = DemoLyricsDataSource()
}
