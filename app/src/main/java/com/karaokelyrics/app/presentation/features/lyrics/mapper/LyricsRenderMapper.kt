package com.karaokelyrics.app.presentation.features.lyrics.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.karaokelyrics.app.domain.model.SyncedLyrics
import com.karaokelyrics.app.domain.model.UserSettings
import com.karaokelyrics.app.presentation.features.lyrics.calculator.*
import com.karaokelyrics.app.presentation.features.lyrics.model.*
import javax.inject.Inject

/**
 * Mapper that uses injected calculators instead of static dependencies.
 * This follows Dependency Inversion Principle and makes the mapper testable.
 */
class LyricsRenderMapper @Inject constructor(
    private val renderCalculator: RenderCalculator
) {
    /**
     * Maps domain model to render state.
     * All calculations are delegated to injected calculators.
     */
    fun mapToRenderState(
        lyrics: SyncedLyrics,
        currentTimeMs: Int,
        userSettings: UserSettings,
        textStyle: TextStyle,
        accompanimentTextStyle: TextStyle,
        deviceCapabilities: DeviceCapabilities = DeviceCapabilities(
            supportsBlur = true,
            maxTextureSize = 4096,
            isLowEndDevice = false
        )
    ): LyricsRenderState {
        // Find focused indices
        val focusedIndices = findFocusedIndices(lyrics, currentTimeMs)
        
        // Create render context
        val context = RenderContext(
            currentTimeMs = currentTimeMs,
            allLines = lyrics.lines,
            focusedIndices = focusedIndices,
            userPreferences = UserRenderPreferences(
                textStyle = textStyle,
                textColor = Color(userSettings.lyricsColorArgb),
                accompanimentTextStyle = accompanimentTextStyle,
                enableAnimations = userSettings.enableAnimations,
                enableBlur = userSettings.enableBlurEffect,
                enableCharacterAnimations = userSettings.enableCharacterAnimations,
                timingOffset = userSettings.lyricsTimingOffsetMs
            ),
            deviceCapabilities = deviceCapabilities
        )

        // Map each line using the injected calculator
        val models = lyrics.lines.mapIndexed { index, line ->
            renderCalculator.calculateRenderModel(line, index, context)
        }.map { model ->
            // Update distance from active based on focused indices
            if (focusedIndices.isNotEmpty()) {
                val minDistance = focusedIndices.minOf { 
                    kotlin.math.abs(model.index - it) 
                }
                model.copy(
                    timing = model.timing.copy(
                        distanceFromActive = minDistance
                    )
                )
            } else model
        }

        // Determine scroll target
        val scrollTarget = if (focusedIndices.isNotEmpty()) {
            ScrollTarget(
                index = focusedIndices.first(),
                offset = 0,
                animated = true
            )
        } else null

        // Create global config
        val globalConfig = GlobalRenderConfig(
            lineSpacing = 12f,
            horizontalPadding = 16f,
            verticalPadding = 100f,
            scrollBehavior = ScrollBehavior.SMOOTH,
            renderMode = if (userSettings.enableCharacterAnimations) {
                RenderMode.KARAOKE
            } else {
                RenderMode.SIMPLE
            }
        )

        return LyricsRenderState(
            models = models,
            scrollTarget = scrollTarget,
            globalConfig = globalConfig
        )
    }

    private fun findFocusedIndices(
        lyrics: SyncedLyrics,
        currentTimeMs: Int
    ): Set<Int> {
        return lyrics.lines.mapIndexedNotNull { index, line ->
            if (currentTimeMs in line.start..line.end) index else null
        }.toSet()
    }
}