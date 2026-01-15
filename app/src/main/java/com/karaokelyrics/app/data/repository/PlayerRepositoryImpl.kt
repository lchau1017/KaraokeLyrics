package com.karaokelyrics.app.data.repository

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.karaokelyrics.app.domain.repository.PlayerRepository
import com.karaokelyrics.app.data.service.PlaybackService
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PlayerRepository {

    private var mediaController: MediaController? = null
    private val controllerFuture: ListenableFuture<MediaController>
    private val _isPlaying = MutableStateFlow(false)
    private val mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val executor = Executors.newSingleThreadExecutor()

    init {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        initializeController()
    }

    private fun initializeController() {
        controllerFuture.addListener({
            mediaController = controllerFuture.get()
            mediaController?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }
            })
        }, executor)
    }

    override fun observePlaybackPosition(): Flow<Long> = flow {
        while (true) {
            mediaController?.let {
                emit(it.currentPosition)
            } ?: emit(0L)
            delay(100) // Update every 100ms for smooth animation
        }
    }.flowOn(Dispatchers.Main)

    override fun observeIsPlaying(): Flow<Boolean> = _isPlaying.asStateFlow()

    override suspend fun play() {
        withContext(Dispatchers.Main) {
            mediaController?.play()
        }
    }

    override suspend fun pause() {
        withContext(Dispatchers.Main) {
            mediaController?.pause()
        }
    }

    override suspend fun seekTo(position: Long) {
        withContext(Dispatchers.Main) {
            mediaController?.seekTo(position)
        }
    }

    override suspend fun loadMedia(assetPath: String) {
        withContext(Dispatchers.Main) {
            // Wait for controller to be ready
            var retries = 0
            while (mediaController == null && retries < 10) {
                delay(100)
                retries++
            }

            val mediaItem = MediaItem.fromUri("asset:///$assetPath")

            mediaController?.let { controller ->
                controller.setMediaItem(mediaItem)
                controller.repeatMode = Player.REPEAT_MODE_ONE
                controller.prepare()
                controller.play()
            }
        }
    }
}