package com.kyrics.demo.presentation.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyrics.demo.presentation.viewmodel.ColorPickerTarget
import com.kyrics.demo.presentation.viewmodel.DemoIntent
import com.kyrics.demo.presentation.viewmodel.DemoState
import java.util.Locale

/**
 * Stateless settings panel composable containing all demo controls.
 */
@Composable
fun SettingsPanel(state: DemoState, onIntent: (DemoIntent) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Playback controls
        ControlPanel(
            isPlaying = state.isPlaying,
            currentTimeMs = state.currentTimeMs,
            onPlayPause = { onIntent(DemoIntent.TogglePlayPause) },
            onReset = { onIntent(DemoIntent.Reset) },
            onSeek = { onIntent(DemoIntent.Seek(it)) }
        )

        SectionDivider()

        // Viewer Type
        SectionTitle("Viewer Type")
        ViewerTypeSelector(
            selectedIndex = state.settings.viewerTypeIndex,
            onSelectViewerType = { onIntent(DemoIntent.SelectViewerType(it)) }
        )

        SectionDivider()

        // Font settings
        FontSettingsSection(
            fontSize = state.settings.fontSize,
            fontWeight = state.settings.fontWeight,
            fontFamily = state.settings.fontFamily,
            textAlign = state.settings.textAlign,
            lineSpacing = state.settings.lineSpacing,
            onIntent = onIntent
        )

        SectionDivider()

        // Colors
        ColorsSection(
            sungColor = state.settings.sungColor,
            unsungColor = state.settings.unsungColor,
            activeColor = state.settings.activeColor,
            backgroundColor = state.settings.backgroundColor,
            onIntent = onIntent
        )

        SectionDivider()

        // Visual Effects
        VisualEffectsSection(
            gradientEnabled = state.settings.gradientEnabled,
            gradientAngle = state.settings.gradientAngle,
            blurEnabled = state.settings.blurEnabled,
            blurIntensity = state.settings.blurIntensity,
            onIntent = onIntent
        )

        SectionDivider()

        // Animations
        AnimationsSection(
            charAnimEnabled = state.settings.charAnimEnabled,
            charMaxScale = state.settings.charMaxScale,
            charFloatOffset = state.settings.charFloatOffset,
            charRotationDegrees = state.settings.charRotationDegrees,
            lineAnimEnabled = state.settings.lineAnimEnabled,
            lineScaleOnPlay = state.settings.lineScaleOnPlay,
            pulseEnabled = state.settings.pulseEnabled,
            pulseMinScale = state.settings.pulseMinScale,
            pulseMaxScale = state.settings.pulseMaxScale,
            onIntent = onIntent
        )

        SectionDivider()

        // Presets
        SectionTitle("Load Preset")
        PresetSelector(
            onSelectPreset = { onIntent(DemoIntent.LoadPreset(it)) }
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun FontSettingsSection(
    fontSize: Float,
    fontWeight: FontWeight,
    fontFamily: FontFamily,
    textAlign: TextAlign,
    lineSpacing: Float,
    onIntent: (DemoIntent) -> Unit
) {
    SectionTitle("Font Settings")

    Text("Size: ${fontSize.toInt()}sp")
    Slider(
        value = fontSize,
        onValueChange = { onIntent(DemoIntent.UpdateFontSize(it)) },
        valueRange = 12f..60f
    )

    Text("Font Weight")
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf(
            FontWeight.Light to "Light",
            FontWeight.Normal to "Normal",
            FontWeight.Bold to "Bold",
            FontWeight.Black to "Black"
        ).forEach { (weight, label) ->
            FilterChip(
                selected = fontWeight == weight,
                onClick = { onIntent(DemoIntent.UpdateFontWeight(weight)) },
                label = { Text(label, fontSize = 10.sp) }
            )
        }
    }

    Text("Font Family")
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf(
            FontFamily.Default to "Default",
            FontFamily.Serif to "Serif",
            FontFamily.Monospace to "Mono",
            FontFamily.Cursive to "Cursive"
        ).forEach { (family, label) ->
            FilterChip(
                selected = fontFamily == family,
                onClick = { onIntent(DemoIntent.UpdateFontFamily(family)) },
                label = { Text(label, fontSize = 10.sp) }
            )
        }
    }

    Text("Text Alignment")
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf(
            TextAlign.Start to "Left",
            TextAlign.Center to "Center",
            TextAlign.End to "Right"
        ).forEach { (align, label) ->
            FilterChip(
                selected = textAlign == align,
                onClick = { onIntent(DemoIntent.UpdateTextAlign(align)) },
                label = { Text(label, fontSize = 10.sp) }
            )
        }
    }

    Text("Line Spacing: ${lineSpacing.toInt()}dp")
    Slider(
        value = lineSpacing,
        onValueChange = { onIntent(DemoIntent.UpdateLineSpacing(it)) },
        valueRange = 0f..150f
    )
}

@Composable
private fun ColorsSection(
    sungColor: Color,
    unsungColor: Color,
    activeColor: Color,
    backgroundColor: Color,
    onIntent: (DemoIntent) -> Unit
) {
    SectionTitle("Colors")

    ColorRow("Sung", sungColor) {
        onIntent(DemoIntent.ShowColorPicker(ColorPickerTarget.SUNG_COLOR))
    }
    ColorRow("Unsung", unsungColor) {
        onIntent(DemoIntent.ShowColorPicker(ColorPickerTarget.UNSUNG_COLOR))
    }
    ColorRow("Active", activeColor) {
        onIntent(DemoIntent.ShowColorPicker(ColorPickerTarget.ACTIVE_COLOR))
    }
    ColorRow("Background", backgroundColor) {
        onIntent(DemoIntent.ShowColorPicker(ColorPickerTarget.BACKGROUND_COLOR))
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
private fun VisualEffectsSection(
    gradientEnabled: Boolean,
    gradientAngle: Float,
    blurEnabled: Boolean,
    blurIntensity: Float,
    onIntent: (DemoIntent) -> Unit
) {
    SectionTitle("Visual Effects")

    // Gradient
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(
            checked = gradientEnabled,
            onCheckedChange = { onIntent(DemoIntent.ToggleGradient(it)) }
        )
        Text("Gradient", modifier = Modifier.padding(start = 8.dp))
    }
    if (gradientEnabled) {
        Text("Angle: ${gradientAngle.toInt()}°", fontSize = 12.sp)
        Slider(
            value = gradientAngle,
            onValueChange = { onIntent(DemoIntent.UpdateGradientAngle(it)) },
            valueRange = 0f..360f
        )
    }

    // Blur
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(
            checked = blurEnabled,
            onCheckedChange = { onIntent(DemoIntent.ToggleBlur(it)) }
        )
        Text("Blur (for non-active lines)", modifier = Modifier.padding(start = 8.dp))
    }
    if (blurEnabled) {
        Text("Intensity: ${String.format(Locale.US, "%.1f", blurIntensity)}", fontSize = 12.sp)
        Slider(
            value = blurIntensity,
            onValueChange = { onIntent(DemoIntent.UpdateBlurIntensity(it)) },
            valueRange = 0.1f..3f
        )
    }
}

@Composable
private fun AnimationsSection(
    charAnimEnabled: Boolean,
    charMaxScale: Float,
    charFloatOffset: Float,
    charRotationDegrees: Float,
    lineAnimEnabled: Boolean,
    lineScaleOnPlay: Float,
    pulseEnabled: Boolean,
    pulseMinScale: Float,
    pulseMaxScale: Float,
    onIntent: (DemoIntent) -> Unit
) {
    SectionTitle("Animations")

    // Character animations
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(
            checked = charAnimEnabled,
            onCheckedChange = { onIntent(DemoIntent.ToggleCharAnimation(it)) }
        )
        Text("Character Animation", modifier = Modifier.padding(start = 8.dp))
    }
    if (charAnimEnabled) {
        Text("Max Scale: ${String.format(Locale.US, "%.2f", charMaxScale)}", fontSize = 12.sp)
        Slider(
            value = charMaxScale,
            onValueChange = { onIntent(DemoIntent.UpdateCharMaxScale(it)) },
            valueRange = 1f..2f
        )
        Text("Float Offset: ${charFloatOffset.toInt()}", fontSize = 12.sp)
        Slider(
            value = charFloatOffset,
            onValueChange = { onIntent(DemoIntent.UpdateCharFloatOffset(it)) },
            valueRange = 0f..20f
        )
        Text("Rotation: ${charRotationDegrees.toInt()}°", fontSize = 12.sp)
        Slider(
            value = charRotationDegrees,
            onValueChange = { onIntent(DemoIntent.UpdateCharRotation(it)) },
            valueRange = 0f..15f
        )
    }

    // Line animations
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(
            checked = lineAnimEnabled,
            onCheckedChange = { onIntent(DemoIntent.ToggleLineAnimation(it)) }
        )
        Text("Line Animation", modifier = Modifier.padding(start = 8.dp))
    }
    if (lineAnimEnabled) {
        Text("Scale on Play: ${String.format(Locale.US, "%.2f", lineScaleOnPlay)}", fontSize = 12.sp)
        Slider(
            value = lineScaleOnPlay,
            onValueChange = { onIntent(DemoIntent.UpdateLineScaleOnPlay(it)) },
            valueRange = 1f..1.5f
        )
    }

    // Pulse animation
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(
            checked = pulseEnabled,
            onCheckedChange = { onIntent(DemoIntent.TogglePulse(it)) }
        )
        Text("Pulse Effect", modifier = Modifier.padding(start = 8.dp))
    }
    if (pulseEnabled) {
        Text(
            "Pulse Range: ${String.format(Locale.US, "%.2f", pulseMinScale)} - ${String.format(Locale.US, "%.2f", pulseMaxScale)}",
            fontSize = 12.sp
        )
        Slider(
            value = pulseMinScale,
            onValueChange = { onIntent(DemoIntent.UpdatePulseMinScale(it)) },
            valueRange = 0.9f..1f
        )
        Slider(
            value = pulseMaxScale,
            onValueChange = { onIntent(DemoIntent.UpdatePulseMaxScale(it)) },
            valueRange = 1f..1.1f
        )
    }
}
