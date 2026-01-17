package com.karaokelyrics.app.presentation.features.settings.viewmodel

import androidx.compose.ui.graphics.Color
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.domain.usecase.ObserveUserSettingsUseCase
import com.karaokelyrics.app.domain.usecase.UpdateUserSettingsUseCase
import com.karaokelyrics.app.presentation.features.settings.effect.SettingsEffect
import com.karaokelyrics.app.presentation.features.settings.intent.SettingsIntent
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
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var observeUserSettingsUseCase: ObserveUserSettingsUseCase
    private lateinit var updateUserSettingsUseCase: UpdateUserSettingsUseCase
    private lateinit var settingsFlow: MutableStateFlow<UserSettings>
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        settingsFlow = MutableStateFlow(UserSettings())
        observeUserSettingsUseCase = mockk {
            every { this@mockk.invoke() } returns settingsFlow
        }
        updateUserSettingsUseCase = mockk(relaxed = true)

        viewModel = SettingsViewModel(
            observeUserSettingsUseCase = observeUserSettingsUseCase,
            updateUserSettingsUseCase = updateUserSettingsUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ==================== Initial State Tests ====================

    @Test
    fun `initial state has default settings`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.state.value.settings).isEqualTo(UserSettings())
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `observes settings changes from use case`() = runTest {
        val newSettings = UserSettings(fontSize = FontSize.LARGE)

        settingsFlow.value = newSettings
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.state.value.settings).isEqualTo(newSettings)
    }

    // ==================== Intent Handling Tests ====================

    @Test
    fun `UpdateLyricsColor intent calls use case and emits effect`() = runTest {
        val testColor = Color.Red

        viewModel.effects.test {
            viewModel.handleIntent(SettingsIntent.UpdateLyricsColor(testColor))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateUserSettingsUseCase.updateLyricsColor(any()) }
            assertThat(awaitItem()).isEqualTo(SettingsEffect.SettingsUpdated)
        }
    }

    @Test
    fun `UpdateBackgroundColor intent calls use case and emits effect`() = runTest {
        val testColor = Color.Blue

        viewModel.effects.test {
            viewModel.handleIntent(SettingsIntent.UpdateBackgroundColor(testColor))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateUserSettingsUseCase.updateBackgroundColor(any()) }
            assertThat(awaitItem()).isEqualTo(SettingsEffect.SettingsUpdated)
        }
    }

    @Test
    fun `UpdateFontSize intent calls use case and emits effect`() = runTest {
        viewModel.effects.test {
            viewModel.handleIntent(SettingsIntent.UpdateFontSize(FontSize.EXTRA_LARGE))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateUserSettingsUseCase.updateFontSize(FontSize.EXTRA_LARGE) }
            assertThat(awaitItem()).isEqualTo(SettingsEffect.SettingsUpdated)
        }
    }

    @Test
    fun `UpdateAnimationsEnabled intent calls use case`() = runTest {
        viewModel.effects.test {
            viewModel.handleIntent(SettingsIntent.UpdateAnimationsEnabled(false))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateUserSettingsUseCase.updateAnimationsEnabled(false) }
            assertThat(awaitItem()).isEqualTo(SettingsEffect.SettingsUpdated)
        }
    }

    @Test
    fun `UpdateBlurEffectEnabled intent calls use case`() = runTest {
        viewModel.effects.test {
            viewModel.handleIntent(SettingsIntent.UpdateBlurEffectEnabled(true))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateUserSettingsUseCase.updateBlurEffectEnabled(true) }
            assertThat(awaitItem()).isEqualTo(SettingsEffect.SettingsUpdated)
        }
    }

    @Test
    fun `UpdateCharacterAnimationsEnabled intent calls use case`() = runTest {
        viewModel.effects.test {
            viewModel.handleIntent(SettingsIntent.UpdateCharacterAnimationsEnabled(false))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateUserSettingsUseCase.updateCharacterAnimationsEnabled(false) }
            assertThat(awaitItem()).isEqualTo(SettingsEffect.SettingsUpdated)
        }
    }

    @Test
    fun `UpdateDarkMode intent calls use case`() = runTest {
        viewModel.effects.test {
            viewModel.handleIntent(SettingsIntent.UpdateDarkMode(false))
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateUserSettingsUseCase.updateDarkMode(false) }
            assertThat(awaitItem()).isEqualTo(SettingsEffect.SettingsUpdated)
        }
    }

    @Test
    fun `ResetToDefaults intent calls use case and emits SettingsReset effect`() = runTest {
        viewModel.effects.test {
            viewModel.handleIntent(SettingsIntent.ResetToDefaults)
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { updateUserSettingsUseCase.resetToDefaults() }
            assertThat(awaitItem()).isEqualTo(SettingsEffect.SettingsReset)
        }
    }

    // ==================== Settings Flow Tests ====================

    @Test
    fun `state updates when settings flow emits new value`() = runTest {
        val initialSettings = UserSettings()
        val updatedSettings = UserSettings(
            fontSize = FontSize.SMALL,
            enableAnimations = false,
            enableBlurEffect = true,
            isDarkMode = false
        )

        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.state.value.settings).isEqualTo(initialSettings)

        settingsFlow.value = updatedSettings
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.state.value.settings).isEqualTo(updatedSettings)
        assertThat(viewModel.state.value.settings.fontSize).isEqualTo(FontSize.SMALL)
        assertThat(viewModel.state.value.settings.enableAnimations).isFalse()
        assertThat(viewModel.state.value.settings.enableBlurEffect).isTrue()
        assertThat(viewModel.state.value.settings.isDarkMode).isFalse()
    }

    @Test
    fun `multiple settings updates are handled correctly`() = runTest {
        viewModel.effects.test {
            viewModel.handleIntent(SettingsIntent.UpdateFontSize(FontSize.LARGE))
            viewModel.handleIntent(SettingsIntent.UpdateAnimationsEnabled(false))
            viewModel.handleIntent(SettingsIntent.UpdateDarkMode(false))
            testDispatcher.scheduler.advanceUntilIdle()

            // Should receive 3 effects
            assertThat(awaitItem()).isEqualTo(SettingsEffect.SettingsUpdated)
            assertThat(awaitItem()).isEqualTo(SettingsEffect.SettingsUpdated)
            assertThat(awaitItem()).isEqualTo(SettingsEffect.SettingsUpdated)
        }

        coVerify(exactly = 1) { updateUserSettingsUseCase.updateFontSize(FontSize.LARGE) }
        coVerify(exactly = 1) { updateUserSettingsUseCase.updateAnimationsEnabled(false) }
        coVerify(exactly = 1) { updateUserSettingsUseCase.updateDarkMode(false) }
    }
}
