package com.karaokelyrics.app.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.karaokelyrics.app.presentation.ui.theme.ColorStyles
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    isVisible: Boolean,
    settings: UserSettings,
    onDismiss: () -> Unit,
    onUpdateLyricsColor: (Color) -> Unit,
    onUpdateBackgroundColor: (Color) -> Unit,
    onUpdateFontSize: (FontSize) -> Unit,
    onUpdateAnimationsEnabled: (Boolean) -> Unit,
    onUpdateBlurEffectEnabled: (Boolean) -> Unit,
    onUpdateCharacterAnimationsEnabled: (Boolean) -> Unit,
    onUpdateDarkMode: (Boolean) -> Unit,
    onResetToDefaults: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = ColorStyles.bottomSheetBackground(),
            contentColor = ColorStyles.primaryText(),
            tonalElevation = 0.dp,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = ColorStyles.bottomSheetDragHandle()
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.80f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Title
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ColorStyles.primaryText()
                )

                // Theme Section
                SettingsSection(title = "Theme") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dark Mode",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = ColorStyles.primaryText()
                        )
                        Switch(
                            checked = settings.isDarkMode,
                            onCheckedChange = onUpdateDarkMode,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ColorStyles.switchThumbOn(),
                                checkedTrackColor = ColorStyles.switchTrackOn(),
                                uncheckedThumbColor = ColorStyles.switchThumbOff(),
                                uncheckedTrackColor = ColorStyles.switchTrackOff()
                            )
                        )
                    }
                }

                // Colors Section
                SettingsSection(title = "Colors") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Lyrics Color
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Lyrics Color",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ColorStyles.secondaryText()
                            )
                            ColorPicker(
                                selectedColor = settings.lyricsColor,
                                onColorSelected = onUpdateLyricsColor,
                                isDarkColors = false,
                                isCurrentlyDarkTheme = settings.isDarkMode
                            )
                        }

                        // Background Color
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Background Color",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ColorStyles.secondaryText()
                            )
                            ColorPicker(
                                selectedColor = settings.backgroundColor,
                                onColorSelected = onUpdateBackgroundColor,
                                isDarkColors = true,
                                isCurrentlyDarkTheme = settings.isDarkMode
                            )
                        }
                    }
                }

                // Font Section
                SettingsSection(title = "Font Size") {
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

                // Animations Section
                SettingsSection(title = "Animations") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SettingsToggle(
                            title = "Enable Animations",
                            checked = settings.enableAnimations,
                            onCheckedChange = onUpdateAnimationsEnabled
                        )

                        SettingsToggle(
                            title = "Blur Effect",
                            checked = settings.enableBlurEffect,
                            onCheckedChange = onUpdateBlurEffectEnabled,
                            enabled = settings.enableAnimations
                        )

                        SettingsToggle(
                            title = "Character Animations",
                            checked = settings.enableCharacterAnimations,
                            onCheckedChange = onUpdateCharacterAnimationsEnabled,
                            enabled = settings.enableAnimations
                        )
                    }
                }

                // Reset Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    color = Color.Transparent
                ) {
                    OutlinedButton(
                        onClick = onResetToDefaults,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = ColorStyles.errorButton()
                        ),
                        border = BorderStroke(
                            width = 1.5.dp,
                            color = ColorStyles.errorButtonBorder()
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Reset to Defaults",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent,
        contentColor = ColorStyles.primaryText()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = ColorStyles.sectionTitle(),
                fontWeight = FontWeight.SemiBold
            )
            content()
        }
    }
}

@Composable
private fun SettingsToggle(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (enabled) {
                ColorStyles.primaryText()
            } else {
                ColorStyles.disabledText()
            }
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ColorStyles.switchThumbOn(),
                checkedTrackColor = ColorStyles.switchTrackOn(),
                uncheckedThumbColor = ColorStyles.switchThumbOff(),
                uncheckedTrackColor = ColorStyles.switchTrackOff(),
                disabledCheckedThumbColor = ColorStyles.disabledText(),
                disabledCheckedTrackColor = ColorStyles.disabledBackground(),
                disabledUncheckedThumbColor = ColorStyles.disabledText(),
                disabledUncheckedTrackColor = ColorStyles.disabledBackground()
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
        horizontalArrangement = Arrangement.spacedBy(12.dp)
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
    Surface(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        color = Color.Transparent,
        border = BorderStroke(
            width = if (isSelected) 3.dp else 1.dp,
            color = if (isSelected) {
                ColorStyles.activeBorder()
            } else {
                ColorStyles.inactiveBorder()
            }
        ),
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isSelected) 3.dp else 1.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}

@Composable
private fun FontSizeChip(
    fontSize: FontSize,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = if (isSelected) {
            ColorStyles.chipBackgroundSelected()
        } else {
            ColorStyles.chipBackground()
        },
        shape = RoundedCornerShape(20.dp),
        tonalElevation = if (isSelected) 2.dp else 0.dp,
        border = if (isSelected) {
            null
        } else {
            BorderStroke(1.dp, ColorStyles.inactiveBorder())
        }
    ) {
        Text(
            text = fontSize.displayName,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) {
                ColorStyles.chipTextSelected()
            } else {
                ColorStyles.chipText()
            }
        )
    }
}