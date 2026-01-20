package com.karaokelyrics.app.presentation.features.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.LyricsSource
import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.presentation.features.settings.mapper.SettingsUiMapper.backgroundColor
import com.karaokelyrics.app.presentation.features.settings.mapper.SettingsUiMapper.lyricsColor
import com.karaokelyrics.app.presentation.features.settings.viewdata.SettingsBottomSheetViewData
import com.karaokelyrics.app.presentation.features.settings.viewdata.SwitchColorsViewData
import com.karaokelyrics.app.presentation.ui.core.*

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
    onUpdateLyricsSource: (LyricsSource) -> Unit,
    onResetToDefaults: () -> Unit,
    viewData: SettingsBottomSheetViewData = SettingsBottomSheetViewData.default()
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = viewData.containerColor,
            contentColor = viewData.contentColor,
            tonalElevation = 0.dp,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = viewData.dragHandleColor
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
                AppText(
                    viewData = TextViewData(
                        text = "Settings",
                        style = viewData.titleStyle,
                        color = viewData.titleColor,
                        fontWeight = FontWeight.Bold
                    )
                )

                // Lyrics Source Section (for testing different formats)
                SettingsSection(
                    title = "Lyrics Source",
                    titleStyle = viewData.sectionTitleStyle,
                    titleColor = viewData.sectionTitleColor
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppText(
                            viewData = TextViewData(
                                text = "Select format to test parsing",
                                style = MaterialTheme.typography.bodySmall,
                                color = viewData.secondaryLabelColor
                            )
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(LyricsSource.entries.toList()) { source ->
                                LyricsSourceChip(
                                    onClick = { onUpdateLyricsSource(source) },
                                    chipViewData = ChipViewData.default(
                                        text = source.displayName,
                                        selected = source == settings.lyricsSource
                                    )
                                )
                            }
                        }
                    }
                }

                // Theme Section
                SettingsSection(
                    title = "Theme",
                    titleStyle = viewData.sectionTitleStyle,
                    titleColor = viewData.sectionTitleColor
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppText(
                            viewData = TextViewData(
                                text = "Dark Mode",
                                style = viewData.labelStyle,
                                color = viewData.labelColor,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Switch(
                            checked = settings.isDarkMode,
                            onCheckedChange = onUpdateDarkMode,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = viewData.switchColors.checkedThumbColor,
                                checkedTrackColor = viewData.switchColors.checkedTrackColor,
                                uncheckedThumbColor = viewData.switchColors.uncheckedThumbColor,
                                uncheckedTrackColor = viewData.switchColors.uncheckedTrackColor
                            )
                        )
                    }
                }

                // Colors Section
                SettingsSection(
                    title = "Colors",
                    titleStyle = viewData.sectionTitleStyle,
                    titleColor = viewData.sectionTitleColor
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Lyrics Color
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppText(
                                viewData = TextViewData(
                                    text = "Lyrics Color",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = viewData.secondaryLabelColor
                                )
                            )
                            ColorPicker(
                                selectedColor = settings.lyricsColor,
                                onColorSelected = onUpdateLyricsColor,
                                isDarkColors = false,
                                isCurrentlyDarkTheme = settings.isDarkMode,
                                borderColors = Pair(
                                    viewData.sectionTitleColor,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            )
                        }

                        // Background Color
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppText(
                                viewData = TextViewData(
                                    text = "Background Color",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = viewData.secondaryLabelColor
                                )
                            )
                            ColorPicker(
                                selectedColor = settings.backgroundColor,
                                onColorSelected = onUpdateBackgroundColor,
                                isDarkColors = true,
                                isCurrentlyDarkTheme = settings.isDarkMode,
                                borderColors = Pair(
                                    viewData.sectionTitleColor,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }

                // Font Section
                SettingsSection(
                    title = "Font Size",
                    titleStyle = viewData.sectionTitleStyle,
                    titleColor = viewData.sectionTitleColor
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(FontSize.values()) { fontSize ->
                            FontSizeChip(
                                fontSize = fontSize,
                                isSelected = fontSize == settings.fontSize,
                                onClick = { onUpdateFontSize(fontSize) },
                                chipViewData = ChipViewData.default(
                                    text = fontSize.displayName,
                                    selected = fontSize == settings.fontSize
                                )
                            )
                        }
                    }
                }

                // Animations Section
                SettingsSection(
                    title = "Animations",
                    titleStyle = viewData.sectionTitleStyle,
                    titleColor = viewData.sectionTitleColor
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        SettingsToggle(
                            title = "Enable Animations",
                            checked = settings.enableAnimations,
                            onCheckedChange = onUpdateAnimationsEnabled,
                            labelStyle = viewData.labelStyle,
                            labelColor = viewData.labelColor,
                            switchColors = viewData.switchColors
                        )

                        SettingsToggle(
                            title = "Blur Effect",
                            checked = settings.enableBlurEffect,
                            onCheckedChange = onUpdateBlurEffectEnabled,
                            enabled = settings.enableAnimations,
                            labelStyle = viewData.labelStyle,
                            labelColor = viewData.labelColor,
                            disabledColor = viewData.disabledTextColor,
                            switchColors = viewData.switchColors
                        )

                        SettingsToggle(
                            title = "Character Animations",
                            checked = settings.enableCharacterAnimations,
                            onCheckedChange = onUpdateCharacterAnimationsEnabled,
                            enabled = settings.enableAnimations,
                            labelStyle = viewData.labelStyle,
                            labelColor = viewData.labelColor,
                            disabledColor = viewData.disabledTextColor,
                            switchColors = viewData.switchColors
                        )
                    }
                }

                // Reset Button
                AppSurface(
                    viewData = SurfaceViewData(
                        backgroundColor = Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppButton(
                        viewData = ButtonViewData(
                            text = "Reset to Defaults",
                            backgroundColor = Color.Transparent,
                            contentColor = viewData.errorButtonColor,
                            border = BorderStroke(
                                width = 1.5.dp,
                                color = viewData.errorButtonBorderColor
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ),
                        onClick = onResetToDefaults,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    titleStyle: androidx.compose.ui.text.TextStyle,
    titleColor: Color,
    content: @Composable () -> Unit
) {
    AppSurface(
        viewData = SurfaceViewData(
            backgroundColor = Color.Transparent,
            contentColor = titleColor
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AppText(
                viewData = TextViewData(
                    text = title,
                    style = titleStyle,
                    color = titleColor,
                    fontWeight = FontWeight.SemiBold
                )
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
    enabled: Boolean = true,
    labelStyle: androidx.compose.ui.text.TextStyle,
    labelColor: Color,
    disabledColor: Color = labelColor.copy(alpha = 0.38f),
    switchColors: SwitchColorsViewData
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppText(
            viewData = TextViewData(
                text = title,
                style = labelStyle,
                color = if (enabled) labelColor else disabledColor
            )
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = switchColors.checkedThumbColor,
                checkedTrackColor = switchColors.checkedTrackColor,
                uncheckedThumbColor = switchColors.uncheckedThumbColor,
                uncheckedTrackColor = switchColors.uncheckedTrackColor,
                disabledCheckedThumbColor = switchColors.disabledCheckedThumbColor,
                disabledCheckedTrackColor = switchColors.disabledCheckedTrackColor,
                disabledUncheckedThumbColor = switchColors.disabledUncheckedThumbColor,
                disabledUncheckedTrackColor = switchColors.disabledUncheckedTrackColor
            )
        )
    }
}

@Composable
private fun ColorPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    isDarkColors: Boolean = false,
    isCurrentlyDarkTheme: Boolean = true,
    borderColors: Pair<Color, Color>
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
        Color(0xFFFFEB3B) // yellow
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
        Color(0xFFF9A825) // yellow
    )

    val darkBackgroundColors = listOf(
        spotifyBlack, // Original Spotify black
        Color(0xFF1A0E2E), // Deep purple black
        Color(0xFF0F2027), // Dark blue gradient base
        Color(0xFF1C1432), // Midnight purple
        Color(0xFF0D1929), // Navy blue black
        Color(0xFF1B1B2F), // Space blue
        Color(0xFF162447), // Dark royal blue
        Color(0xFF1F3A3D), // Dark teal
        Color(0xFF2D1B69) // Deep violet
    )

    val lightBackgroundColors = listOf(
        white, // Pure white
        Color(0xFFF3E5F5), // Light purple tint
        Color(0xFFE8F5E9), // Light green tint
        Color(0xFFFFF3E0), // Light amber tint
        Color(0xFFE3F2FD), // Light blue tint
        Color(0xFFFCE4EC), // Light pink tint
        Color(0xFFE0F2F1), // Light teal tint
        Color(0xFFFFF9C4), // Light yellow tint
        Color(0xFFF3E0FF) // Light lavender tint
    )

    val colors = when {
        isDarkColors && isCurrentlyDarkTheme -> darkBackgroundColors
        isDarkColors && !isCurrentlyDarkTheme -> lightBackgroundColors
        !isDarkColors && isCurrentlyDarkTheme -> darkLyricColors
        else -> lightLyricColors
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(colors) { color ->
            ColorSwatch(
                color = color,
                isSelected = color == selectedColor,
                onClick = { onColorSelected(color) },
                selectedBorderColor = borderColors.first,
                unselectedBorderColor = borderColors.second
            )
        }
    }
}

@Composable
private fun LyricsSourceChip(onClick: () -> Unit, chipViewData: ChipViewData) {
    AppChip(
        viewData = chipViewData,
        onClick = onClick
    )
}
