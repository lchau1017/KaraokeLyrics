package com.karaokelyrics.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.karaokelyrics.demo.data.DemoLyricsProvider
import com.karaokelyrics.ui.api.KaraokeLibrary
import com.karaokelyrics.ui.core.config.*
import kotlinx.coroutines.delay

/**
 * Main demo composable for the Karaoke UI Library.
 * Provides comprehensive customization controls for all library features.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaraokeLibraryDemo() {
    var currentTimeMs by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }

    // Use a single settings state for all configuration
    var settings by remember {
        mutableStateOf(
            DemoSettings(
                fontSize = 32f,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default,
                textAlign = TextAlign.Center,
                sungColor = Color.Green,
                unsungColor = Color.White,
                activeColor = Color.Yellow,
                backgroundColor = Color.Black,
                lineSpacing = 80f
            )
        )
    }

    // Show color picker dialog
    var showColorPicker by remember { mutableStateOf(false) }
    var colorPickerTarget by remember { mutableStateOf<String?>(null) }

    // Demo lyrics
    val demoLines = remember { DemoLyricsProvider.createDemoLyrics() }
    var selectedLineIndex by remember { mutableStateOf(0) }

    // Build config from current settings - use derivedStateOf for optimization
    val currentConfig by remember {
        derivedStateOf {
            KaraokeLibraryConfig(
                visual = VisualConfig(
                    fontSize = settings.fontSize.sp,
                    fontWeight = settings.fontWeight,
                    fontFamily = settings.fontFamily,
                    textAlign = settings.textAlign,
                    playingTextColor = settings.activeColor,
                    playedTextColor = settings.sungColor,
                    upcomingTextColor = settings.unsungColor,
                    backgroundColor = settings.backgroundColor,
                    gradientEnabled = settings.gradientEnabled,
                    gradientAngle = settings.gradientAngle,
                    colors = ColorConfig(
                        sung = settings.sungColor,
                        unsung = settings.unsungColor,
                        active = settings.activeColor
                    )
                ),
                animation = AnimationConfig(
                    enableCharacterAnimations = settings.charAnimEnabled,
                    characterMaxScale = settings.charMaxScale,
                    characterFloatOffset = settings.charFloatOffset,
                    characterRotationDegrees = settings.charRotationDegrees,
                    characterAnimationDuration = 800f,
                    enableLineAnimations = settings.lineAnimEnabled,
                    lineScaleOnPlay = settings.lineScaleOnPlay,
                    lineAnimationDuration = 700f,
                    enablePulse = settings.pulseEnabled,
                    pulseMinScale = settings.pulseMinScale,
                    pulseMaxScale = settings.pulseMaxScale,
                ),
                effects = EffectsConfig(
                    enableBlur = settings.blurEnabled,
                    blurIntensity = settings.blurIntensity,
                    upcomingLineBlur = (3 * settings.blurIntensity).dp,
                    distantLineBlur = (6 * settings.blurIntensity).dp
                ),
                layout = LayoutConfig(
                    viewerConfig = ViewerConfig(
                        type = when (settings.viewerTypeIndex) {
                            0 -> ViewerType.CENTER_FOCUSED
                            1 -> ViewerType.SMOOTH_SCROLL
                            2 -> ViewerType.STACKED
                            3 -> ViewerType.HORIZONTAL_PAGED
                            4 -> ViewerType.WAVE_FLOW
                            5 -> ViewerType.SPIRAL
                            6 -> ViewerType.CAROUSEL_3D
                            7 -> ViewerType.SPLIT_DUAL
                            8 -> ViewerType.ELASTIC_BOUNCE
                            9 -> ViewerType.FADE_THROUGH
                            10 -> ViewerType.RADIAL_BURST
                            11 -> ViewerType.FLIP_CARD
                            else -> ViewerType.CENTER_FOCUSED
                        }
                    ),
                    lineSpacing = settings.lineSpacing.dp,
                    containerPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
                )
            )
        }
    }

    // Auto-play timer
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(100)
            currentTimeMs += 100
            if (currentTimeMs > 20000) {
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
                title = { Text("Karaoke UI Library Demo") },
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
        ) {
            // Top - Display area (1/3 of screen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.33f)
                    .background(settings.backgroundColor)
            ) {
                // Use KaraokeLyricsViewer for automatic scrolling
                KaraokeLibrary.KaraokeLyricsViewer(
                    lines = demoLines,
                    currentTimeMs = currentTimeMs,
                    config = currentConfig,
                    modifier = Modifier.fillMaxSize(),
                    onLineClick = { line, index ->
                        // Handle line click if needed
                        selectedLineIndex = index
                    }
                )
            }

            // Bottom - Controls (2/3 of screen, scrollable)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.67f)
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Playback controls
                    Text("Playback", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            currentTimeMs = 0
                            isPlaying = false
                        }) {
                            Text("Reset")
                        }
                        Button(onClick = { isPlaying = !isPlaying }) {
                            Text(if (isPlaying) "Pause" else "Play")
                        }
                    }
                    Slider(
                        value = currentTimeMs.toFloat(),
                        onValueChange = { currentTimeMs = it.toInt() },
                        valueRange = 0f..20000f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Time: ${currentTimeMs / 1000}s")

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Viewer Type
                    Text("Viewer Type", style = MaterialTheme.typography.titleMedium)

                    // List of viewer types (12 total now)
                    val viewerTypes = listOf(
                        "Center", "Smooth", "Stacked", "H-Paged",
                        "Wave", "Spiral", "3D-Carousel", "Split",
                        "Bounce", "Fade", "Burst", "Flip"
                    )

                    // Scrollable row for all viewer types
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(viewerTypes) { index, name ->
                            FilterChip(
                                selected = settings.viewerTypeIndex == index,
                                onClick = { settings = settings.copy(viewerTypeIndex = index) },
                                label = { Text(name, fontSize = 10.sp) }
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Font settings
                    Text("Font Settings", style = MaterialTheme.typography.titleMedium)

                    Text("Size: ${settings.fontSize.toInt()}sp")
                    Slider(
                        value = settings.fontSize,
                        onValueChange = { settings = settings.copy(fontSize = it) },
                        valueRange = 12f..60f
                    )

                    Text("Font Weight")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = settings.fontWeight == FontWeight.Light,
                            onClick = { settings = settings.copy(fontWeight = FontWeight.Light) },
                            label = { Text("Light", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = settings.fontWeight == FontWeight.Normal,
                            onClick = { settings = settings.copy(fontWeight = FontWeight.Normal) },
                            label = { Text("Normal", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = settings.fontWeight == FontWeight.Bold,
                            onClick = { settings = settings.copy(fontWeight = FontWeight.Bold) },
                            label = { Text("Bold", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = settings.fontWeight == FontWeight.Black,
                            onClick = { settings = settings.copy(fontWeight = FontWeight.Black) },
                            label = { Text("Black", fontSize = 10.sp) }
                        )
                    }

                    Text("Font Family")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = settings.fontFamily == FontFamily.Default,
                            onClick = { settings = settings.copy(fontFamily = FontFamily.Default) },
                            label = { Text("Default", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = settings.fontFamily == FontFamily.Serif,
                            onClick = { settings = settings.copy(fontFamily = FontFamily.Serif) },
                            label = { Text("Serif", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = settings.fontFamily == FontFamily.Monospace,
                            onClick = { settings = settings.copy(fontFamily = FontFamily.Monospace) },
                            label = { Text("Mono", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = settings.fontFamily == FontFamily.Cursive,
                            onClick = { settings = settings.copy(fontFamily = FontFamily.Cursive) },
                            label = { Text("Cursive", fontSize = 10.sp) }
                        )
                    }

                    Text("Text Alignment")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = settings.textAlign == TextAlign.Start,
                            onClick = { settings = settings.copy(textAlign = TextAlign.Start) },
                            label = { Text("Left", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = settings.textAlign == TextAlign.Center,
                            onClick = { settings = settings.copy(textAlign = TextAlign.Center) },
                            label = { Text("Center", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = settings.textAlign == TextAlign.End,
                            onClick = { settings = settings.copy(textAlign = TextAlign.End) },
                            label = { Text("Right", fontSize = 10.sp) }
                        )
                    }

                    Text("Line Spacing: ${settings.lineSpacing.toInt()}dp")
                    Slider(
                        value = settings.lineSpacing,
                        onValueChange = { settings = settings.copy(lineSpacing = it) },
                        valueRange = 0f..150f // Allow much larger spacing
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Colors
                    Text("Colors", style = MaterialTheme.typography.titleMedium)

                    ColorRow("Sung", settings.sungColor) {
                        colorPickerTarget = "sung"
                        showColorPicker = true
                    }
                    ColorRow("Unsung", settings.unsungColor) {
                        colorPickerTarget = "unsung"
                        showColorPicker = true
                    }
                    ColorRow("Active", settings.activeColor) {
                        colorPickerTarget = "active"
                        showColorPicker = true
                    }
                    ColorRow("Background", settings.backgroundColor) {
                        colorPickerTarget = "background"
                        showColorPicker = true
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Visual Effects
                    Text("Visual Effects", style = MaterialTheme.typography.titleMedium)

                    // Gradient
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = settings.gradientEnabled, onCheckedChange = { settings = settings.copy(gradientEnabled = it) })
                        Text("Gradient", modifier = Modifier.padding(start = 8.dp))
                    }
                    if (settings.gradientEnabled) {
                        Text("Angle: ${settings.gradientAngle.toInt()}°", fontSize = 12.sp)
                        Slider(
                            value = settings.gradientAngle,
                            onValueChange = { settings = settings.copy(gradientAngle = it) },
                            valueRange = 0f..360f
                        )
                    }

                    // Blur
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = settings.blurEnabled, onCheckedChange = { settings = settings.copy(blurEnabled = it) })
                        Text("Blur (for non-active lines)", modifier = Modifier.padding(start = 8.dp))
                    }
                    if (settings.blurEnabled) {
                        Text("Intensity: ${String.format("%.1f", settings.blurIntensity)}", fontSize = 12.sp)
                        Slider(
                            value = settings.blurIntensity,
                            onValueChange = { settings = settings.copy(blurIntensity = it) },
                            valueRange = 0.1f..3f
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Animations
                    Text("Animations", style = MaterialTheme.typography.titleMedium)

                    // Character animations
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = settings.charAnimEnabled, onCheckedChange = { settings = settings.copy(charAnimEnabled = it) })
                        Text("Character Animation", modifier = Modifier.padding(start = 8.dp))
                    }
                    if (settings.charAnimEnabled) {
                        Text("Max Scale: ${String.format("%.2f", settings.charMaxScale)}", fontSize = 12.sp)
                        Slider(
                            value = settings.charMaxScale,
                            onValueChange = { settings = settings.copy(charMaxScale = it) },
                            valueRange = 1f..2f
                        )
                        Text("Float Offset: ${settings.charFloatOffset.toInt()}", fontSize = 12.sp)
                        Slider(
                            value = settings.charFloatOffset,
                            onValueChange = { settings = settings.copy(charFloatOffset = it) },
                            valueRange = 0f..20f
                        )
                        Text("Rotation: ${settings.charRotationDegrees.toInt()}°", fontSize = 12.sp)
                        Slider(
                            value = settings.charRotationDegrees,
                            onValueChange = { settings = settings.copy(charRotationDegrees = it) },
                            valueRange = 0f..15f
                        )
                    }

                    // Line animations
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = settings.lineAnimEnabled, onCheckedChange = { settings = settings.copy(lineAnimEnabled = it) })
                        Text("Line Animation", modifier = Modifier.padding(start = 8.dp))
                    }
                    if (settings.lineAnimEnabled) {
                        Text("Scale on Play: ${String.format("%.2f", settings.lineScaleOnPlay)}", fontSize = 12.sp)
                        Slider(
                            value = settings.lineScaleOnPlay,
                            onValueChange = { settings = settings.copy(lineScaleOnPlay = it) },
                            valueRange = 1f..1.5f
                        )
                    }

                    // Pulse animation
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = settings.pulseEnabled, onCheckedChange = { settings = settings.copy(pulseEnabled = it) })
                        Text("Pulse Effect", modifier = Modifier.padding(start = 8.dp))
                    }
                    if (settings.pulseEnabled) {
                        Text(
                            "Pulse Range: ${String.format(
                                "%.2f",
                                settings.pulseMinScale
                            )} - ${String.format("%.2f", settings.pulseMaxScale)}",
                            fontSize = 12.sp
                        )
                        Slider(
                            value = settings.pulseMinScale,
                            onValueChange = { settings = settings.copy(pulseMinScale = it) },
                            valueRange = 0.9f..1f
                        )
                        Slider(
                            value = settings.pulseMaxScale,
                            onValueChange = { settings = settings.copy(pulseMaxScale = it) },
                            valueRange = 1f..1.1f
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Preset buttons
                    Text("Load Preset", style = MaterialTheme.typography.titleMedium)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Button(
                                onClick = {
                                    val preset = LibraryPresets.Classic
                                    settings = settings.copy(
                                        fontSize = preset.visual.fontSize.value,
                                        fontWeight = preset.visual.fontWeight,
                                        sungColor = preset.visual.playedTextColor,
                                        unsungColor = preset.visual.upcomingTextColor,
                                        activeColor = preset.visual.playingTextColor,
                                        charAnimEnabled = preset.animation.enableCharacterAnimations,
                                        lineAnimEnabled = preset.animation.enableLineAnimations,
                                        pulseEnabled = preset.animation.enablePulse,
                                        pulseMinScale = preset.animation.pulseMinScale,
                                        pulseMaxScale = preset.animation.pulseMaxScale,
                                        gradientEnabled = false,
                                        blurEnabled = false
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Classic", fontSize = 10.sp)
                            }
                            Button(
                                onClick = {
                                    val preset = LibraryPresets.Neon
                                    settings = settings.copy(
                                        fontSize = preset.visual.fontSize.value,
                                        fontWeight = preset.visual.fontWeight,
                                        sungColor = preset.visual.playedTextColor,
                                        unsungColor = preset.visual.upcomingTextColor,
                                        activeColor = preset.visual.playingTextColor,
                                        gradientEnabled = preset.visual.gradientEnabled,
                                        charAnimEnabled = preset.animation.enableCharacterAnimations,
                                        lineAnimEnabled = preset.animation.enableLineAnimations,
                                        pulseEnabled = preset.animation.enablePulse,
                                        blurEnabled = preset.effects.enableBlur,
                                        blurIntensity = preset.effects.blurIntensity
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Neon", fontSize = 10.sp)
                            }
                            Button(
                                onClick = {
                                    settings = settings.copy(
                                        fontSize = 30f,
                                        fontWeight = FontWeight.Normal,
                                        sungColor = Color.Gray,
                                        unsungColor = Color.LightGray,
                                        activeColor = Color.Black,
                                        backgroundColor = Color.White,
                                        gradientEnabled = false,
                                        blurEnabled = false,
                                        charAnimEnabled = false,
                                        lineAnimEnabled = false
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Minimal", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Color picker dialog
    if (showColorPicker) {
        ColorPickerDialog(
            currentColor = when (colorPickerTarget) {
                "sung" -> settings.sungColor
                "unsung" -> settings.unsungColor
                "active" -> settings.activeColor
                "background" -> settings.backgroundColor
                else -> Color.White
            },
            onColorSelected = { color ->
                settings = when (colorPickerTarget) {
                    "sung" -> settings.copy(sungColor = color)
                    "unsung" -> settings.copy(unsungColor = color)
                    "active" -> settings.copy(activeColor = color)
                    "background" -> settings.copy(backgroundColor = color)
                    else -> settings
                }
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }
}

@Composable
private fun ColorRow(label: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f), fontSize = 12.sp)
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(color)
                .border(1.dp, Color.Gray)
        )
    }
}

@Composable
private fun ColorPickerDialog(currentColor: Color, onColorSelected: (Color) -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Select Color", style = MaterialTheme.typography.titleMedium)

                // Predefined colors
                val colors = listOf(
                    Color.White, Color.Black, Color.Red, Color.Green,
                    Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta,
                    Color.Gray, Color.LightGray, Color.DarkGray,
                    Color(0xFFFFD700), // Gold
                    Color(0xFFC0C0C0), // Silver
                    Color(0xFFFF6347), // Tomato
                    Color(0xFF00CED1), // Dark Turquoise
                    Color(0xFFFF1493) // Deep Pink
                )

                colors.chunked(4).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(color)
                                    .border(
                                        width = if (color == currentColor) 3.dp else 1.dp,
                                        color = if (color == currentColor) Color.Blue else Color.Gray
                                    )
                                    .clickable { onColorSelected(color) }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
