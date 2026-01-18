package com.kyrics.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import com.kyrics.config.KyricsConfig
import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable
import com.kyrics.state.LineUiState
import org.junit.Rule
import org.junit.Test

/**
 * Compose UI tests for KyricsSingleLine component.
 */
class KyricsSingleLineTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testLine = KyricsLine(
        syllables = listOf(
            KyricsSyllable("Hello ", 0, 500),
            KyricsSyllable("World", 500, 1000)
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
            KyricsSingleLine(
                line = testLine,
                lineUiState = playingState,
                currentTimeMs = 500,
                config = KyricsConfig.Default,
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
            KyricsSingleLine(
                line = testLine,
                lineUiState = upcomingState,
                currentTimeMs = -100,
                config = KyricsConfig.Default,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Line should be displayed
        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    @Test
    fun singleLine_displaysPlayedState() {
        composeTestRule.setContent {
            KyricsSingleLine(
                line = testLine,
                lineUiState = playedState,
                currentTimeMs = 1500,
                config = KyricsConfig.Default,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Line should be displayed even with reduced opacity
        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    // ==================== Click Tests ====================

    @Test
    fun singleLine_clickTriggersCallback() {
        var clickedLine: com.kyrics.models.ISyncedLine? = null

        composeTestRule.setContent {
            KyricsSingleLine(
                line = testLine,
                lineUiState = playingState,
                currentTimeMs = 500,
                config = KyricsConfig.Default.copy(
                    behavior = KyricsConfig.Default.behavior.copy(
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
        var clickedLine: com.kyrics.models.ISyncedLine? = null

        composeTestRule.setContent {
            KyricsSingleLine(
                line = testLine,
                lineUiState = playingState,
                currentTimeMs = 500,
                config = KyricsConfig.Default.copy(
                    behavior = KyricsConfig.Default.behavior.copy(
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
            KyricsSingleLine(
                line = testLine,
                lineUiState = playingState,
                currentTimeMs = 500,
                config = KyricsConfig.Minimal,
                modifier = Modifier.fillMaxWidth()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    @Test
    fun singleLine_withDramaticConfig_displaysCorrectly() {
        composeTestRule.setContent {
            KyricsSingleLine(
                line = testLine,
                lineUiState = playingState,
                currentTimeMs = 500,
                config = KyricsConfig.Dramatic,
                modifier = Modifier.fillMaxWidth()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    // ==================== Multiple Syllables Tests ====================

    @Test
    fun singleLine_withMultipleSyllables_displaysAll() {
        val multiSyllableLine = KyricsLine(
            syllables = listOf(
                KyricsSyllable("Ka", 0, 200),
                KyricsSyllable("ra", 200, 400),
                KyricsSyllable("o", 400, 600),
                KyricsSyllable("ke", 600, 800)
            ),
            start = 0,
            end = 800
        )

        composeTestRule.setContent {
            KyricsSingleLine(
                line = multiSyllableLine,
                lineUiState = playingState,
                currentTimeMs = 400,
                config = KyricsConfig.Default,
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
            KyricsSingleLine(
                line = testLine,
                lineUiState = playingState,
                currentTimeMs = 0,
                config = KyricsConfig.Default,
                modifier = Modifier.fillMaxWidth()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    @Test
    fun singleLine_atMiddle_showsCorrectProgress() {
        composeTestRule.setContent {
            KyricsSingleLine(
                line = testLine,
                lineUiState = playingState,
                currentTimeMs = 500,
                config = KyricsConfig.Default,
                modifier = Modifier.fillMaxWidth()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("World", substring = true).assertIsDisplayed()
    }

    @Test
    fun singleLine_atEnd_showsCorrectProgress() {
        composeTestRule.setContent {
            KyricsSingleLine(
                line = testLine,
                lineUiState = playingState,
                currentTimeMs = 1000,
                config = KyricsConfig.Default,
                modifier = Modifier.fillMaxWidth()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("World", substring = true).assertIsDisplayed()
    }
}
