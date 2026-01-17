package com.karaokelyrics.ui.screenshot

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.karaokelyrics.ui.components.KaraokeSingleLine
import com.karaokelyrics.ui.core.config.LibraryPresets
import com.karaokelyrics.ui.core.models.KaraokeLine
import com.karaokelyrics.ui.core.models.KaraokeSyllable
import com.karaokelyrics.ui.state.LineUiState
import org.junit.Rule
import org.junit.Test

/**
 * Paparazzi screenshot tests for all LibraryPresets.
 * Captures visual appearance of each preset theme.
 */
class LibraryPresetsScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6,
        showSystemUi = false
    )

    private val testLine = KaraokeLine(
        syllables = listOf(
            KaraokeSyllable("Every ", 0, 400),
            KaraokeSyllable("moment ", 400, 800),
            KaraokeSyllable("feels ", 800, 1200),
            KaraokeSyllable("so ", 1200, 1400),
            KaraokeSyllable("right", 1400, 2000)
        ),
        start = 0,
        end = 2000
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

    private val upcomingState = LineUiState(
        isPlaying = false,
        hasPlayed = false,
        isUpcoming = true,
        opacity = 0.6f,
        scale = 1f
    )

    // ==================== All Presets Comparison ====================

    @Test
    fun allPresets_playing_state() {
        paparazzi.snapshot {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LibraryPresets.allPresets.forEach { (name, config) ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(config.visual.backgroundColor)
                            .padding(8.dp)
                    ) {
                        KaraokeSingleLine(
                            line = testLine,
                            lineUiState = playingState,
                            currentTimeMs = 1000, // Midway through
                            config = config,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    // ==================== Individual Preset Full Tests ====================

    @Test
    fun preset_classic_allStates() {
        val config = LibraryPresets.Classic
        paparazzi.snapshot {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Upcoming
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = upcomingState,
                    currentTimeMs = -100,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                // Playing
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playingState,
                    currentTimeMs = 1000,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                // Played
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playedState,
                    currentTimeMs = 2500,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Test
    fun preset_neon_allStates() {
        val config = LibraryPresets.Neon
        paparazzi.snapshot {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = upcomingState,
                    currentTimeMs = -100,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playingState,
                    currentTimeMs = 1000,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playedState,
                    currentTimeMs = 2500,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Test
    fun preset_rainbow_allStates() {
        val config = LibraryPresets.Rainbow
        paparazzi.snapshot {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = upcomingState,
                    currentTimeMs = -100,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playingState,
                    currentTimeMs = 1000,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playedState,
                    currentTimeMs = 2500,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Test
    fun preset_fire_allStates() {
        val config = LibraryPresets.Fire
        paparazzi.snapshot {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = upcomingState,
                    currentTimeMs = -100,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playingState,
                    currentTimeMs = 1000,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playedState,
                    currentTimeMs = 2500,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Test
    fun preset_ocean_allStates() {
        val config = LibraryPresets.Ocean
        paparazzi.snapshot {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = upcomingState,
                    currentTimeMs = -100,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playingState,
                    currentTimeMs = 1000,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playedState,
                    currentTimeMs = 2500,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Test
    fun preset_retro_allStates() {
        val config = LibraryPresets.Retro
        paparazzi.snapshot {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = upcomingState,
                    currentTimeMs = -100,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playingState,
                    currentTimeMs = 1000,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playedState,
                    currentTimeMs = 2500,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Test
    fun preset_minimal_allStates() {
        val config = LibraryPresets.Minimal
        paparazzi.snapshot {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = upcomingState.copy(opacity = 0.4f),
                    currentTimeMs = -100,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playingState.copy(scale = 1f),
                    currentTimeMs = 1000,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playedState,
                    currentTimeMs = 2500,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Test
    fun preset_elegant_allStates() {
        val config = LibraryPresets.Elegant
        paparazzi.snapshot {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = upcomingState,
                    currentTimeMs = -100,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playingState,
                    currentTimeMs = 1000,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playedState,
                    currentTimeMs = 2500,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Test
    fun preset_party_allStates() {
        val config = LibraryPresets.Party
        paparazzi.snapshot {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = upcomingState,
                    currentTimeMs = -100,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playingState,
                    currentTimeMs = 1000,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playedState,
                    currentTimeMs = 2500,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Test
    fun preset_matrix_allStates() {
        val config = LibraryPresets.Matrix
        paparazzi.snapshot {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = upcomingState,
                    currentTimeMs = -100,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playingState,
                    currentTimeMs = 1000,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = playedState,
                    currentTimeMs = 2500,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
