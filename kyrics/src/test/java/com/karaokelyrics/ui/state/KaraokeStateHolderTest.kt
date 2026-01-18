package com.kyrics.state

import com.google.common.truth.Truth.assertThat
import com.kyrics.config.KyricsConfig
import com.kyrics.testdata.TestData
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for KyricsStateHolder.
 * Tests the state management and update logic.
 */
class KyricsStateHolderTest {

    private lateinit var stateHolder: KyricsStateHolder
    private lateinit var defaultConfig: KyricsConfig

    @Before
    fun setup() {
        defaultConfig = KyricsConfig.Default
        stateHolder = KyricsStateHolder(defaultConfig)
    }

    // ==================== Initial State Tests ====================

    @Test
    fun `initial state is empty and not initialized`() {
        val state = stateHolder.uiState.value

        assertThat(state.lines).isEmpty()
        assertThat(state.currentTimeMs).isEqualTo(0)
        assertThat(state.currentLineIndex).isNull()
        assertThat(state.isInitialized).isFalse()
    }

    @Test
    fun `isInitialized returns false initially`() {
        assertThat(stateHolder.isInitialized).isFalse()
    }

    // ==================== setLines Tests ====================

    @Test
    fun `setLines updates state with lines`() {
        val lines = TestData.createSimpleLines()

        stateHolder.setLines(lines)

        val state = stateHolder.uiState.value
        assertThat(state.lines).hasSize(5)
        assertThat(state.isInitialized).isTrue()
    }

    @Test
    fun `setLines calculates initial line states`() {
        val lines = TestData.createSimpleLines()

        stateHolder.setLines(lines)

        val state = stateHolder.uiState.value
        assertThat(state.lineStates).hasSize(5)
    }

    @Test
    fun `setLines preserves current time`() {
        stateHolder.updateTime(1000)
        stateHolder.setLines(TestData.createSimpleLines())

        val state = stateHolder.uiState.value
        assertThat(state.currentTimeMs).isEqualTo(1000)
    }

    // ==================== updateTime Tests ====================

    @Test
    fun `updateTime updates currentTimeMs`() {
        stateHolder.setLines(TestData.createSimpleLines())

        stateHolder.updateTime(1500)

        assertThat(stateHolder.uiState.value.currentTimeMs).isEqualTo(1500)
    }

    @Test
    fun `updateTime recalculates currentLineIndex`() {
        stateHolder.setLines(TestData.createSimpleLines())

        // Time 1000ms is within line 0 (0-2000ms)
        stateHolder.updateTime(1000)
        assertThat(stateHolder.currentLineIndex).isEqualTo(0)

        // Time 3500ms is within line 1 (2500-4500ms)
        stateHolder.updateTime(3500)
        assertThat(stateHolder.currentLineIndex).isEqualTo(1)
    }

    @Test
    fun `updateTime does nothing for same time value`() {
        stateHolder.setLines(TestData.createSimpleLines())
        stateHolder.updateTime(1000)

        val stateBefore = stateHolder.uiState.value

        // Update with same time
        stateHolder.updateTime(1000)

        val stateAfter = stateHolder.uiState.value

        // Should be the same object (no recalculation)
        assertThat(stateAfter).isSameInstanceAs(stateBefore)
    }

    @Test
    fun `updateTime recalculates line states`() {
        stateHolder.setLines(TestData.createSimpleLines())

        // Initially at time 0, line 0 should be playing
        stateHolder.updateTime(1000)
        assertThat(stateHolder.uiState.value.lineStates[0]?.isPlaying).isTrue()
        assertThat(stateHolder.uiState.value.lineStates[1]?.isUpcoming).isTrue()

        // Move to time 3500, line 0 should be played, line 1 playing
        stateHolder.updateTime(3500)
        assertThat(stateHolder.uiState.value.lineStates[0]?.hasPlayed).isTrue()
        assertThat(stateHolder.uiState.value.lineStates[1]?.isPlaying).isTrue()
    }

    // ==================== update (combined) Tests ====================

    @Test
    fun `update sets both lines and time`() {
        val lines = TestData.createSimpleLines()

        stateHolder.update(lines, 3500)

        val state = stateHolder.uiState.value
        assertThat(state.lines).hasSize(5)
        assertThat(state.currentTimeMs).isEqualTo(3500)
        assertThat(state.currentLineIndex).isEqualTo(1) // Line 1 is at 2500-4500
    }

    @Test
    fun `update replaces existing lines`() {
        stateHolder.setLines(TestData.createSimpleLines())

        val newLines = TestData.createKyricsLines()
        stateHolder.update(newLines, 0)

        assertThat(stateHolder.uiState.value.lines).hasSize(3)
    }

    // ==================== reset Tests ====================

    @Test
    fun `reset clears all state`() {
        stateHolder.setLines(TestData.createSimpleLines())
        stateHolder.updateTime(3500)

        stateHolder.reset()

        val state = stateHolder.uiState.value
        assertThat(state.lines).isEmpty()
        assertThat(state.currentTimeMs).isEqualTo(0)
        assertThat(state.currentLineIndex).isNull()
        assertThat(state.isInitialized).isFalse()
    }

    // ==================== currentLine Tests ====================

    @Test
    fun `currentLine returns null when no line playing`() {
        stateHolder.setLines(TestData.createSimpleLines())
        stateHolder.updateTime(2250) // Between line 0 and 1

        assertThat(stateHolder.currentLine).isNull()
    }

    @Test
    fun `currentLine returns correct line when playing`() {
        val lines = TestData.createSimpleLines()
        stateHolder.setLines(lines)
        stateHolder.updateTime(3500) // Line 1 is playing

        assertThat(stateHolder.currentLine).isEqualTo(lines[1])
    }

    // ==================== Configuration Tests ====================

    @Test
    fun `currentConfig returns provided config`() {
        val customConfig = KyricsConfig.Dramatic

        val holder = KyricsStateHolder(customConfig)

        assertThat(holder.currentConfig).isEqualTo(customConfig)
    }

    @Test
    fun `state calculations use provided config`() {
        val minimalConfig = KyricsConfig.Minimal
        val holder = KyricsStateHolder(minimalConfig)

        holder.setLines(TestData.createSimpleLines())
        holder.updateTime(1000)

        // With minimal config, line animations are disabled, so scale should be 1.0
        val playingLineState = holder.uiState.value.lineStates[0]
        assertThat(playingLineState?.scale).isEqualTo(1f)
    }

    // ==================== Edge Cases ====================

    @Test
    fun `handles empty lines list`() {
        stateHolder.setLines(emptyList())

        val state = stateHolder.uiState.value
        assertThat(state.lines).isEmpty()
        assertThat(state.isInitialized).isTrue()
        assertThat(state.currentLineIndex).isNull()
    }

    @Test
    fun `handles negative time`() {
        stateHolder.setLines(TestData.createSimpleLines())
        stateHolder.updateTime(-500)

        assertThat(stateHolder.currentLineIndex).isNull()
        // All lines should be upcoming
        stateHolder.uiState.value.lineStates.values.forEach { lineState ->
            assertThat(lineState.isUpcoming).isTrue()
        }
    }

    @Test
    fun `handles time after all lines`() {
        stateHolder.setLines(TestData.createSimpleLines())
        stateHolder.updateTime(20000) // After all lines

        assertThat(stateHolder.currentLineIndex).isNull()
        // All lines should be played
        stateHolder.uiState.value.lineStates.values.forEach { lineState ->
            assertThat(lineState.hasPlayed).isTrue()
        }
    }
}
