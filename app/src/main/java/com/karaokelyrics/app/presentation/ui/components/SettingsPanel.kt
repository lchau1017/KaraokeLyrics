package com.karaokelyrics.app.presentation.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.karaokelyrics.app.domain.model.ColorPresets
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.UserSettings

@Composable
fun SettingsPanel(
    settings: UserSettings,
    onUpdateLyricsColor: (Color) -> Unit,
    onUpdateBackgroundColor: (Color) -> Unit,
    onUpdateFontSize: (FontSize) -> Unit,
    onUpdateAnimationsEnabled: (Boolean) -> Unit,
    onUpdateBlurEffectEnabled: (Boolean) -> Unit,
    onUpdateCharacterAnimationsEnabled: (Boolean) -> Unit,
    onUpdateDarkMode: (Boolean) -> Unit,
    onResetToDefaults: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (expanded) 8.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(horizontal = 16.dp)
        ) {
            // Header with expand/collapse
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            if (expanded) {
                Column(
                    modifier = Modifier.padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Theme Section
                    SettingsSection(title = "Theme") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Dark Mode",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Switch(
                                checked = settings.isDarkMode,
                                onCheckedChange = onUpdateDarkMode,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }

                    // Colors Section
                    SettingsSection(title = "Colors") {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Lyrics Color
                            Text(
                                text = "Lyrics Color",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            ColorPicker(
                                selectedColor = settings.lyricsColor,
                                onColorSelected = onUpdateLyricsColor,
                                isDarkColors = false,
                                isCurrentlyDarkTheme = settings.isDarkMode
                            )

                            // Background Color
                            Text(
                                text = "Background Color",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            ColorPicker(
                                selectedColor = settings.backgroundColor,
                                onColorSelected = onUpdateBackgroundColor,
                                isDarkColors = true,
                                isCurrentlyDarkTheme = settings.isDarkMode
                            )
                        }
                    }

                    // Font Section
                    SettingsSection(title = "Font") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Font Size: ${settings.fontSize.displayName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(FontSize.values()) { fontSize ->
                                    FontSizeChip(
                                        fontSize = fontSize,
                                        isSelected = fontSize == settings.fontSize,
                                        onClick = { onUpdateFontSize(fontSize) }
                                    )
                                }
                            }
                        }
                    }

                    // Animations Section
                    SettingsSection(title = "Animations") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            SettingsToggle(
                                title = "Enable Animations",
                                subtitle = "Overall animation effects",
                                checked = settings.enableAnimations,
                                onCheckedChange = onUpdateAnimationsEnabled
                            )

                            SettingsToggle(
                                title = "Blur Effect",
                                subtitle = "Text blur transitions",
                                checked = settings.enableBlurEffect,
                                onCheckedChange = onUpdateBlurEffectEnabled
                            )

                            SettingsToggle(
                                title = "Character Animations",
                                subtitle = "Individual character effects",
                                checked = settings.enableCharacterAnimations,
                                onCheckedChange = onUpdateCharacterAnimationsEnabled
                            )
                        }
                    }

                    // Reset Button
                    OutlinedButton(
                        onClick = onResetToDefaults,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Text("Reset to Defaults")
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
private fun SettingsToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun ColorPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    isDarkColors: Boolean = false,
    isCurrentlyDarkTheme: Boolean = true
) {
    val colors = when {
        isDarkColors && isCurrentlyDarkTheme -> ColorPresets.darkBackgroundColors
        isDarkColors && !isCurrentlyDarkTheme -> ColorPresets.lightBackgroundColors
        !isDarkColors && isCurrentlyDarkTheme -> ColorPresets.darkLyricColors
        else -> ColorPresets.lightLyricColors
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(colors) { color ->
            ColorSwatch(
                color = color,
                isSelected = color == selectedColor,
                onClick = { onColorSelected(color) }
            )
        }
    }
}

@Composable
private fun ColorSwatch(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() }
            .then(
                if (isSelected) {
                    Modifier.background(
                        Color.White.copy(alpha = 0.3f),
                        CircleShape
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color.White, CircleShape)
            )
        }
    }
}

@Composable
private fun FontSizeChip(
    fontSize: FontSize,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                }
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = fontSize.displayName,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) {
                Color.White
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}