package com.karaokelyrics.app.presentation.ui.components

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
import com.karaokelyrics.app.domain.model.ColorPresets
import com.karaokelyrics.app.domain.model.FontSize
import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.presentation.ui.core.*
import com.karaokelyrics.app.presentation.ui.components.settings.SettingsBottomSheetViewData
import com.karaokelyrics.app.presentation.ui.components.settings.SwitchColorsViewData
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
                onClick = { onColorSelected(color) },
                selectedBorderColor = borderColors.first,
                unselectedBorderColor = borderColors.second
            )
        }
    }
}