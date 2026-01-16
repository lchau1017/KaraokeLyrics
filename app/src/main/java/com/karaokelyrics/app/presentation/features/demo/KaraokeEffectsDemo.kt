package com.karaokelyrics.app.presentation.features.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karaokelyrics.app.domain.model.ISyncedLine
import com.karaokelyrics.app.domain.model.karaoke.KaraokeLine
import com.karaokelyrics.app.domain.model.karaoke.KaraokeSyllable
import com.karaokelyrics.ui.api.KaraokeLibrary
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import com.karaokelyrics.ui.core.config.LibraryPresets
import kotlinx.coroutines.delay

/**
 * Demo screen showcasing all karaoke effects and animations.
 * Allows switching between different presets to demonstrate various styles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaraokeEffectsDemo() {
    var currentTimeMs by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var selectedPreset by remember { mutableStateOf("Neon") }
    var selectedLineIndex by remember { mutableStateOf(0) }

    // Demo lyrics with various lengths to test wrapping and effects
    val demoLines = remember { createDemoLyrics() }

    // Get current preset configuration
    val currentConfig = remember(selectedPreset) {
        LibraryPresets.allPresets.find { it.first == selectedPreset }?.second
            ?: LibraryPresets.Classic
    }

    // Auto-play timer
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(100)
            currentTimeMs += 100
            // Loop after all lines
            if (currentTimeMs > 60000) {
                currentTimeMs = 0
                selectedLineIndex = 0
            }
        }
    }

    // Update selected line based on time
    LaunchedEffect(currentTimeMs) {
        val currentLine = demoLines.indexOfFirst { line ->
            currentTimeMs >= line.start && currentTimeMs <= line.end
        }
        if (currentLine >= 0) {
            selectedLineIndex = currentLine
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Karaoke Effects Demo - $selectedPreset",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(currentConfig.visual.backgroundColor)
        ) {
            // Karaoke Display Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(currentConfig.visual.backgroundColor)
            ) {
                // Display current line with selected preset
                if (selectedLineIndex in demoLines.indices) {
                    val currentLine = demoLines[selectedLineIndex]

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Previous line (faded)
                        if (selectedLineIndex > 0) {
                            DisplayLine(
                                line = demoLines[selectedLineIndex - 1],
                                currentTimeMs = currentTimeMs,
                                config = currentConfig,
                                alpha = 0.3f
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        // Current line (active)
                        DisplayLine(
                            line = currentLine,
                            currentTimeMs = currentTimeMs,
                            config = currentConfig,
                            alpha = 1f
                        )

                        // Next line (upcoming)
                        if (selectedLineIndex < demoLines.size - 1) {
                            Spacer(modifier = Modifier.height(20.dp))
                            DisplayLine(
                                line = demoLines[selectedLineIndex + 1],
                                currentTimeMs = currentTimeMs,
                                config = currentConfig,
                                alpha = 0.5f
                            )
                        }
                    }
                }
            }

            // Control Panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Playback controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = {
                            currentTimeMs = 0
                            isPlaying = false
                            selectedLineIndex = 0
                        }) {
                            Text("Reset")
                        }

                        FloatingActionButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Text(
                                if (isPlaying) "⏸" else "▶",
                                fontSize = 24.sp
                            )
                        }

                        // Time display
                        Text(
                            text = "${currentTimeMs / 1000}s",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Time slider
                    Slider(
                        value = currentTimeMs.toFloat(),
                        onValueChange = {
                            currentTimeMs = it.toInt()
                            // Update selected line
                            val line = demoLines.indexOfFirst { l ->
                                it >= l.start && it <= l.end
                            }
                            if (line >= 0) selectedLineIndex = line
                        },
                        valueRange = 0f..60000f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Preset selector title
                    Text(
                        "Select Preset Style:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preset selector - scrollable row
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(LibraryPresets.allPresets) { preset ->
                            FilterChip(
                                selected = selectedPreset == preset.first,
                                onClick = { selectedPreset = preset.first },
                                label = {
                                    Text(
                                        preset.first,
                                        fontWeight = if (selectedPreset == preset.first)
                                            FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Effect status display
                    Text(
                        "Active Effects:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Show which effects are enabled
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            EffectIndicator("Gradient", currentConfig.visual.gradientEnabled)
                            EffectIndicator("Shadow", currentConfig.visual.shadowEnabled)
                            EffectIndicator("Glow", currentConfig.visual.glowEnabled)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            EffectIndicator("Char Animation", currentConfig.animation.enableCharacterAnimations)
                            EffectIndicator("Line Animation", currentConfig.animation.enableLineAnimations)
                            EffectIndicator("Blur", currentConfig.effects.enableBlur)
                        }
                    }

                    // Preset description
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = getPresetDescription(selectedPreset),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DisplayLine(
    line: ISyncedLine,
    currentTimeMs: Int,
    config: KaraokeLibraryConfig,
    alpha: Float
) {
    val libraryLine = (line as? KaraokeLine)?.let { appLine ->
        com.karaokelyrics.ui.core.models.KaraokeLine(
            syllables = appLine.syllables.map { syllable ->
                com.karaokelyrics.ui.core.models.KaraokeSyllable(
                    content = syllable.content,
                    start = syllable.start,
                    end = syllable.end
                )
            },
            start = appLine.start,
            end = appLine.end,
            metadata = appLine.metadata
        )
    }

    libraryLine?.let {
        KaraokeLibrary.KaraokeLineDisplay(
            line = it,
            currentTimeMs = currentTimeMs,
            config = config,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(alpha),
            onLineClick = { _ -> }
        )
    }
}

@Composable
private fun EffectIndicator(name: String, active: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            "● ",
            color = if (active) Color.Green else Color.Gray,
            fontSize = 10.sp
        )
        Text(
            name,
            fontSize = 10.sp,
            color = if (active) MaterialTheme.colorScheme.onSurface else Color.Gray
        )
    }
}

private fun getPresetDescription(preset: String): String {
    return when (preset) {
        "Classic" -> "Simple and clean karaoke style with basic shadow"
        "Neon" -> "Cyberpunk style with gradient, glow, and animations"
        "Rainbow" -> "Multi-color gradient with rainbow effect"
        "Fire" -> "Warm colors with fire-like animations"
        "Ocean" -> "Cool blue tones with wave-like motion"
        "Retro" -> "80s style with bold colors and effects"
        "Minimal" -> "Clean, no effects, focus on readability"
        "Elegant" -> "Subtle gold/silver with gentle animations"
        "Party" -> "All effects maxed out for celebration!"
        "Matrix" -> "Green digital rain effect"
        else -> "Custom configuration"
    }
}

private fun createDemoLyrics(): List<KaraokeLine> {
    return listOf(
        // Short line
        KaraokeLine(
            syllables = listOf(
                KaraokeSyllable("Dream", 0, 500),
                KaraokeSyllable("ing ", 500, 1000),
                KaraokeSyllable("high", 1000, 2000)
            ),
            start = 0,
            end = 2000
        ),
        // Medium line
        KaraokeLine(
            syllables = listOf(
                KaraokeSyllable("Dan", 2000, 2400),
                KaraokeSyllable("cing ", 2400, 2800),
                KaraokeSyllable("in ", 2800, 3100),
                KaraokeSyllable("the ", 3100, 3400),
                KaraokeSyllable("moon", 3400, 4000),
                KaraokeSyllable("light", 4000, 5000)
            ),
            start = 2000,
            end = 5000
        ),
        // Long line to test wrapping
        KaraokeLine(
            syllables = listOf(
                KaraokeSyllable("This ", 5000, 5300),
                KaraokeSyllable("is ", 5300, 5500),
                KaraokeSyllable("a ", 5500, 5600),
                KaraokeSyllable("very ", 5600, 5900),
                KaraokeSyllable("long ", 5900, 6200),
                KaraokeSyllable("line ", 6200, 6500),
                KaraokeSyllable("to ", 6500, 6700),
                KaraokeSyllable("test ", 6700, 7000),
                KaraokeSyllable("the ", 7000, 7200),
                KaraokeSyllable("wrap", 7200, 7500),
                KaraokeSyllable("ping ", 7500, 7800),
                KaraokeSyllable("and ", 7800, 8000),
                KaraokeSyllable("all ", 8000, 8200),
                KaraokeSyllable("ef", 8200, 8400),
                KaraokeSyllable("fects", 8400, 9000)
            ),
            start = 5000,
            end = 9000
        ),
        // Emoji test
        KaraokeLine(
            syllables = listOf(
                KaraokeSyllable("Love ", 9000, 9500),
                KaraokeSyllable("❤️ ", 9500, 10000),
                KaraokeSyllable("Music ", 10000, 10500),
                KaraokeSyllable("🎵", 10500, 11000)
            ),
            start = 9000,
            end = 11000
        ),
        // Fast syllables
        KaraokeLine(
            syllables = listOf(
                KaraokeSyllable("Ra", 11000, 11100),
                KaraokeSyllable("pid ", 11100, 11200),
                KaraokeSyllable("fi", 11200, 11300),
                KaraokeSyllable("re ", 11300, 11400),
                KaraokeSyllable("syl", 11400, 11500),
                KaraokeSyllable("la", 11500, 11600),
                KaraokeSyllable("bles", 11600, 12000)
            ),
            start = 11000,
            end = 12000
        ),
        // Numbers and symbols
        KaraokeLine(
            syllables = listOf(
                KaraokeSyllable("Count: ", 12000, 12500),
                KaraokeSyllable("1 ", 12500, 12700),
                KaraokeSyllable("2 ", 12700, 12900),
                KaraokeSyllable("3 ", 12900, 13100),
                KaraokeSyllable("Go!", 13100, 14000)
            ),
            start = 12000,
            end = 14000
        ),
        // Different languages
        KaraokeLine(
            syllables = listOf(
                KaraokeSyllable("Hel", 14000, 14300),
                KaraokeSyllable("lo ", 14300, 14600),
                KaraokeSyllable("世", 14600, 14900),
                KaraokeSyllable("界", 14900, 15500)
            ),
            start = 14000,
            end = 15500
        ),
        // Grand finale
        KaraokeLine(
            syllables = listOf(
                KaraokeSyllable("🌟 ", 15500, 16000),
                KaraokeSyllable("A", 16000, 16200),
                KaraokeSyllable("MA", 16200, 16400),
                KaraokeSyllable("ZING ", 16400, 16800),
                KaraokeSyllable("EF", 16800, 17000),
                KaraokeSyllable("FECTS ", 17000, 17500),
                KaraokeSyllable("🌟", 17500, 18000)
            ),
            start = 15500,
            end = 18000
        )
    )
}