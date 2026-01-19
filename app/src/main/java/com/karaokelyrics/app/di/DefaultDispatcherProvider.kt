package com.karaokelyrics.app.di

import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Default implementation of [DispatcherProvider] using standard Android dispatchers.
 */
class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
}
