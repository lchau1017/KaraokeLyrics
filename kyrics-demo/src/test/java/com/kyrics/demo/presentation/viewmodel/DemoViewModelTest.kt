package com.kyrics.demo.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kyrics.demo.data.datasource.DemoLyricsDataSource
import com.kyrics.demo.domain.model.DemoSettings
import com.kyrics.demo.domain.usecase.GetDemoSettingsUseCase
import com.kyrics.demo.domain.usecase.UpdateDemoSettingsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DemoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getDemoSettingsUseCase: GetDemoSettingsUseCase
    private lateinit var updateDemoSettingsUseCase: UpdateDemoSettingsUseCase
    private lateinit var demoLyricsDataSource: DemoLyricsDataSource
    private lateinit var viewModel: DemoViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getDemoSettingsUseCase = mockk()
        updateDemoSettingsUseCase = mockk()
        demoLyricsDataSource = mockk()

        every { getDemoSettingsUseCase() } returns flowOf(DemoSettings.Default)
        every { demoLyricsDataSource.getDemoLyrics() } returns emptyList()
        coEvery { updateDemoSettingsUseCase(any()) } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): DemoViewModel = DemoViewModel(
        getDemoSettingsUseCase,
        updateDemoSettingsUseCase,
        demoLyricsDataSource
    )

    // ==================== Initial State Tests ====================

    @Test
    fun `initial state has default values`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.isPlaying).isFalse()
        assertThat(state.currentTimeMs).isEqualTo(0L)
        assertThat(state.selectedLineIndex).isEqualTo(0)
        assertThat(state.showColorPicker).isNull()
    }

    @Test
    fun `initial state loads settings from use case`() = runTest {
        val customSettings = DemoSettings(fontSize = 40f)
        every { getDemoSettingsUseCase() } returns flowOf(customSettings)

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.state.value.settings.fontSize).isEqualTo(40f)
    }

    @Test
    fun `initial state loads demo lyrics from data source`() = runTest {
        val demoLines = DemoLyricsDataSource().getDemoLyrics()
        every { demoLyricsDataSource.getDemoLyrics() } returns demoLines

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.state.value.demoLines).isEqualTo(demoLines)
    }

    // ==================== Playback Tests ====================

    @Test
    fun `TogglePlayPause starts playback`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.TogglePlayPause)
        assertThat(viewModel.state.value.isPlaying).isTrue()

        // Stop playback to cancel the timer coroutine
        viewModel.onIntent(DemoIntent.TogglePlayPause)
    }

    @Test
    fun `TogglePlayPause pauses when playing`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.TogglePlayPause) // Start
        assertThat(viewModel.state.value.isPlaying).isTrue()

        viewModel.onIntent(DemoIntent.TogglePlayPause) // Pause
        assertThat(viewModel.state.value.isPlaying).isFalse()
    }

    @Test
    fun `Reset stops playback and resets time`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.TogglePlayPause)
        viewModel.onIntent(DemoIntent.UpdateTime(5000))
        viewModel.onIntent(DemoIntent.Reset)

        assertThat(viewModel.state.value.isPlaying).isFalse()
        assertThat(viewModel.state.value.currentTimeMs).isEqualTo(0L)
        assertThat(viewModel.state.value.selectedLineIndex).isEqualTo(0)
    }

    @Test
    fun `Seek updates current time`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.Seek(10_000))

        assertThat(viewModel.state.value.currentTimeMs).isEqualTo(10_000)
    }

    @Test
    fun `UpdateTime wraps around at end of song`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.UpdateTime(DemoLyricsDataSource.TOTAL_DURATION_MS + 100))

        assertThat(viewModel.state.value.currentTimeMs).isEqualTo(0L)
    }

    // ==================== Color Picker Tests ====================

    @Test
    fun `ShowColorPicker sets target`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.ShowColorPicker(ColorPickerTarget.SUNG_COLOR))

        assertThat(viewModel.state.value.showColorPicker).isEqualTo(ColorPickerTarget.SUNG_COLOR)
    }

    @Test
    fun `DismissColorPicker clears target`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.ShowColorPicker(ColorPickerTarget.SUNG_COLOR))
        viewModel.onIntent(DemoIntent.DismissColorPicker)

        assertThat(viewModel.state.value.showColorPicker).isNull()
    }

    @Test
    fun `UpdateColor updates sung color`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.UpdateColor(ColorPickerTarget.SUNG_COLOR, Color.Red))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(match { it.sungColor == Color.Red }) }
    }

    @Test
    fun `UpdateColor updates unsung color`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.UpdateColor(ColorPickerTarget.UNSUNG_COLOR, Color.Blue))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(match { it.unsungColor == Color.Blue }) }
    }

    @Test
    fun `UpdateColor updates active color`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.UpdateColor(ColorPickerTarget.ACTIVE_COLOR, Color.Yellow))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(match { it.activeColor == Color.Yellow }) }
    }

    @Test
    fun `UpdateColor updates background color`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.UpdateColor(ColorPickerTarget.BACKGROUND_COLOR, Color.Gray))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(match { it.backgroundColor == Color.Gray }) }
    }

    // ==================== Font Settings Tests ====================

    @Test
    fun `UpdateFontSize calls update use case`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.UpdateFontSize(50f))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(match { it.fontSize == 50f }) }
    }

    @Test
    fun `UpdateFontWeight calls update use case`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.UpdateFontWeight(FontWeight.Light))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(match { it.fontWeight == FontWeight.Light }) }
    }

    @Test
    fun `UpdateFontFamily calls update use case`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.UpdateFontFamily(FontFamily.Monospace))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(match { it.fontFamily == FontFamily.Monospace }) }
    }

    @Test
    fun `UpdateTextAlign calls update use case`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.UpdateTextAlign(TextAlign.Start))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(match { it.textAlign == TextAlign.Start }) }
    }

    // ==================== Viewer Type Tests ====================

    @Test
    fun `SelectViewerType calls update use case`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.SelectViewerType(5))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(match { it.viewerTypeIndex == 5 }) }
    }

    // ==================== Visual Effects Tests ====================

    @Test
    fun `ToggleGradient calls update use case`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.ToggleGradient(true))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(match { it.gradientEnabled }) }
    }

    @Test
    fun `UpdateGradientAngle calls update use case`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.UpdateGradientAngle(180f))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(match { it.gradientAngle == 180f }) }
    }

    @Test
    fun `ToggleBlur calls update use case`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.ToggleBlur(true))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(match { it.blurEnabled }) }
    }

    @Test
    fun `UpdateBlurIntensity calls update use case`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.UpdateBlurIntensity(2.5f))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(match { it.blurIntensity == 2.5f }) }
    }

    // ==================== Animation Tests ====================

    @Test
    fun `ToggleCharAnimation calls update use case`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.ToggleCharAnimation(true))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(match { it.charAnimEnabled }) }
    }

    @Test
    fun `UpdateCharMaxScale calls update use case`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.UpdateCharMaxScale(1.5f))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(match { it.charMaxScale == 1.5f }) }
    }

    @Test
    fun `ToggleLineAnimation calls update use case`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.ToggleLineAnimation(true))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(match { it.lineAnimEnabled }) }
    }

    @Test
    fun `TogglePulse calls update use case`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.TogglePulse(true))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(match { it.pulseEnabled }) }
    }

    // ==================== Preset Tests ====================

    @Test
    fun `LoadPreset Classic updates settings`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.LoadPreset("Classic"))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(any()) }
    }

    @Test
    fun `LoadPreset Neon updates settings`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.LoadPreset("Neon"))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(any()) }
    }

    @Test
    fun `LoadPreset sends PresetLoaded effect`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onIntent(DemoIntent.LoadPreset("Classic"))
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertThat(effect).isEqualTo(DemoEffect.PresetLoaded)
        }
    }

    @Test
    fun `LoadPreset invalid name does not update`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.LoadPreset("InvalidPreset"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Should only have been called once during initial setup
        coVerify(exactly = 0) { updateDemoSettingsUseCase(any()) }
    }

    // ==================== Line Selection Tests ====================

    @Test
    fun `SelectLine updates selected line index`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.SelectLine(3))

        assertThat(viewModel.state.value.selectedLineIndex).isEqualTo(3)
    }

    // ==================== Layout Tests ====================

    @Test
    fun `UpdateLineSpacing calls update use case`() = runTest {
        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onIntent(DemoIntent.UpdateLineSpacing(100f))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDemoSettingsUseCase(match { it.lineSpacing == 100f }) }
    }
}
