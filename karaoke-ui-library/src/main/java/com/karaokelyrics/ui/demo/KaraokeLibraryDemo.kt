package com.karaokelyrics.ui.demo

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karaokelyrics.ui.api.KaraokeLibrary
import com.karaokelyrics.ui.core.config.*
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.core.models.KaraokeLine
import com.karaokelyrics.ui.core.models.KaraokeSyllable
import kotlinx.coroutines.delay

/**
 * Demo and preview for the Karaoke UI Library.
 * Shows different configurations and use cases.
 */
object KaraokeLibraryDemo {

    /**
     * Sample karaoke data for demos.
     */
    fun getSampleKaraokeLines(): List<ISyncedLine> {
        return listOf(
            KaraokeLine(
                syllables = listOf(
                    KaraokeSyllable("Ne", 0, 500),
                    KaraokeSyllable("ver ", 500, 800),
                    KaraokeSyllable("gon", 800, 1200),
                    KaraokeSyllable("na ", 1200, 1400),
                    KaraokeSyllable("give ", 1400, 1700),
                    KaraokeSyllable("you ", 1700, 1900),
                    KaraokeSyllable("up", 1900, 2400)
                ),
                start = 0,
                end = 2400,
                metadata = mapOf("alignment" to "center")
            ),
            KaraokeLine(
                syllables = listOf(
                    KaraokeSyllable("Ne", 2400, 2700),
                    KaraokeSyllable("ver ", 2700, 2900),
                    KaraokeSyllable("gon", 2900, 3200),
                    KaraokeSyllable("na ", 3200, 3400),
                    KaraokeSyllable("let ", 3400, 3600),
                    KaraokeSyllable("you ", 3600, 3900),
                    KaraokeSyllable("down", 3900, 4400)
                ),
                start = 2400,
                end = 4400,
                metadata = mapOf("alignment" to "center")
            ),
            KaraokeLine(
                syllables = listOf(
                    KaraokeSyllable("(Oh ", 4400, 4600),
                    KaraokeSyllable("yeah, ", 4600, 4900),
                    KaraokeSyllable("oh ", 4900, 5100),
                    KaraokeSyllable("yeah!)", 5100, 5600)
                ),
                start = 4400,
                end = 5600,
                metadata = mapOf("type" to "accompaniment", "alignment" to "center")
            ),
            KaraokeLine(
                syllables = listOf(
                    KaraokeSyllable("Ne", 5600, 5900),
                    KaraokeSyllable("ver ", 5900, 6100),
                    KaraokeSyllable("gon", 6100, 6400),
                    KaraokeSyllable("na ", 6400, 6600),
                    KaraokeSyllable("run ", 6600, 6900),
                    KaraokeSyllable("a", 6900, 7100),
                    KaraokeSyllable("round ", 7100, 7400),
                    KaraokeSyllable("and ", 7400, 7600),
                    KaraokeSyllable("de", 7600, 7800),
                    KaraokeSyllable("sert ", 7800, 8100),
                    KaraokeSyllable("you", 8100, 8600)
                ),
                start = 5600,
                end = 8600,
                metadata = mapOf("alignment" to "center")
            )
        )
    }

    /**
     * Sample simple text lines (non-karaoke).
     */
    fun getSampleTextLines(): List<ISyncedLine> {
        return listOf(
            SimpleSyncedLine(
                content = "Welcome to the Karaoke UI Library",
                start = 0,
                end = 2000
            ),
            SimpleSyncedLine(
                content = "This is a simple text line",
                start = 2000,
                end = 4000
            ),
            SimpleSyncedLine(
                content = "No syllable-level sync needed",
                start = 4000,
                end = 6000
            )
        )
    }

    /**
     * Simple implementation of ISyncedLine for demo purposes.
     */
    private data class SimpleSyncedLine(
        private val content: String,
        override val start: Int,
        override val end: Int
    ) : ISyncedLine {
        override fun getContent(): String = content
    }
}

/**
 * Main demo composable showing the library in action.
 */
@Composable
fun KaraokeLibraryDemoScreen() {
    var currentTimeMs by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var selectedConfig by remember { mutableStateOf("Default") }

    // Animate playback time
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(100)
            currentTimeMs += 100
            if (currentTimeMs > 10000) {
                currentTimeMs = 0 // Loop
            }
        }
    }

    val config = when (selectedConfig) {
        "Minimal" -> KaraokeLibraryConfig.Minimal
        "Dramatic" -> KaraokeLibraryConfig.Dramatic
        "Custom" -> KaraokeLibraryConfig(
            visual = VisualConfig(
                fontSize = 40.sp,
                playingTextColor = Color.Yellow,
                playedTextColor = Color.Gray,
                upcomingTextColor = Color.White,
                accompanimentTextColor = Color.Cyan,
                enableGradients = true,
                playingGradientColors = listOf(Color.Magenta, Color.Cyan)
            ),
            animation = AnimationConfig(
                enableCharacterAnimations = true,
                characterMaxScale = 1.3f,
                characterFloatOffset = 10f
            ),
            effects = EffectsConfig(
                enableBlur = true,
                blurIntensity = 1.2f,
                enableGlow = true,
                glowColor = Color.White
            )
        )
        else -> KaraokeLibraryConfig.Default
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Karaoke UI Library Demo",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { isPlaying = !isPlaying },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlaying) Color.Red else Color.Green
                        )
                    ) {
                        Text(if (isPlaying) "Pause" else "Play")
                    }

                    Button(
                        onClick = { currentTimeMs = 0 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                    ) {
                        Text("Reset")
                    }

                    Text(
                        text = "Time: ${currentTimeMs}ms",
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Config Presets:", color = Color.White)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("Default", "Minimal", "Dramatic", "Custom").forEach { preset ->
                        FilterChip(
                            selected = selectedConfig == preset,
                            onClick = { selectedConfig = preset },
                            label = { Text(preset) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Karaoke Display
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, Color.Gray)
                .padding(8.dp)
        ) {
            KaraokeLibrary.KaraokeLyricsDisplay(
                lines = KaraokeLibraryDemo.getSampleKaraokeLines(),
                currentTimeMs = currentTimeMs,
                config = config,
                onLineClick = { line, index ->
                    // Handle line click - could seek to line start
                    currentTimeMs = line.start
                }
            )
        }
    }
}

/**
 * Preview for single line display.
 */
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun PreviewSingleKaraokeLine() {
    val line = KaraokeLine(
        syllables = listOf(
            KaraokeSyllable("Hel", 0, 300),
            KaraokeSyllable("lo ", 300, 600),
            KaraokeSyllable("World", 600, 1200)
        ),
        start = 0,
        end = 1200,
        metadata = mapOf("alignment" to "center")
    )

    KaraokeLibrary.KaraokeLineDisplay(
        line = line,
        currentTimeMs = 500, // Middle of playback
        config = KaraokeLibraryConfig.Default
    )
}

/**
 * Preview for multiple lines with scrolling.
 */
@Preview(showBackground = true, backgroundColor = 0xFF000000, heightDp = 600)
@Composable
fun PreviewKaraokeLyrics() {
    KaraokeLibrary.KaraokeLyricsDisplay(
        lines = KaraokeLibraryDemo.getSampleKaraokeLines(),
        currentTimeMs = 2500, // Second line playing
        config = KaraokeLibraryConfig.Default
    )
}

/**
 * Preview with minimal config (no effects).
 */
@Preview(showBackground = true, backgroundColor = 0xFF000000, heightDp = 600)
@Composable
fun PreviewMinimalConfig() {
    KaraokeLibrary.KaraokeLyricsDisplay(
        lines = KaraokeLibraryDemo.getSampleKaraokeLines(),
        currentTimeMs = 2500,
        config = KaraokeLibraryConfig.Minimal
    )
}

/**
 * Preview with dramatic config (enhanced effects).
 */
@Preview(showBackground = true, backgroundColor = 0xFF000000, heightDp = 600)
@Composable
fun PreviewDramaticConfig() {
    KaraokeLibrary.KaraokeLyricsDisplay(
        lines = KaraokeLibraryDemo.getSampleKaraokeLines(),
        currentTimeMs = 2500,
        config = KaraokeLibraryConfig.Dramatic
    )
}

/**
 * Preview with custom config.
 */
@Preview(showBackground = true, backgroundColor = 0xFF000000, heightDp = 600)
@Composable
fun PreviewCustomConfig() {
    val customConfig = KaraokeLibraryConfig(
        visual = VisualConfig(
            fontSize = 28.sp,
            playingTextColor = Color(0xFFFFD700), // Gold
            playedTextColor = Color(0xFF808080),  // Gray
            upcomingTextColor = Color(0xFFFFFFFF).copy(alpha = 0.7f),
            accompanimentTextColor = Color(0xFF00CED1), // Dark Turquoise
            backgroundColor = Color(0xFF1A1A2E), // Dark Blue
            enableGradients = true,
            playingGradientColors = listOf(
                Color(0xFFFF6B6B),
                Color(0xFF4ECDC4)
            ),
            textAlign = TextAlign.Center
        ),
        animation = AnimationConfig(
            enableCharacterAnimations = true,
            characterMaxScale = 1.25f,
            characterFloatOffset = 8f,
            enableLineAnimations = true,
            lineScaleOnPlay = 1.1f
        ),
        effects = EffectsConfig(
            enableBlur = true,
            playedLineBlur = 3.dp,
            upcomingLineBlur = 2.dp,
            enableShadows = true,
            textShadowColor = Color.Black.copy(alpha = 0.5f),
            playingLineOpacity = 1f,
            playedLineOpacity = 0.3f,
            upcomingLineOpacity = 0.7f
        ),
        layout = LayoutConfig(
            lineSpacing = 20.dp,
            linePadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
        ),
        behavior = BehaviorConfig(
            scrollBehavior = ScrollBehavior.SMOOTH_CENTER,
            scrollAnimationDuration = 600
        )
    )

    KaraokeLibrary.KaraokeLyricsDisplay(
        lines = KaraokeLibraryDemo.getSampleKaraokeLines(),
        currentTimeMs = 4500, // Third line (accompaniment) playing
        config = customConfig
    )
}