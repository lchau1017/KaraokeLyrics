package com.karaokelyrics.app.data.repository

import android.content.Context
import com.karaokelyrics.app.domain.repository.LyricsRepository
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.data.parser.TtmlParser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LyricsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LyricsRepository {

    private val ttmlParser = TtmlParser()
    private val _currentLyrics = MutableStateFlow<SyncedLyrics?>(null)

    override suspend fun loadLyricsFromAsset(fileName: String): Result<SyncedLyrics> =
        withContext(Dispatchers.IO) {
            runCatching {
                val lyricsData = context.assets.open(fileName).bufferedReader().use {
                    it.readLines()
                }
                val parsedLyrics = ttmlParser.parse(lyricsData)
                _currentLyrics.value = parsedLyrics
                parsedLyrics
            }
        }

    override fun getCurrentLyrics(): Flow<SyncedLyrics?> = _currentLyrics.asStateFlow()
}