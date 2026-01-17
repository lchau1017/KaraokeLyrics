package com.karaokelyrics.app.presentation.features.settings.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.presentation.features.settings.mapper.SettingsUiMapper.lyricsColor
import com.karaokelyrics.app.presentation.features.settings.mapper.SettingsUiMapper.backgroundColor
import androidx.compose.ui.graphics.toArgb

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
    // Define color presets directly in presentation layer
    val spotifyGreen = Color(0xFF1DB954)
    val spotifyBlack = Color(0xFF121212)
    val white = Color(0xFFFFFFFF)

    val darkLyricColors = listOf(
        spotifyGreen,
        white,
        Color(0xFF9B59B6), // purple
        Color(0xFF3498DB), // blue
        Color(0xFFE74C3C), // red
        Color(0xFFF39C12), // orange
        Color(0xFFE91E63), // pink
        Color(0xFF00BCD4), // cyan
        Color(0xFFFFEB3B)  // yellow
    )

    val lightLyricColors = listOf(
        Color(0xFF27AE60), // green
        Color(0xFF2C3E50), // dark gray
        Color(0xFF8E44AD), // purple
        Color(0xFF2980B9), // blue
        Color(0xFFC0392B), // red
        Color(0xFFE67E22), // orange
        Color(0xFFD81B60), // pink
        Color(0xFF00ACC1), // cyan
        Color(0xFFF9A825)  // yellow
    )

    val darkBackgroundColors = listOf(
        spotifyBlack,
        Color(0xFF000000), // pure black
        Color(0xFF1A1A1A), // very dark gray
        Color(0xFF2C2C2C), // dark gray
        Color(0xFF0D0D0D), // near black
        Color(0xFF1E1E1E), // charcoal
        Color(0xFF101010), // jet black
        Color(0xFF0A0A0A), // onyx
        Color(0xFF141414)  // dark charcoal
    )

    val lightBackgroundColors = listOf(
        white,
        Color(0xFFF5F5F5), // light gray
        Color(0xFFECECEC), // very light gray
        Color(0xFFE0E0E0), // gray
        Color(0xFFFAFAFA), // off white
        Color(0xFFF0F0F0), // smoke white
        Color(0xFFF8F8F8), // ghost white
        Color(0xFFEEEEEE), // white smoke
        Color(0xFFFDFDFD)  // snow
    )

    val colors = when {
        isDarkColors && isCurrentlyDarkTheme -> darkBackgroundColors
        isDarkColors && !isCurrentlyDarkTheme -> lightBackgroundColors
        !isDarkColors && isCurrentlyDarkTheme -> darkLyricColors
        else -> lightLyricColors
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

// ColorSwatch is now imported from the public component

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
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = fontSize.displayName,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}