package com.kyrics.demo.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kyrics.demo.domain.model.DemoSettings
import com.kyrics.demo.domain.repository.DemoSettingsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetDemoSettingsUseCaseTest {

    private lateinit var repository: DemoSettingsRepository
    private lateinit var useCase: GetDemoSettingsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetDemoSettingsUseCase(repository)
    }

    @Test
    fun `invoke returns settings from repository`() = runTest {
        val expectedSettings = DemoSettings(fontSize = 40f)
        every { repository.getSettings() } returns flowOf(expectedSettings)

        useCase().test {
            val settings = awaitItem()
            assertThat(settings).isEqualTo(expectedSettings)
            awaitComplete()
        }

        verify { repository.getSettings() }
    }

    @Test
    fun `invoke returns default settings when repository has default`() = runTest {
        every { repository.getSettings() } returns flowOf(DemoSettings.Default)

        useCase().test {
            val settings = awaitItem()
            assertThat(settings).isEqualTo(DemoSettings.Default)
            awaitComplete()
        }
    }
}
