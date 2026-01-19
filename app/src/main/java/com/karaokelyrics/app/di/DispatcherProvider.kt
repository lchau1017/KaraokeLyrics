package com.karaokelyrics.app.di

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Interface for providing coroutine dispatchers.
 * Allows for dependency injection and easier testing.
 */
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}
