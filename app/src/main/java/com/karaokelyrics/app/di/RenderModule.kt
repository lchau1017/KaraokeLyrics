package com.karaokelyrics.app.di

import com.karaokelyrics.app.presentation.features.lyrics.calculator.*
import com.karaokelyrics.app.presentation.features.lyrics.calculator.impl.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger module for render calculator bindings.
 * This follows industry best practice of using interfaces and DI.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RenderModule {

    @Binds
    @Singleton
    abstract fun bindRenderCalculator(
        impl: DefaultRenderCalculator
    ): RenderCalculator

    @Binds
    @Singleton
    abstract fun bindTimingCalculator(
        impl: DefaultTimingCalculator
    ): TimingCalculator

    @Binds
    @Singleton
    abstract fun bindVisualCalculator(
        impl: DefaultVisualCalculator
    ): VisualCalculator

    @Binds
    @Singleton
    abstract fun bindInteractionCalculator(
        impl: DefaultInteractionCalculator
    ): InteractionCalculator

    @Binds
    @Singleton
    abstract fun bindInstructionCalculator(
        impl: DefaultInstructionCalculator
    ): InstructionCalculator
}