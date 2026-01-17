package com.karaokelyrics.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.config.LayoutConfig
import com.karaokelyrics.ui.core.config.ViewerConfig
import com.karaokelyrics.ui.core.config.ViewerType
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.core.models.KaraokeLine
import com.karaokelyrics.ui.core.models.KaraokeSyllable
import org.junit.Rule
import org.junit.Test

/**
 * Compose UI tests for KaraokeLyricsViewer component.
 */
class KaraokeLyricsViewerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testLines = listOf(
        KaraokeLine(
            syllables = listOf(
                KaraokeSyllable("Hello ", 0, 500),
                KaraokeSyllable("World", 500, 1000)
            ),
            start = 0,
            end = 1000
        ),
        KaraokeLine(
            syllables = listOf(
                KaraokeSyllable("Second ", 1500, 2000),
                KaraokeSyllable("Line", 2000, 2500)
            ),
            start = 1500,
            end = 2500
        ),
        KaraokeLine(
            syllables = listOf(
                KaraokeSyllable("Third ", 3000, 3500),
                KaraokeSyllable("Line", 3500, 4000)
            ),
            start = 3000,
            end = 4000
        )
    )

    // ==================== Basic Display Tests ====================

    @Test
    fun viewer_displaysLines() {
        composeTestRule.setContent {
            KaraokeLyricsViewer(
                lines = testLines,
                currentTimeMs = 0,
                config = KaraokeLibraryConfig.Default,
                modifier = Modifier.fillMaxSize()
            )
        }

        // First line should be visible
        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    @Test
    fun viewer_displaysCurrentLine() {
        composeTestRule.setContent {
            KaraokeLyricsViewer(
                lines = testLines,
                currentTimeMs = 500,
                config = KaraokeLibraryConfig.Default,
                modifier = Modifier.fillMaxSize()
            )
        }

        // First line should be visible (it's playing)
        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    @Test
    fun viewer_withEmptyLines_doesNotCrash() {
        composeTestRule.setContent {
            KaraokeLyricsViewer(
                lines = emptyList(),
                currentTimeMs = 0,
                config = KaraokeLibraryConfig.Default,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Should not crash
        composeTestRule.waitForIdle()
    }

    // ==================== Viewer Type Tests ====================

    @Test
    fun viewer_centerFocused_displaysCorrectly() {
        val config = KaraokeLibraryConfig.Default.copy(
            layout = LayoutConfig(
                viewerConfig = ViewerConfig(type = ViewerType.CENTER_FOCUSED)
            )
        )

        composeTestRule.setContent {
            KaraokeLyricsViewer(
                lines = testLines,
                currentTimeMs = 500,
                config = config,
                modifier = Modifier.fillMaxSize()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    @Test
    fun viewer_smoothScroll_displaysCorrectly() {
        val config = KaraokeLibraryConfig.Default.copy(
            layout = LayoutConfig(
                viewerConfig = ViewerConfig(type = ViewerType.SMOOTH_SCROLL)
            )
        )

        composeTestRule.setContent {
            KaraokeLyricsViewer(
                lines = testLines,
                currentTimeMs = 500,
                config = config,
                modifier = Modifier.fillMaxSize()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    @Test
    fun viewer_stacked_displaysCorrectly() {
        val config = KaraokeLibraryConfig.Default.copy(
            layout = LayoutConfig(
                viewerConfig = ViewerConfig(type = ViewerType.STACKED)
            )
        )

        composeTestRule.setContent {
            KaraokeLyricsViewer(
                lines = testLines,
                currentTimeMs = 500,
                config = config,
                modifier = Modifier.fillMaxSize()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    @Test
    fun viewer_horizontalPaged_displaysCorrectly() {
        val config = KaraokeLibraryConfig.Default.copy(
            layout = LayoutConfig(
                viewerConfig = ViewerConfig(type = ViewerType.HORIZONTAL_PAGED)
            )
        )

        composeTestRule.setContent {
            KaraokeLyricsViewer(
                lines = testLines,
                currentTimeMs = 500,
                config = config,
                modifier = Modifier.fillMaxSize()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    @Test
    fun viewer_waveFlow_displaysCorrectly() {
        val config = KaraokeLibraryConfig.Default.copy(
            layout = LayoutConfig(
                viewerConfig = ViewerConfig(type = ViewerType.WAVE_FLOW)
            )
        )

        composeTestRule.setContent {
            KaraokeLyricsViewer(
                lines = testLines,
                currentTimeMs = 500,
                config = config,
                modifier = Modifier.fillMaxSize()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    @Test
    fun viewer_fadeThrough_displaysCorrectly() {
        val config = KaraokeLibraryConfig.Default.copy(
            layout = LayoutConfig(
                viewerConfig = ViewerConfig(type = ViewerType.FADE_THROUGH)
            )
        )

        composeTestRule.setContent {
            KaraokeLyricsViewer(
                lines = testLines,
                currentTimeMs = 500,
                config = config,
                modifier = Modifier.fillMaxSize()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    // ==================== Click Callback Tests ====================

    @Test
    fun viewer_clickTriggersCallback() {
        var clickedLine: ISyncedLine? = null
        var clickedIndex: Int? = null

        val config = KaraokeLibraryConfig.Default.copy(
            behavior = KaraokeLibraryConfig.Default.behavior.copy(
                enableLineClick = true
            )
        )

        composeTestRule.setContent {
            KaraokeLyricsViewer(
                lines = testLines,
                currentTimeMs = 500,
                config = config,
                modifier = Modifier.fillMaxSize(),
                onLineClick = { line, index ->
                    clickedLine = line
                    clickedIndex = index
                }
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).performClick()

        assertThat(clickedLine).isNotNull()
        assertThat(clickedIndex).isEqualTo(0)
    }

    // ==================== Time Update Tests ====================

    @Test
    fun viewer_updatesWithTimeChange() {
        composeTestRule.setContent {
            KaraokeLyricsViewer(
                lines = testLines,
                currentTimeMs = 0,
                config = KaraokeLibraryConfig.Default,
                modifier = Modifier.fillMaxSize()
            )
        }

        // First line should be visible
        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()

        // Change time and verify
        composeTestRule.setContent {
            KaraokeLyricsViewer(
                lines = testLines,
                currentTimeMs = 1800,
                config = KaraokeLibraryConfig.Default,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Second line should now be visible
        composeTestRule.onNodeWithText("Second ", substring = true).assertIsDisplayed()
    }

    // ==================== Configuration Tests ====================

    @Test
    fun viewer_withMinimalConfig_displaysCorrectly() {
        composeTestRule.setContent {
            KaraokeLyricsViewer(
                lines = testLines,
                currentTimeMs = 500,
                config = KaraokeLibraryConfig.Minimal,
                modifier = Modifier.fillMaxSize()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    @Test
    fun viewer_withDramaticConfig_displaysCorrectly() {
        composeTestRule.setContent {
            KaraokeLyricsViewer(
                lines = testLines,
                currentTimeMs = 500,
                config = KaraokeLibraryConfig.Dramatic,
                modifier = Modifier.fillMaxSize()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    // ==================== Scrolling Tests ====================

    @Test
    fun viewer_scrollsToCurrentLine() {
        composeTestRule.setContent {
            KaraokeLyricsViewer(
                lines = testLines,
                currentTimeMs = 3200, // Third line is playing
                config = KaraokeLibraryConfig.Default,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Third line should be visible
        composeTestRule.onNodeWithText("Third ", substring = true).assertIsDisplayed()
    }

    // ==================== Single Line Tests ====================

    @Test
    fun viewer_withSingleLine_displaysCorrectly() {
        val singleLine = listOf(testLines.first())

        composeTestRule.setContent {
            KaraokeLyricsViewer(
                lines = singleLine,
                currentTimeMs = 500,
                config = KaraokeLibraryConfig.Default,
                modifier = Modifier.fillMaxSize()
            )
        }

        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    // ==================== Before/After All Lines Tests ====================

    @Test
    fun viewer_beforeAllLines_displaysFirstLine() {
        composeTestRule.setContent {
            KaraokeLyricsViewer(
                lines = testLines,
                currentTimeMs = -500, // Before any line starts
                config = KaraokeLibraryConfig.Default,
                modifier = Modifier.fillMaxSize()
            )
        }

        // First line should still be visible as upcoming
        composeTestRule.onNodeWithText("Hello ", substring = true).assertIsDisplayed()
    }

    @Test
    fun viewer_afterAllLines_displaysLastLine() {
        composeTestRule.setContent {
            KaraokeLyricsViewer(
                lines = testLines,
                currentTimeMs = 10000, // After all lines
                config = KaraokeLibraryConfig.Default,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Last line should still be visible as played
        composeTestRule.onNodeWithText("Third ", substring = true).assertIsDisplayed()
    }
}
