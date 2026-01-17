package com.karaokelyrics.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.karaokelyrics.ui.api.KaraokeLibrary
import com.karaokelyrics.ui.core.config.*
import com.karaokelyrics.ui.core.models.KaraokeLine
import com.karaokelyrics.ui.core.models.KaraokeSyllable
import com.karaokelyrics.demo.data.DemoLyricsProvider
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

    // Visual settings
    var fontSize by remember { mutableStateOf(32f) }
    var fontWeight by remember { mutableStateOf(FontWeight.Bold) }
    var fontFamily by remember { mutableStateOf<FontFamily?>(null) }
    var textAlign by remember { mutableStateOf(TextAlign.Center) }

    // Colors
    var sungColor by remember { mutableStateOf(Color.Green) }
    var unsungColor by remember { mutableStateOf(Color.White) }
    var activeColor by remember { mutableStateOf(Color.Yellow) }
    var backgroundColor by remember { mutableStateOf(Color.Black) }

    // Effects toggles
    var gradientEnabled by remember { mutableStateOf(false) }
    var gradientAngle by remember { mutableStateOf(45f) }
    var shadowEnabled by remember { mutableStateOf(false) }
    var shadowColor by remember { mutableStateOf(Color.Black) }
    var shadowOffsetX by remember { mutableStateOf(2f) }
    var shadowOffsetY by remember { mutableStateOf(2f) }
    var glowEnabled by remember { mutableStateOf(false) }
    var glowColor by remember { mutableStateOf(Color.Yellow) }
    var blurEnabled by remember { mutableStateOf(false) }
    var blurIntensity by remember { mutableStateOf(1.0f) }

    // Animation settings
    var charAnimEnabled by remember { mutableStateOf(false) }
    var charMaxScale by remember { mutableStateOf(1.2f) }
    var charFloatOffset by remember { mutableStateOf(8f) }
    var charRotationDegrees by remember { mutableStateOf(5f) }
    var lineAnimEnabled by remember { mutableStateOf(false) }
    var lineScaleOnPlay by remember { mutableStateOf(1.05f) }

    // New animation settings
    var pulseEnabled by remember { mutableStateOf(false) }
    var pulseMinScale by remember { mutableStateOf(0.98f) }
    var pulseMaxScale by remember { mutableStateOf(1.02f) }
    var shimmerEnabled by remember { mutableStateOf(false) }
    var shimmerIntensity by remember { mutableStateOf(0.3f) }

    // Line spacing
    var lineSpacing by remember { mutableStateOf(80f) } // Very large spacing to show only active line

    // Show color picker dialog
    var showColorPicker by remember { mutableStateOf(false) }
    var colorPickerTarget by remember { mutableStateOf<String?>(null) }

    // Demo lyrics
    val demoLines = remember { DemoLyricsProvider.createDemoLyrics() }
    var selectedLineIndex by remember { mutableStateOf(0) }

    // Build config from current settings
    val currentConfig = remember(
        fontSize, fontWeight, fontFamily, textAlign,
        sungColor, unsungColor, activeColor, backgroundColor,
        gradientEnabled, gradientAngle, shadowEnabled, shadowColor,
        shadowOffsetX, shadowOffsetY, glowEnabled, glowColor,
        blurEnabled, blurIntensity, charAnimEnabled, charMaxScale,
        charFloatOffset, charRotationDegrees, lineAnimEnabled, lineScaleOnPlay,
        pulseEnabled, pulseMinScale, pulseMaxScale,
        shimmerEnabled, shimmerIntensity,
        lineSpacing
    ) {
        KaraokeLibraryConfig(
            visual = VisualConfig(
                fontSize = fontSize.sp,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                textAlign = textAlign,
                playingTextColor = activeColor,
                playedTextColor = sungColor,
                upcomingTextColor = unsungColor,
                backgroundColor = backgroundColor,
                gradientEnabled = gradientEnabled,
                gradientAngle = gradientAngle,
                shadowEnabled = shadowEnabled,
                shadowColor = shadowColor,
                shadowOffset = Offset(shadowOffsetX, shadowOffsetY),
                glowEnabled = glowEnabled,
                glowColor = glowColor,
                colors = ColorConfig(
                    sung = sungColor,
                    unsung = unsungColor,
                    active = activeColor
                )
            ),
            animation = AnimationConfig(
                enableCharacterAnimations = charAnimEnabled,
                characterMaxScale = charMaxScale,
                characterFloatOffset = charFloatOffset,
                characterRotationDegrees = charRotationDegrees,
                characterAnimationDuration = 800f,
                enableLineAnimations = lineAnimEnabled,
                lineScaleOnPlay = lineScaleOnPlay,
                lineAnimationDuration = 700f,
                enablePulse = pulseEnabled,
                pulseMinScale = pulseMinScale,
                pulseMaxScale = pulseMaxScale,
                enableShimmer = shimmerEnabled,
                shimmerIntensity = shimmerIntensity
            ),
            effects = EffectsConfig(
                enableBlur = blurEnabled,
                blurIntensity = blurIntensity,
                upcomingLineBlur = (3 * blurIntensity).dp,
                distantLineBlur = (6 * blurIntensity).dp
            ),
            layout = LayoutConfig(
                lineSpacing = lineSpacing.dp,
                // Adapt padding for demo's smaller viewport (1/3 of screen)
                contentTopPadding = 30.dp, // Smaller top padding for demo
                scrollTopOffset = 30.dp, // Match content top padding
                contentBottomPaddingRatio = 1.5f, // More bottom padding to hide upcoming
                activeGroupSpacing = 60.dp, // Space before active line
                upcomingGroupSpacing = 150.dp, // Very large gap to ensure upcoming lines are hidden
                containerPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
            )
        )
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
                    .background(backgroundColor)
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
                        Button(onClick = { currentTimeMs = 0; isPlaying = false }) {
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

                    // Font settings
                    Text("Font Settings", style = MaterialTheme.typography.titleMedium)

                    Text("Size: ${fontSize.toInt()}sp")
                    Slider(
                        value = fontSize,
                        onValueChange = { fontSize = it },
                        valueRange = 12f..60f
                    )

                    Text("Font Weight")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = fontWeight == FontWeight.Light,
                            onClick = { fontWeight = FontWeight.Light },
                            label = { Text("Light", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = fontWeight == FontWeight.Normal,
                            onClick = { fontWeight = FontWeight.Normal },
                            label = { Text("Normal", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = fontWeight == FontWeight.Bold,
                            onClick = { fontWeight = FontWeight.Bold },
                            label = { Text("Bold", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = fontWeight == FontWeight.Black,
                            onClick = { fontWeight = FontWeight.Black },
                            label = { Text("Black", fontSize = 10.sp) }
                        )
                    }

                    Text("Font Family")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = fontFamily == null,
                            onClick = { fontFamily = null },
                            label = { Text("Default", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = fontFamily == FontFamily.Serif,
                            onClick = { fontFamily = FontFamily.Serif },
                            label = { Text("Serif", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = fontFamily == FontFamily.Monospace,
                            onClick = { fontFamily = FontFamily.Monospace },
                            label = { Text("Mono", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = fontFamily == FontFamily.Cursive,
                            onClick = { fontFamily = FontFamily.Cursive },
                            label = { Text("Cursive", fontSize = 10.sp) }
                        )
                    }

                    Text("Text Alignment")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = textAlign == TextAlign.Start,
                            onClick = { textAlign = TextAlign.Start },
                            label = { Text("Left", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = textAlign == TextAlign.Center,
                            onClick = { textAlign = TextAlign.Center },
                            label = { Text("Center", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = textAlign == TextAlign.End,
                            onClick = { textAlign = TextAlign.End },
                            label = { Text("Right", fontSize = 10.sp) }
                        )
                    }

                    Text("Line Spacing: ${lineSpacing.toInt()}dp")
                    Slider(
                        value = lineSpacing,
                        onValueChange = { lineSpacing = it },
                        valueRange = 0f..150f  // Allow much larger spacing
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Colors
                    Text("Colors", style = MaterialTheme.typography.titleMedium)

                    ColorRow("Sung", sungColor) {
                        colorPickerTarget = "sung"
                        showColorPicker = true
                    }
                    ColorRow("Unsung", unsungColor) {
                        colorPickerTarget = "unsung"
                        showColorPicker = true
                    }
                    ColorRow("Active", activeColor) {
                        colorPickerTarget = "active"
                        showColorPicker = true
                    }
                    ColorRow("Background", backgroundColor) {
                        colorPickerTarget = "background"
                        showColorPicker = true
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Visual Effects
                    Text("Visual Effects", style = MaterialTheme.typography.titleMedium)

                    // Gradient
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = gradientEnabled, onCheckedChange = { gradientEnabled = it })
                        Text("Gradient", modifier = Modifier.padding(start = 8.dp))
                    }
                    if (gradientEnabled) {
                        Text("Angle: ${gradientAngle.toInt()}°", fontSize = 12.sp)
                        Slider(
                            value = gradientAngle,
                            onValueChange = { gradientAngle = it },
                            valueRange = 0f..360f
                        )
                    }

                    // Shadow
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = shadowEnabled, onCheckedChange = { shadowEnabled = it })
                        Text("Shadow", modifier = Modifier.padding(start = 8.dp))
                    }
                    if (shadowEnabled) {
                        ColorRow("Shadow Color", shadowColor) {
                            colorPickerTarget = "shadow"
                            showColorPicker = true
                        }
                        Text("Offset X: ${shadowOffsetX.toInt()}", fontSize = 12.sp)
                        Slider(
                            value = shadowOffsetX,
                            onValueChange = { shadowOffsetX = it },
                            valueRange = -10f..10f
                        )
                        Text("Offset Y: ${shadowOffsetY.toInt()}", fontSize = 12.sp)
                        Slider(
                            value = shadowOffsetY,
                            onValueChange = { shadowOffsetY = it },
                            valueRange = -10f..10f
                        )
                    }

                    // Glow
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = glowEnabled, onCheckedChange = { glowEnabled = it })
                        Text("Glow", modifier = Modifier.padding(start = 8.dp))
                    }
                    if (glowEnabled) {
                        ColorRow("Glow Color", glowColor) {
                            colorPickerTarget = "glow"
                            showColorPicker = true
                        }
                    }

                    // Blur
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = blurEnabled, onCheckedChange = { blurEnabled = it })
                        Text("Blur (for non-active lines)", modifier = Modifier.padding(start = 8.dp))
                    }
                    if (blurEnabled) {
                        Text("Intensity: ${String.format("%.1f", blurIntensity)}", fontSize = 12.sp)
                        Slider(
                            value = blurIntensity,
                            onValueChange = { blurIntensity = it },
                            valueRange = 0.1f..3f
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Animations
                    Text("Animations", style = MaterialTheme.typography.titleMedium)

                    // Character animations
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = charAnimEnabled, onCheckedChange = { charAnimEnabled = it })
                        Text("Character Animation", modifier = Modifier.padding(start = 8.dp))
                    }
                    if (charAnimEnabled) {
                        Text("Max Scale: ${String.format("%.2f", charMaxScale)}", fontSize = 12.sp)
                        Slider(
                            value = charMaxScale,
                            onValueChange = { charMaxScale = it },
                            valueRange = 1f..2f
                        )
                        Text("Float Offset: ${charFloatOffset.toInt()}", fontSize = 12.sp)
                        Slider(
                            value = charFloatOffset,
                            onValueChange = { charFloatOffset = it },
                            valueRange = 0f..20f
                        )
                        Text("Rotation: ${charRotationDegrees.toInt()}°", fontSize = 12.sp)
                        Slider(
                            value = charRotationDegrees,
                            onValueChange = { charRotationDegrees = it },
                            valueRange = 0f..15f
                        )
                    }

                    // Line animations
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = lineAnimEnabled, onCheckedChange = { lineAnimEnabled = it })
                        Text("Line Animation", modifier = Modifier.padding(start = 8.dp))
                    }
                    if (lineAnimEnabled) {
                        Text("Scale on Play: ${String.format("%.2f", lineScaleOnPlay)}", fontSize = 12.sp)
                        Slider(
                            value = lineScaleOnPlay,
                            onValueChange = { lineScaleOnPlay = it },
                            valueRange = 1f..1.5f
                        )
                    }

                    // Pulse animation
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = pulseEnabled, onCheckedChange = { pulseEnabled = it })
                        Text("Pulse Effect", modifier = Modifier.padding(start = 8.dp))
                    }
                    if (pulseEnabled) {
                        Text("Pulse Range: ${String.format("%.2f", pulseMinScale)} - ${String.format("%.2f", pulseMaxScale)}", fontSize = 12.sp)
                        Slider(
                            value = pulseMinScale,
                            onValueChange = { pulseMinScale = it },
                            valueRange = 0.9f..1f
                        )
                        Slider(
                            value = pulseMaxScale,
                            onValueChange = { pulseMaxScale = it },
                            valueRange = 1f..1.1f
                        )
                    }

                    // Shimmer animation
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = shimmerEnabled, onCheckedChange = { shimmerEnabled = it })
                        Text("Shimmer Effect", modifier = Modifier.padding(start = 8.dp))
                    }
                    if (shimmerEnabled) {
                        Text("Intensity: ${String.format("%.2f", shimmerIntensity)}", fontSize = 12.sp)
                        Slider(
                            value = shimmerIntensity,
                            onValueChange = { shimmerIntensity = it },
                            valueRange = 0.1f..1f
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
                                    preset.visual.let {
                                        fontSize = it.fontSize.value
                                        fontWeight = it.fontWeight
                                        sungColor = it.playedTextColor
                                        unsungColor = it.upcomingTextColor
                                        activeColor = it.playingTextColor
                                    }
                                    preset.animation.let {
                                        charAnimEnabled = it.enableCharacterAnimations
                                        lineAnimEnabled = it.enableLineAnimations
                                        pulseEnabled = it.enablePulse
                                        pulseMinScale = it.pulseMinScale
                                        pulseMaxScale = it.pulseMaxScale
                                        shimmerEnabled = it.enableShimmer
                                        shimmerIntensity = it.shimmerIntensity
                                    }
                                    gradientEnabled = false
                                    shadowEnabled = true
                                    glowEnabled = false
                                    blurEnabled = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Classic", fontSize = 10.sp)
                            }
                            Button(
                                onClick = {
                                    val preset = LibraryPresets.Neon
                                    preset.visual.let {
                                        fontSize = it.fontSize.value
                                        fontWeight = it.fontWeight
                                        sungColor = it.playedTextColor
                                        unsungColor = it.upcomingTextColor
                                        activeColor = it.playingTextColor
                                        gradientEnabled = it.gradientEnabled
                                        shadowEnabled = it.shadowEnabled
                                        glowEnabled = it.glowEnabled
                                    }
                                    preset.animation.let {
                                        charAnimEnabled = it.enableCharacterAnimations
                                        lineAnimEnabled = it.enableLineAnimations
                                        pulseEnabled = it.enablePulse
                                        shimmerEnabled = it.enableShimmer
                                        shimmerIntensity = it.shimmerIntensity
                                    }
                                    preset.effects.let {
                                        blurEnabled = it.enableBlur
                                        blurIntensity = it.blurIntensity
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Neon", fontSize = 10.sp)
                            }
                            Button(
                                onClick = {
                                    fontSize = 30f
                                    fontWeight = FontWeight.Normal
                                    sungColor = Color.Gray
                                    unsungColor = Color.LightGray
                                    activeColor = Color.Black
                                    backgroundColor = Color.White
                                    gradientEnabled = false
                                    shadowEnabled = false
                                    glowEnabled = false
                                    blurEnabled = false
                                    charAnimEnabled = false
                                    lineAnimEnabled = false
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
                "sung" -> sungColor
                "unsung" -> unsungColor
                "active" -> activeColor
                "background" -> backgroundColor
                "shadow" -> shadowColor
                "glow" -> glowColor
                else -> Color.White
            },
            onColorSelected = { color ->
                when (colorPickerTarget) {
                    "sung" -> sungColor = color
                    "unsung" -> unsungColor = color
                    "active" -> activeColor = color
                    "background" -> backgroundColor = color
                    "shadow" -> shadowColor = color
                    "glow" -> glowColor = color
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
private fun ColorPickerDialog(
    currentColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
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
                    Color(0xFFFF1493)  // Deep Pink
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