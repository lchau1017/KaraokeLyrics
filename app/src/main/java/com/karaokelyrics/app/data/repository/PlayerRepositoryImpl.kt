package com.karaokelyrics.app.data.repository

import com.karaokelyrics.app.data.datasource.local.PlayerLocalDataSource
import com.karaokelyrics.app.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for player operations
 * Dependency Inversion Principle: Depends on abstraction (PlayerRepository)
 */
@Singleton
class PlayerRepositoryImpl @Inject constructor(
    private val playerDataSource: PlayerLocalDataSource
) : PlayerRepository {

    override fun observePlaybackPosition(): Flow<Long> {
        return playerDataSource.observePlaybackPosition()
    }

    override fun observeIsPlaying(): Flow<Boolean> {
        return playerDataSource.observeIsPlaying()
    }

    override fun observeDuration(): Flow<Long> {
        return playerDataSource.observeDuration()
    }

    override suspend fun play() {
        playerDataSource.play()
    }

    override suspend fun pause() {
        playerDataSource.pause()
    }

    override suspend fun seekTo(position: Long) {
        playerDataSource.seekTo(position)
    }

    override suspend fun loadMedia(assetPath: String) {
        playerDataSource.loadMedia(assetPath)
    }

    override fun getPlaybackPosition(): Flow<Long> {
        return playerDataSource.observePlaybackPosition()
    }

    override fun isPlaying(): Flow<Boolean> {
        return playerDataSource.observeIsPlaying()
    }
}