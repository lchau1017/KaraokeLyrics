package com.karaokelyrics.app.data.source.local

import android.content.Context
import android.content.res.AssetFileDescriptor
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Local data source for accessing files from assets.
 * Single responsibility: Read files from the assets folder.
 */
@Singleton
class AssetDataSource @Inject constructor(@ApplicationContext private val context: Context) {
    /**
     * Read text file content from assets.
     */
    suspend fun readTextFile(fileName: String): Result<List<String>> = withContext(Dispatchers.IO) {
        runCatching {
            context.assets.open(fileName).bufferedReader().use {
                it.readLines()
            }
        }
    }

    /**
     * Get asset file descriptor for media files.
     * Used for audio/video files that need file descriptors.
     */
    suspend fun getAssetFileDescriptor(fileName: String): Result<AssetFileDescriptor> = withContext(Dispatchers.IO) {
        runCatching {
            context.assets.openFd(fileName)
        }
    }

    /**
     * List all files in a specific asset directory.
     */
    suspend fun listFiles(directory: String = ""): Result<List<String>> = withContext(Dispatchers.IO) {
        runCatching {
            context.assets.list(directory)?.toList() ?: emptyList()
        }
    }

    /**
     * Check if a file exists in assets.
     */
    suspend fun fileExists(fileName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            context.assets.open(fileName).use { true }
        } catch (e: Exception) {
            false
        }
    }
}
