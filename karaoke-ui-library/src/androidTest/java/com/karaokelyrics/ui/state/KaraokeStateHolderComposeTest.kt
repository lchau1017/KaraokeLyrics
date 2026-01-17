package com.karaokelyrics.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import com.google.common.truth.Truth.assertThat
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.models.KaraokeLine
import com.karaokelyrics.ui.core.models.KaraokeSyllable
import org.junit.Rule
import org.junit.Test

/**
 * Compose UI tests for KaraokeStateHolder.
 * Tests the state holder behavior within a Compose context.
 */
class KaraokeStateHolderComposeTest {

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

    // ==================== rememberKaraokeStateHolder Tests ====================

    @Test
    fun rememberKaraokeStateHolder_createsStateHolder() {
        var stateHolder: KaraokeStateHolder? = null

        composeTestRule.setContent {
            stateHolder = rememberKaraokeStateHolder(KaraokeLibraryConfig.Default)
        }

        composeTestRule.waitForIdle()

        assertThat(stateHolder).isNotNull()
    }

    @Test
    fun rememberKaraokeStateHolder_preservesStateAcrossRecompositions() {
        var stateHolder1: KaraokeStateHolder? = null
        var stateHolder2: KaraokeStateHolder? = null
        var recompositionCount = 0

        composeTestRule.setContent {
            val holder = rememberKaraokeStateHolder(KaraokeLibraryConfig.Default)
            if (recompositionCount == 0) {
                stateHolder1 = holder
            } else {
                stateHolder2 = holder
            }
            recompositionCount++
        }

        composeTestRule.waitForIdle()

        // Trigger recomposition
        composeTestRule.runOnIdle {
            stateHolder1?.setLines(testLines)
        }

        composeTestRule.waitForIdle()

        // State holder should be the same instance
        assertThat(stateHolder1).isSameInstanceAs(stateHolder2)
    }

    // ==================== UI State Updates Tests ====================

    @Test
    fun stateHolder_setLines_updatesUiState() {
        var capturedUiState: KaraokeUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKaraokeStateHolder(KaraokeLibraryConfig.Default)
            stateHolder.setLines(testLines)
            val uiState by stateHolder.uiState
            capturedUiState = uiState
        }

        composeTestRule.waitForIdle()

        assertThat(capturedUiState).isNotNull()
        assertThat(capturedUiState!!.lines).isEqualTo(testLines)
        assertThat(capturedUiState!!.lineStates).hasSize(testLines.size)
    }

    @Test
    fun stateHolder_updateTime_updatesCurrentLine() {
        var capturedUiState: KaraokeUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKaraokeStateHolder(KaraokeLibraryConfig.Default)
            stateHolder.setLines(testLines)
            stateHolder.updateTime(500) // First line is playing
            val uiState by stateHolder.uiState
            capturedUiState = uiState
        }

        composeTestRule.waitForIdle()

        assertThat(capturedUiState).isNotNull()
        assertThat(capturedUiState!!.currentLineIndex).isEqualTo(0)
    }

    @Test
    fun stateHolder_updateTime_secondLine() {
        var capturedUiState: KaraokeUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKaraokeStateHolder(KaraokeLibraryConfig.Default)
            stateHolder.setLines(testLines)
            stateHolder.updateTime(1800) // Second line is playing
            val uiState by stateHolder.uiState
            capturedUiState = uiState
        }

        composeTestRule.waitForIdle()

        assertThat(capturedUiState).isNotNull()
        assertThat(capturedUiState!!.currentLineIndex).isEqualTo(1)
    }

    @Test
    fun stateHolder_updateTime_thirdLine() {
        var capturedUiState: KaraokeUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKaraokeStateHolder(KaraokeLibraryConfig.Default)
            stateHolder.setLines(testLines)
            stateHolder.updateTime(3500) // Third line is playing
            val uiState by stateHolder.uiState
            capturedUiState = uiState
        }

        composeTestRule.waitForIdle()

        assertThat(capturedUiState).isNotNull()
        assertThat(capturedUiState!!.currentLineIndex).isEqualTo(2)
    }

    // ==================== Line State Tests ====================

    @Test
    fun stateHolder_lineStates_playingLineHasCorrectState() {
        var capturedUiState: KaraokeUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKaraokeStateHolder(KaraokeLibraryConfig.Default)
            stateHolder.setLines(testLines)
            stateHolder.updateTime(500) // First line is playing
            val uiState by stateHolder.uiState
            capturedUiState = uiState
        }

        composeTestRule.waitForIdle()

        assertThat(capturedUiState).isNotNull()
        val firstLineState = capturedUiState!!.lineStates.getValue(0)
        assertThat(firstLineState.isPlaying).isTrue()
        assertThat(firstLineState.hasPlayed).isFalse()
        assertThat(firstLineState.isUpcoming).isFalse()
    }

    @Test
    fun stateHolder_lineStates_upcomingLineHasCorrectState() {
        var capturedUiState: KaraokeUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKaraokeStateHolder(KaraokeLibraryConfig.Default)
            stateHolder.setLines(testLines)
            stateHolder.updateTime(500) // First line is playing
            val uiState by stateHolder.uiState
            capturedUiState = uiState
        }

        composeTestRule.waitForIdle()

        assertThat(capturedUiState).isNotNull()
        // Second and third lines should be upcoming
        val secondLineState = capturedUiState!!.lineStates.getValue(1)
        assertThat(secondLineState.isPlaying).isFalse()
        assertThat(secondLineState.hasPlayed).isFalse()
        assertThat(secondLineState.isUpcoming).isTrue()
    }

    @Test
    fun stateHolder_lineStates_playedLineHasCorrectState() {
        var capturedUiState: KaraokeUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKaraokeStateHolder(KaraokeLibraryConfig.Default)
            stateHolder.setLines(testLines)
            stateHolder.updateTime(3500) // Third line is playing
            val uiState by stateHolder.uiState
            capturedUiState = uiState
        }

        composeTestRule.waitForIdle()

        assertThat(capturedUiState).isNotNull()
        // First and second lines should be played
        val firstLineState = capturedUiState!!.lineStates.getValue(0)
        assertThat(firstLineState.isPlaying).isFalse()
        assertThat(firstLineState.hasPlayed).isTrue()
        assertThat(firstLineState.isUpcoming).isFalse()
    }

    // ==================== Empty Lines Tests ====================

    @Test
    fun stateHolder_emptyLines_hasEmptyState() {
        var capturedUiState: KaraokeUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKaraokeStateHolder(KaraokeLibraryConfig.Default)
            stateHolder.setLines(emptyList())
            val uiState by stateHolder.uiState
            capturedUiState = uiState
        }

        composeTestRule.waitForIdle()

        assertThat(capturedUiState).isNotNull()
        assertThat(capturedUiState!!.lines).isEmpty()
        assertThat(capturedUiState!!.lineStates).isEmpty()
        assertThat(capturedUiState!!.currentLineIndex).isEqualTo(-1)
    }

    // ==================== Current Time Tests ====================

    @Test
    fun stateHolder_currentTime_isUpdated() {
        var capturedUiState: KaraokeUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKaraokeStateHolder(KaraokeLibraryConfig.Default)
            stateHolder.setLines(testLines)
            stateHolder.updateTime(1234)
            val uiState by stateHolder.uiState
            capturedUiState = uiState
        }

        composeTestRule.waitForIdle()

        assertThat(capturedUiState).isNotNull()
        assertThat(capturedUiState!!.currentTimeMs).isEqualTo(1234)
    }

    // ==================== Configuration Tests ====================

    @Test
    fun stateHolder_withMinimalConfig_works() {
        var capturedUiState: KaraokeUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKaraokeStateHolder(KaraokeLibraryConfig.Minimal)
            stateHolder.setLines(testLines)
            stateHolder.updateTime(500)
            val uiState by stateHolder.uiState
            capturedUiState = uiState
        }

        composeTestRule.waitForIdle()

        assertThat(capturedUiState).isNotNull()
        assertThat(capturedUiState!!.currentLineIndex).isEqualTo(0)
    }

    @Test
    fun stateHolder_withDramaticConfig_works() {
        var capturedUiState: KaraokeUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKaraokeStateHolder(KaraokeLibraryConfig.Dramatic)
            stateHolder.setLines(testLines)
            stateHolder.updateTime(500)
            val uiState by stateHolder.uiState
            capturedUiState = uiState
        }

        composeTestRule.waitForIdle()

        assertThat(capturedUiState).isNotNull()
        assertThat(capturedUiState!!.currentLineIndex).isEqualTo(0)
    }
}
