package com.karaokelyrics.app.presentation.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.karaokelyrics.app.di.DispatcherProvider
import com.karaokelyrics.app.infrastructure.service.PlaybackService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Media player controller for presentation layer.
 * Handles Android MediaController and MediaSession interaction.
 */
@Singleton
class MediaPlayerController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatcherProvider: DispatcherProvider
) : PlayerController {

    private var mediaController: MediaController? = null
    private val controllerFuture: ListenableFuture<MediaController>
    private val _isPlaying = MutableStateFlow(false)
    private val _playbackPosition = MutableStateFlow(0L)
    private val executor = Executors.newSingleThreadExecutor()
    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.main)

    init {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        initializeController()
        startPositionPolling()
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

    private fun startPositionPolling() {
        scope.launch {
            while (true) {
                if (_isPlaying.value) {
                    mediaController?.let {
                        _playbackPosition.value = it.currentPosition
                    }
                }
                delay(100)
            }
        }
    }

    override fun observePlaybackPosition(): Flow<Long> = _playbackPosition.asStateFlow()

    override fun observeIsPlaying(): Flow<Boolean> = _isPlaying.asStateFlow()

    override suspend fun play() {
        withContext(dispatcherProvider.main) {
            mediaController?.play()
        }
    }

    override suspend fun pause() {
        withContext(dispatcherProvider.main) {
            mediaController?.pause()
        }
    }

    override suspend fun seekTo(position: Long) {
        withContext(dispatcherProvider.main) {
            mediaController?.seekTo(position)
            // Update position immediately after seek for responsive UI
            mediaController?.let {
                _playbackPosition.value = it.currentPosition
            }
        }
    }

    override suspend fun loadMedia(assetPath: String) {
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
