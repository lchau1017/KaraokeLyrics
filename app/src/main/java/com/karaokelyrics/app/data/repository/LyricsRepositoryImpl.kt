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
                android.util.Log.d("LyricsRepository", "Starting to parse lyrics...")
                val parsedLyrics = ttmlParser.parse(lyricsData)
                android.util.Log.d("LyricsRepository", "Parsed ${parsedLyrics.lines.size} lines")
                parsedLyrics.lines.take(5).forEach { line ->
                    if (line is com.karaokelyrics.app.domain.model.karaoke.KaraokeLine) {
                        android.util.Log.d("LyricsRepository", "Line: ${line.content.take(50)}, syllables: ${line.syllables.size}")
                    }
                }
                _currentLyrics.value = parsedLyrics
                parsedLyrics
            }
        }

    override fun getCurrentLyrics(): Flow<SyncedLyrics?> = _currentLyrics.asStateFlow()
}