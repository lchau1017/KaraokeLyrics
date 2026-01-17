package com.karaokelyrics.demo.data.repository

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.karaokelyrics.demo.domain.model.DemoSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DemoSettingsRepositoryImplTest {

    private lateinit var repository: DemoSettingsRepositoryImpl

    @Before
    fun setup() {
        repository = DemoSettingsRepositoryImpl()
    }

    @Test
    fun `getSettings returns default settings initially`() = runTest {
        repository.getSettings().test {
            val settings = awaitItem()
            assertThat(settings).isEqualTo(DemoSettings.Default)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `updateSettings emits new settings`() = runTest {
        val newSettings = DemoSettings(fontSize = 50f)

        repository.getSettings().test {
            // Initial value
            assertThat(awaitItem()).isEqualTo(DemoSettings.Default)

            // Update
            repository.updateSettings(newSettings)
            assertThat(awaitItem()).isEqualTo(newSettings)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `resetSettings resets to default`() = runTest {
        val customSettings = DemoSettings(fontSize = 50f)

        repository.getSettings().test {
            // Initial value
            assertThat(awaitItem()).isEqualTo(DemoSettings.Default)

            // Update to custom
            repository.updateSettings(customSettings)
            assertThat(awaitItem()).isEqualTo(customSettings)

            // Reset
            repository.resetSettings()
            assertThat(awaitItem()).isEqualTo(DemoSettings.Default)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `multiple updates emit correct values`() = runTest {
        val settings1 = DemoSettings(fontSize = 30f)
        val settings2 = DemoSettings(fontSize = 40f)
        val settings3 = DemoSettings(fontSize = 50f)

        repository.getSettings().test {
            assertThat(awaitItem()).isEqualTo(DemoSettings.Default)

            repository.updateSettings(settings1)
            assertThat(awaitItem()).isEqualTo(settings1)

            repository.updateSettings(settings2)
            assertThat(awaitItem()).isEqualTo(settings2)

            repository.updateSettings(settings3)
            assertThat(awaitItem()).isEqualTo(settings3)

            cancelAndConsumeRemainingEvents()
        }
    }
}
