package com.karaokelyrics.ui.demo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karaokelyrics.ui.api.KaraokeLibrary
import com.karaokelyrics.ui.core.config.*
import com.karaokelyrics.ui.core.models.ISyncedLine
import com.karaokelyrics.ui.core.models.KaraokeLine
import com.karaokelyrics.ui.core.models.KaraokeSyllable

/**
 * Usage examples showing how to integrate the Karaoke UI Library in your app.
 */
object UsageExample {

    /**
     * Example 1: Basic usage with default configuration.
     */
    @Composable
    fun BasicUsage(
        lines: List<ISyncedLine>,
        currentTimeMs: Int
    ) {
        KaraokeLibrary.KaraokeLyricsDisplay(
            lines = lines,
            currentTimeMs = currentTimeMs
        )
    }

    /**
     * Example 2: Using preset configurations.
     */
    @Composable
    fun WithPresetConfig(
        lines: List<ISyncedLine>,
        currentTimeMs: Int,
        presetName: String
    ) {
        val config = when (presetName) {
            "minimal" -> KaraokeLibraryConfig.Minimal
            "dramatic" -> KaraokeLibraryConfig.Dramatic
            else -> KaraokeLibraryConfig.Default
        }

        KaraokeLibrary.KaraokeLyricsDisplay(
            lines = lines,
            currentTimeMs = currentTimeMs,
            config = config
        )
    }

    /**
     * Example 3: Mapping app user settings to library config.
     */
    data class AppUserSettings(
        val fontSize: Float = 34f,
        val textColor: String = "#FFFFFF",
        val enableAnimations: Boolean = true,
        val enableBlur: Boolean = true,
        val scrollMode: String = "smooth",
        val theme: String = "dark"
    )

    fun mapUserSettingsToConfig(settings: AppUserSettings): KaraokeLibraryConfig {
        return KaraokeLibraryConfig(
            visual = VisualConfig(
                fontSize = settings.fontSize.sp,
                playingTextColor = Color(android.graphics.Color.parseColor(settings.textColor)),
                fontWeight = FontWeight.Bold,
                backgroundColor = if (settings.theme == "dark") Color.Black else Color.White
            ),
            animation = AnimationConfig(
                enableCharacterAnimations = settings.enableAnimations,
                enableLineAnimations = settings.enableAnimations
            ),
            effects = EffectsConfig(
                enableBlur = settings.enableBlur
            ),
            behavior = BehaviorConfig(
                scrollBehavior = when (settings.scrollMode) {
                    "instant" -> ScrollBehavior.INSTANT_CENTER
                    "none" -> ScrollBehavior.NONE
                    else -> ScrollBehavior.SMOOTH_CENTER
                }
            )
        )
    }

    @Composable
    fun WithUserSettings(
        lines: List<ISyncedLine>,
        currentTimeMs: Int,
        userSettings: AppUserSettings
    ) {
        val config = remember(userSettings) {
            mapUserSettingsToConfig(userSettings)
        }

        KaraokeLibrary.KaraokeLyricsDisplay(
            lines = lines,
            currentTimeMs = currentTimeMs,
            config = config
        )
    }

    /**
     * Example 4: Custom configuration with specific styling.
     */
    @Composable
    fun CustomStyling(
        lines: List<ISyncedLine>,
        currentTimeMs: Int
    ) {
        val customConfig = KaraokeLibraryConfig(
            visual = VisualConfig(
                fontSize = 36.sp,
                playingTextColor = Color(0xFFFFD700), // Gold
                playedTextColor = Color.Gray,
                upcomingTextColor = Color.White.copy(alpha = 0.8f),
                accompanimentTextColor = Color.Cyan,
                enableGradients = true,
                playingGradientColors = listOf(
                    Color(0xFFFF00FF), // Magenta
                    Color(0xFF00FFFF)  // Cyan
                )
            ),
            animation = AnimationConfig(
                enableCharacterAnimations = true,
                characterMaxScale = 1.2f,
                lineScaleOnPlay = 1.05f
            ),
            effects = EffectsConfig(
                enableBlur = true,
                enableShadows = true,
                textShadowColor = Color.Black.copy(alpha = 0.4f)
            )
        )

        KaraokeLibrary.KaraokeLyricsDisplay(
            lines = lines,
            currentTimeMs = currentTimeMs,
            config = customConfig
        )
    }

    /**
     * Example 5: Handling user interactions.
     */
    @Composable
    fun WithInteractions(
        lines: List<ISyncedLine>,
        currentTimeMs: Int,
        onSeekToLine: (Int) -> Unit
    ) {
        KaraokeLibrary.KaraokeLyricsDisplay(
            lines = lines,
            currentTimeMs = currentTimeMs,
            config = KaraokeLibraryConfig(
                behavior = BehaviorConfig(
                    enableLineClick = true,
                    enableLineLongPress = true
                )
            ),
            onLineClick = { line, index ->
                // Seek to the start of clicked line
                onSeekToLine(line.start)
            },
            onLineLongPress = { line, index ->
                // Could show options menu or copy lyrics
                println("Long pressed line $index: ${line.getContent()}")
            }
        )
    }

    /**
     * Example 6: Single line display for current playing line.
     */
    @Composable
    fun SingleLineDisplay(
        currentLine: ISyncedLine?,
        currentTimeMs: Int
    ) {
        currentLine?.let { line ->
            KaraokeLibrary.KaraokeLineDisplay(
                line = line,
                currentTimeMs = currentTimeMs,
                config = KaraokeLibraryConfig(
                    visual = VisualConfig(
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            )
        }
    }

    /**
     * Example 7: Custom renderer for advanced use cases.
     */
    @Composable
    fun WithCustomRenderer(
        lines: List<ISyncedLine>,
        currentTimeMs: Int
    ) {
        KaraokeLibrary.KaraokeCustomDisplay(
            lines = lines,
            currentTimeMs = currentTimeMs,
            config = KaraokeLibraryConfig.Default,
            lineRenderer = { line, time, config ->
                // Custom rendering logic
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (time in line.start..line.end) {
                            Color.Blue.copy(alpha = 0.3f)
                        } else {
                            Color.Transparent
                        }
                    )
                ) {
                    Text(
                        text = line.getContent(),
                        modifier = Modifier.padding(16.dp),
                        color = Color.White
                    )
                }
            }
        )
    }

    /**
     * Example 8: Performance-optimized configuration.
     */
    @Composable
    fun PerformanceOptimized(
        lines: List<ISyncedLine>,
        currentTimeMs: Int
    ) {
        val performanceConfig = KaraokeLibraryConfig(
            animation = AnimationConfig(
                enableCharacterAnimations = false, // Disable heavy animations
                enableLineAnimations = true,
                lineAnimationDuration = 300f // Faster animations
            ),
            effects = EffectsConfig(
                enableBlur = false, // Disable blur for performance
                enableShadows = false,
                enableGlow = false
            ),
            behavior = BehaviorConfig(
                preloadLines = 3, // Reduce preload
                recycleDistance = 5
            )
        )

        KaraokeLibrary.KaraokeLyricsDisplay(
            lines = lines,
            currentTimeMs = currentTimeMs,
            config = performanceConfig
        )
    }
}

/**
 * Preview showing basic usage.
 */
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun PreviewBasicUsage() {
    val sampleLines = listOf(
        KaraokeLine(
            syllables = listOf(
                KaraokeSyllable("This ", 0, 400),
                KaraokeSyllable("is ", 400, 600),
                KaraokeSyllable("a ", 600, 800),
                KaraokeSyllable("test", 800, 1200)
            ),
            start = 0,
            end = 1200,
            metadata = mapOf("alignment" to "center")
        )
    )

    UsageExample.BasicUsage(
        lines = sampleLines,
        currentTimeMs = 500
    )
}

/**
 * Preview showing user settings integration.
 */
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun PreviewWithUserSettings() {
    val userSettings = UsageExample.AppUserSettings(
        fontSize = 40f,
        textColor = "#FFD700",
        enableAnimations = true,
        enableBlur = true,
        scrollMode = "smooth",
        theme = "dark"
    )

    UsageExample.WithUserSettings(
        lines = KaraokeLibraryDemo.getSampleKaraokeLines(),
        currentTimeMs = 2500,
        userSettings = userSettings
    )
}