package com.kyrics.demo.domain.usecase

import com.kyrics.demo.domain.model.DemoSettings
import com.kyrics.demo.domain.repository.DemoSettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateDemoSettingsUseCaseTest {

    private lateinit var repository: DemoSettingsRepository
    private lateinit var useCase: UpdateDemoSettingsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = UpdateDemoSettingsUseCase(repository)
    }

    @Test
    fun `invoke calls repository updateSettings`() = runTest {
        val settings = DemoSettings(fontSize = 50f)
        coEvery { repository.updateSettings(settings) } returns Unit

        useCase(settings)

        coVerify { repository.updateSettings(settings) }
    }

    @Test
    fun `invoke passes correct settings to repository`() = runTest {
        val settings = DemoSettings(
            fontSize = 30f,
            blurEnabled = true,
            gradientEnabled = true
        )
        coEvery { repository.updateSettings(any()) } returns Unit

        useCase(settings)

        coVerify {
            repository.updateSettings(
                match {
                    it.fontSize == 30f &&
                        it.blurEnabled &&
                        it.gradientEnabled
                }
            )
        }
    }
}
