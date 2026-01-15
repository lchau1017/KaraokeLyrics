package com.karaokelyrics.app.data.datasource.local

import android.content.Context
import com.karaokelyrics.app.data.parser.TtmlParser
import com.karaokelyrics.app.domain.model.SyncedLyrics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data source for lyrics
 * Single Responsibility: Load lyrics from local assets
 */
@Singleton
class LyricsLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ttmlParser: TtmlParser
) {
    suspend fun loadFromAsset(fileName: String): Result<SyncedLyrics> {
        return try {
            val inputStream = context.assets.open(fileName)
            val content = inputStream.bufferedReader().use { it.readText() }
            val lyrics = ttmlParser.parse(content)
            Result.success(lyrics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loadFromFile(filePath: String): Result<SyncedLyrics> {
        return try {
            val content = java.io.File(filePath).readText()
            val lyrics = ttmlParser.parse(content)
            Result.success(lyrics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}