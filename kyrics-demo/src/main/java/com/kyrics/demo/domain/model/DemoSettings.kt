package com.kyrics.demo.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

/**
 * Immutable data class for all demo settings.
 * Using @Immutable helps Compose optimize recomposition.
 */
@Immutable
data class DemoSettings(
    // Text settings
    val fontSize: Float = 32f,
    val fontWeight: FontWeight = FontWeight.Bold,
    val fontFamily: FontFamily = FontFamily.Default,
    val textAlign: TextAlign = TextAlign.Center,

    // Colors
    val sungColor: Color = Color.Green,
    val unsungColor: Color = Color.White,
    val activeColor: Color = Color.Yellow,
    val backgroundColor: Color = Color.Black,

    // Visual effects
    val gradientEnabled: Boolean = false,
    val gradientAngle: Float = 45f,
    val blurEnabled: Boolean = false,
    val blurIntensity: Float = 1f,

    // Character animations
    val charAnimEnabled: Boolean = false,
    val charMaxScale: Float = 1.2f,
    val charFloatOffset: Float = 8f,
    val charRotationDegrees: Float = 5f,

    // Line animations
    val lineAnimEnabled: Boolean = false,
    val lineScaleOnPlay: Float = 1.05f,

    // Pulse effect
    val pulseEnabled: Boolean = false,
    val pulseMinScale: Float = 0.95f,
    val pulseMaxScale: Float = 1.05f,

    // Layout
    val lineSpacing: Float = 80f,

    // Viewer type (12 types total)
    val viewerTypeIndex: Int = 0
) {
    companion object {
        val Default = DemoSettings()
    }
}
