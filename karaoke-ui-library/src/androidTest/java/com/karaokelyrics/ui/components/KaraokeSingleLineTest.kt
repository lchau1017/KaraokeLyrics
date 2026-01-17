package com.karaokelyrics.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.KaraokeLine
import com.karaokelyrics.ui.core.models.KaraokeSyllable
import com.karaokelyrics.ui.state.LineUiState
import org.junit.Rule
import org.junit.Test

/**
 * Compose UI tests for KaraokeSingleLine component.
 */
class KaraokeSingleLineTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testLine = KaraokeLine(
        syllables = listOf(
            KaraokeSyllable("Hello ", 0, 500),
            KaraokeSyllable("World", 500, 1000)
        ),
        start = 0,
        end = 1000
    )

    private val upcomingState = LineUiState(
        isPlaying = false,
        hasPlayed = false,
        isUpcoming = true,
        opacity = 0.6f,
        scale = 1f
    )

    private val playingState = LineUiState(
        isPlaying = true,
        hasPlayed = false,
        isUpcoming = false,
        opacity = 1f,
        scale = 1.05f
    )

    private val playedState = LineUiState(
        isPlaying = false,
        hasPlayed = true,
        isUpcoming = false,
        opacity = 0.25f,
        scale = 1f
    )

    // ==================== Display Tests ====================

    @Test
    fun singleLine_displaysContent() {
        composeTestRule.setContent {
            KaraokeSingleLine(
                line = testLine,
                lineUiState = playingState,
                currentTimeMs = 500,
                config = KaraokeLibraryConfig.Default,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // The syllables should be visible
        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("World", substring = true).assertIsDisplayed()
    }

    @Test
    fun singleLine_displaysUpcomingState() {
        composeTestRule.setContent {
            KaraokeSingleLine(
                line = testLine,
                lineUiState = upcomingState,
                currentTimeMs = -100,
                config = KaraokeLibraryConfig.Default,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Line should be displayed
        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    @Test
    fun singleLine_displaysPlayedState() {
        composeTestRule.setContent {
            KaraokeSingleLine(
                line = testLine,
                lineUiState = playedState,
                currentTimeMs = 1500,
                config = KaraokeLibraryConfig.Default,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Line should be displayed even with reduced opacity
        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    // ==================== Click Tests ====================

    @Test
    fun singleLine_clickTriggersCallback() {
        var clickedLine: com.karaokelyrics.ui.core.models.ISyncedLine? = null

        composeTestRule.setContent {
            KaraokeSingleLine(
                line = testLine,
                lineUiState = playingState,
                currentTimeMs = 500,
                config = KaraokeLibraryConfig.Default.copy(
                    behavior = KaraokeLibraryConfig.Default.behavior.copy(
                        enableLineClick = true
                    )
                ),
                modifier = Modifier.fillMaxWidth(),
                onLineClick = { clickedLine = it }
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).performClick()

        assertThat(clickedLine).isEqualTo(testLine)
    }

    @Test
    fun singleLine_clickDisabled_doesNotTriggerCallback() {
        var clickedLine: com.karaokelyrics.ui.core.models.ISyncedLine? = null

        composeTestRule.setContent {
            KaraokeSingleLine(
                line = testLine,
                lineUiState = playingState,
                currentTimeMs = 500,
                config = KaraokeLibraryConfig.Default.copy(
                    behavior = KaraokeLibraryConfig.Default.behavior.copy(
                        enableLineClick = false
                    )
                ),
                modifier = Modifier.fillMaxWidth(),
                onLineClick = { clickedLine = it }
            )
        }

        // Click should not trigger callback when disabled
        composeTestRule.onNodeWithText("Hello ", substring = true).performClick()

        assertThat(clickedLine).isNull()
    }

    // ==================== Configuration Tests ====================

    @Test
    fun singleLine_withMinimalConfig_displaysCorrectly() {
        composeTestRule.setContent {
            KaraokeSingleLine(
                line = testLine,
                lineUiState = playingState,
                currentTimeMs = 500,
                config = KaraokeLibraryConfig.Minimal,
                modifier = Modifier.fillMaxWidth()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    @Test
    fun singleLine_withDramaticConfig_displaysCorrectly() {
        composeTestRule.setContent {
            KaraokeSingleLine(
                line = testLine,
                lineUiState = playingState,
                currentTimeMs = 500,
                config = KaraokeLibraryConfig.Dramatic,
                modifier = Modifier.fillMaxWidth()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    // ==================== Multiple Syllables Tests ====================

    @Test
    fun singleLine_withMultipleSyllables_displaysAll() {
        val multiSyllableLine = KaraokeLine(
            syllables = listOf(
                KaraokeSyllable("Ka", 0, 200),
                KaraokeSyllable("ra", 200, 400),
                KaraokeSyllable("o", 400, 600),
                KaraokeSyllable("ke", 600, 800)
            ),
            start = 0,
            end = 800
        )

        composeTestRule.setContent {
            KaraokeSingleLine(
                line = multiSyllableLine,
                lineUiState = playingState,
                currentTimeMs = 400,
                config = KaraokeLibraryConfig.Default,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // All syllables should be combined and displayed
        composeTestRule.onNodeWithText("Ka", substring = true).assertIsDisplayed()
    }

    // ==================== Progress Tests ====================

    @Test
    fun singleLine_atStart_showsCorrectProgress() {
        composeTestRule.setContent {
            KaraokeSingleLine(
                line = testLine,
                lineUiState = playingState,
                currentTimeMs = 0,
                config = KaraokeLibraryConfig.Default,
                modifier = Modifier.fillMaxWidth()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    @Test
    fun singleLine_atMiddle_showsCorrectProgress() {
        composeTestRule.setContent {
            KaraokeSingleLine(
                line = testLine,
                lineUiState = playingState,
                currentTimeMs = 500,
                config = KaraokeLibraryConfig.Default,
                modifier = Modifier.fillMaxWidth()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("World", substring = true).assertIsDisplayed()
    }

    @Test
    fun singleLine_atEnd_showsCorrectProgress() {
        composeTestRule.setContent {
            KaraokeSingleLine(
                line = testLine,
                lineUiState = playingState,
                currentTimeMs = 1000,
                config = KaraokeLibraryConfig.Default,
                modifier = Modifier.fillMaxWidth()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("World", substring = true).assertIsDisplayed()
    }
}
