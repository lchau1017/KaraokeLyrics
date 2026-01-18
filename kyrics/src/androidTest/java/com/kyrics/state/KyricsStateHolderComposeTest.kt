package com.kyrics.state

import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import com.google.common.truth.Truth.assertThat
import com.kyrics.config.KyricsConfig
import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable
import org.junit.Rule
import org.junit.Test

/**
 * Compose UI tests for KyricsStateHolder.
 * Tests the state holder behavior within a Compose context.
 */
class KyricsStateHolderComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testLines = listOf(
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("Hello ", 0, 500),
                KyricsSyllable("World", 500, 1000)
            ),
            start = 0,
            end = 1000
        ),
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("Second ", 1500, 2000),
                KyricsSyllable("Line", 2000, 2500)
            ),
            start = 1500,
            end = 2500
        ),
        KyricsLine(
            syllables = listOf(
                KyricsSyllable("Third ", 3000, 3500),
                KyricsSyllable("Line", 3500, 4000)
            ),
            start = 3000,
            end = 4000
        )
    )

    // ==================== rememberKyricsStateHolder Tests ====================

    @Test
    fun rememberKyricsStateHolder_createsStateHolder() {
        var stateHolder: KyricsStateHolder? = null

        composeTestRule.setContent {
            stateHolder = rememberKyricsStateHolder(KyricsConfig.Default)
        }

        composeTestRule.waitForIdle()

        assertThat(stateHolder).isNotNull()
    }

    @Test
    fun rememberKyricsStateHolder_preservesStateAcrossRecompositions() {
        var stateHolder1: KyricsStateHolder? = null
        var stateHolder2: KyricsStateHolder? = null
        var recompositionCount = 0

        composeTestRule.setContent {
            val holder = rememberKyricsStateHolder(KyricsConfig.Default)
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
        var capturedUiState: KyricsUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKyricsStateHolder(KyricsConfig.Default)
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
        var capturedUiState: KyricsUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKyricsStateHolder(KyricsConfig.Default)
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
        var capturedUiState: KyricsUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKyricsStateHolder(KyricsConfig.Default)
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
        var capturedUiState: KyricsUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKyricsStateHolder(KyricsConfig.Default)
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
        var capturedUiState: KyricsUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKyricsStateHolder(KyricsConfig.Default)
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
        var capturedUiState: KyricsUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKyricsStateHolder(KyricsConfig.Default)
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
        var capturedUiState: KyricsUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKyricsStateHolder(KyricsConfig.Default)
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
        var capturedUiState: KyricsUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKyricsStateHolder(KyricsConfig.Default)
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
        var capturedUiState: KyricsUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKyricsStateHolder(KyricsConfig.Default)
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
        var capturedUiState: KyricsUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKyricsStateHolder(KyricsConfig.Minimal)
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
        var capturedUiState: KyricsUiState? = null

        composeTestRule.setContent {
            val stateHolder = rememberKyricsStateHolder(KyricsConfig.Dramatic)
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
