package com.karaokelyrics.app.presentation.features.player.viewmodel

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.karaokelyrics.app.presentation.features.player.effect.PlayerEffect
import com.karaokelyrics.app.presentation.features.player.intent.PlayerIntent
import com.karaokelyrics.app.presentation.player.PlayerController
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var playerController: PlayerController
    private lateinit var isPlayingFlow: MutableStateFlow<Boolean>
    private lateinit var positionFlow: MutableStateFlow<Long>
    private lateinit var viewModel: PlayerViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        isPlayingFlow = MutableStateFlow(false)
        positionFlow = MutableStateFlow(0L)

        playerController = mockk(relaxed = true) {
            every { observeIsPlaying() } returns isPlayingFlow
            every { observePlaybackPosition() } returns positionFlow
        }

        viewModel = PlayerViewModel(playerController = playerController)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ==================== Initial State Tests ====================

    @Test
    fun `initial state is not playing with zero position`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.state.value.isPlaying).isFalse()
        assertThat(viewModel.state.value.currentPosition).isEqualTo(0L)
        assertThat(viewModel.state.value.duration).isEqualTo(0L)
    }

    // ==================== PlayPause Intent Tests ====================

    @Test
    fun `PlayPause intent calls play when not playing`() = runTest {
        isPlayingFlow.value = false
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effects.test {
            viewModel.handleIntent(PlayerIntent.PlayPause)
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { playerController.play() }
            assertThat(awaitItem()).isEqualTo(PlayerEffect.PlaybackStarted)
        }
    }

    @Test
    fun `PlayPause intent calls pause when playing`() = runTest {
        isPlayingFlow.value = true
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effects.test {
            viewModel.handleIntent(PlayerIntent.PlayPause)
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { playerController.pause() }
            assertThat(awaitItem()).isEqualTo(PlayerEffect.PlaybackPaused)
        }
    }

    // ==================== Seek Intent Tests ====================

    @Test
    fun `SeekToPosition intent calls seekTo and emits effect`() = runTest {
        val seekPosition = 5000L

        viewModel.effects.test {
            viewModel.handleIntent(PlayerIntent.SeekToPosition(seekPosition))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { playerController.seekTo(seekPosition) }
            assertThat(awaitItem()).isEqualTo(PlayerEffect.SeekCompleted(seekPosition))
        }
    }

    @Test
    fun `SeekToPosition with zero position works correctly`() = runTest {
        viewModel.effects.test {
            viewModel.handleIntent(PlayerIntent.SeekToPosition(0L))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { playerController.seekTo(0L) }
            assertThat(awaitItem()).isEqualTo(PlayerEffect.SeekCompleted(0L))
        }
    }

    // ==================== LoadMedia Intent Tests ====================

    @Test
    fun `LoadMedia intent calls loadMedia on controller`() = runTest {
        val fileName = "test_audio.mp3"

        viewModel.handleIntent(PlayerIntent.LoadMedia(fileName))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { playerController.loadMedia(fileName) }
    }

    @Test
    fun `LoadMedia intent emits error effect on failure`() = runTest {
        val fileName = "nonexistent.mp3"
        val errorMessage = "File not found"

        coEvery { playerController.loadMedia(fileName) } throws Exception(errorMessage)

        viewModel.effects.test {
            viewModel.handleIntent(PlayerIntent.LoadMedia(fileName))
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertThat(effect).isInstanceOf(PlayerEffect.ShowError::class.java)
            assertThat((effect as PlayerEffect.ShowError).message).isEqualTo(errorMessage)
        }
    }

    // ==================== Playback State Observation Tests ====================

    @Test
    fun `state updates when isPlaying changes`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.state.value.isPlaying).isFalse()

        isPlayingFlow.value = true
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.state.value.isPlaying).isTrue()
    }

    @Test
    fun `state updates when position changes`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.state.value.currentPosition).isEqualTo(0L)

        positionFlow.value = 1000L
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.state.value.currentPosition).isEqualTo(1000L)
    }

    @Test
    fun `state reflects combined isPlaying and position changes`() = runTest {
        isPlayingFlow.value = true
        positionFlow.value = 5000L
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.state.value.isPlaying).isTrue()
        assertThat(viewModel.state.value.currentPosition).isEqualTo(5000L)
    }

    // ==================== Duration Tests ====================

    @Test
    fun `setDuration updates state duration`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setDuration(120_000L)

        assertThat(viewModel.state.value.duration).isEqualTo(120_000L)
    }

    @Test
    fun `setDuration preserves other state values`() = runTest {
        isPlayingFlow.value = true
        positionFlow.value = 5000L
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setDuration(120_000L)

        assertThat(viewModel.state.value.isPlaying).isTrue()
        assertThat(viewModel.state.value.currentPosition).isEqualTo(5000L)
        assertThat(viewModel.state.value.duration).isEqualTo(120_000L)
    }

    // ==================== Edge Cases ====================

    @Test
    fun `multiple rapid intents are processed correctly`() = runTest {
        viewModel.handleIntent(PlayerIntent.SeekToPosition(1000L))
        viewModel.handleIntent(PlayerIntent.SeekToPosition(2000L))
        viewModel.handleIntent(PlayerIntent.SeekToPosition(3000L))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { playerController.seekTo(1000L) }
        coVerify(exactly = 1) { playerController.seekTo(2000L) }
        coVerify(exactly = 1) { playerController.seekTo(3000L) }
    }

    @Test
    fun `playback state changes during seek are handled`() = runTest {
        isPlayingFlow.value = true
        positionFlow.value = 1000L
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effects.test {
            viewModel.handleIntent(PlayerIntent.SeekToPosition(5000L))
            testDispatcher.scheduler.advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(PlayerEffect.SeekCompleted(5000L))
        }

        // Verify state still reflects playing
        assertThat(viewModel.state.value.isPlaying).isTrue()
    }
}
