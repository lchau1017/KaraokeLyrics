package com.karaokelyrics.ui.screenshot

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.karaokelyrics.ui.components.KaraokeSingleLine
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.config.LibraryPresets
import com.karaokelyrics.ui.core.models.KaraokeLine
import com.karaokelyrics.ui.core.models.KaraokeSyllable
import com.karaokelyrics.ui.state.LineUiState
import org.junit.Rule
import org.junit.Test

/**
 * Paparazzi screenshot tests for KaraokeSingleLine component.
 * Captures visual regression tests for different states and configurations.
 */
class KaraokeSingleLineScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6,
        showSystemUi = false
    )

    private val testLine = KaraokeLine(
        syllables = listOf(
            KaraokeSyllable("Hel", 0, 300),
            KaraokeSyllable("lo ", 300, 600),
            KaraokeSyllable("World", 600, 1000)
        ),
        start = 0,
        end = 1000
    )

    // ==================== Default State Tests ====================

    @Test
    fun karaokeSingleLine_default_upcoming() {
        paparazzi.snapshot {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = LineUiState(
                        isPlaying = false,
                        hasPlayed = false,
                        isUpcoming = true,
                        opacity = 0.6f,
                        scale = 1f,
                        blurRadius = 0f
                    ),
                    currentTimeMs = -100,
                    config = KaraokeLibraryConfig.Default,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Test
    fun karaokeSingleLine_default_playing() {
        paparazzi.snapshot {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = LineUiState(
                        isPlaying = true,
                        hasPlayed = false,
                        isUpcoming = false,
                        opacity = 1f,
                        scale = 1.05f,
                        blurRadius = 0f
                    ),
                    currentTimeMs = 500,
                    config = KaraokeLibraryConfig.Default,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Test
    fun karaokeSingleLine_default_played() {
        paparazzi.snapshot {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = LineUiState(
                        isPlaying = false,
                        hasPlayed = true,
                        isUpcoming = false,
                        opacity = 0.25f,
                        scale = 1f,
                        blurRadius = 0f
                    ),
                    currentTimeMs = 1500,
                    config = KaraokeLibraryConfig.Default,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // ==================== Preset Theme Tests ====================

    @Test
    fun karaokeSingleLine_preset_classic() {
        paparazzi.snapshot {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = LineUiState(
                        isPlaying = true,
                        hasPlayed = false,
                        isUpcoming = false,
                        opacity = 1f,
                        scale = 1.05f
                    ),
                    currentTimeMs = 500,
                    config = LibraryPresets.Classic,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Test
    fun karaokeSingleLine_preset_neon() {
        paparazzi.snapshot {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = LineUiState(
                        isPlaying = true,
                        hasPlayed = false,
                        isUpcoming = false,
                        opacity = 1f,
                        scale = 1.05f
                    ),
                    currentTimeMs = 500,
                    config = LibraryPresets.Neon,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Test
    fun karaokeSingleLine_preset_rainbow() {
        paparazzi.snapshot {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = LineUiState(
                        isPlaying = true,
                        hasPlayed = false,
                        isUpcoming = false,
                        opacity = 1f,
                        scale = 1.05f
                    ),
                    currentTimeMs = 500,
                    config = LibraryPresets.Rainbow,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Test
    fun karaokeSingleLine_preset_fire() {
        paparazzi.snapshot {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = LineUiState(
                        isPlaying = true,
                        hasPlayed = false,
                        isUpcoming = false,
                        opacity = 1f,
                        scale = 1.05f
                    ),
                    currentTimeMs = 500,
                    config = LibraryPresets.Fire,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Test
    fun karaokeSingleLine_preset_minimal() {
        paparazzi.snapshot {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = LineUiState(
                        isPlaying = true,
                        hasPlayed = false,
                        isUpcoming = false,
                        opacity = 1f,
                        scale = 1f
                    ),
                    currentTimeMs = 500,
                    config = LibraryPresets.Minimal,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Test
    fun karaokeSingleLine_preset_matrix() {
        paparazzi.snapshot {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = LineUiState(
                        isPlaying = true,
                        hasPlayed = false,
                        isUpcoming = false,
                        opacity = 1f,
                        scale = 1.05f
                    ),
                    currentTimeMs = 500,
                    config = LibraryPresets.Matrix,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // ==================== Progress Tests ====================

    @Test
    fun karaokeSingleLine_progress_0_percent() {
        paparazzi.snapshot {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = LineUiState(
                        isPlaying = true,
                        hasPlayed = false,
                        isUpcoming = false,
                        opacity = 1f,
                        scale = 1.05f
                    ),
                    currentTimeMs = 0,
                    config = KaraokeLibraryConfig.Default,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Test
    fun karaokeSingleLine_progress_50_percent() {
        paparazzi.snapshot {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = LineUiState(
                        isPlaying = true,
                        hasPlayed = false,
                        isUpcoming = false,
                        opacity = 1f,
                        scale = 1.05f
                    ),
                    currentTimeMs = 500,
                    config = KaraokeLibraryConfig.Default,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Test
    fun karaokeSingleLine_progress_100_percent() {
        paparazzi.snapshot {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp)
            ) {
                KaraokeSingleLine(
                    line = testLine,
                    lineUiState = LineUiState(
                        isPlaying = true,
                        hasPlayed = false,
                        isUpcoming = false,
                        opacity = 1f,
                        scale = 1.05f
                    ),
                    currentTimeMs = 1000,
                    config = KaraokeLibraryConfig.Default,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
