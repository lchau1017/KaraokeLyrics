package com.karaokelyrics.ui.rendering

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.karaokelyrics.ui.core.config.GradientPreset
import com.karaokelyrics.ui.core.config.GradientType
import com.karaokelyrics.ui.core.config.KaraokeLibraryConfig
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Unified effects manager for all visual effects in karaoke display.
 * Consolidates gradients, shadows, glows, color calculations, and other visual effects.
 */
class EffectsManager {

    /**
     * Render a character with all configured effects.
     */
    fun renderCharacterWithEffects(
        drawScope: DrawScope,
        charLayout: TextLayoutResult,
        charX: Float,
        charY: Float,
        charColor: Color,
        config: KaraokeLibraryConfig,
        charProgress: Float,
        shimmerProgress: Float,
        animationState: AnimationManager.AnimationState? = null
    ) {
        with(drawScope) {
            // Apply animation transformations if provided
            if (animationState != null && (animationState.scale != 1f || animationState.rotation != 0f)) {
                drawIntoCanvas {
                    scale(
                        scale = animationState.scale,
                        pivot = Offset(charX + charLayout.size.width / 2f, charY + charLayout.size.height / 2f)
                    ) {
                        rotate(
                            degrees = animationState.rotation,
                            pivot = Offset(charX + charLayout.size.width / 2f, charY + charLayout.size.height / 2f)
                        ) {
                            renderCharacterLayers(
                                drawScope = this,
                                charLayout = charLayout,
                                charX = charX + animationState.offset.x,
                                charY = charY + animationState.offset.y,
                                charColor = charColor,
                                config = config,
                                charProgress = charProgress,
                                shimmerProgress = shimmerProgress
                            )
                        }
                    }
                }
            } else {
                renderCharacterLayers(
                    drawScope = this,
                    charLayout = charLayout,
                    charX = charX,
                    charY = charY,
                    charColor = charColor,
                    config = config,
                    charProgress = charProgress,
                    shimmerProgress = shimmerProgress
                )
            }
        }
    }

    private fun renderCharacterLayers(
        drawScope: DrawScope,
        charLayout: TextLayoutResult,
        charX: Float,
        charY: Float,
        charColor: Color,
        config: KaraokeLibraryConfig,
        charProgress: Float,
        shimmerProgress: Float
    ) {
        with(drawScope) {
            // 1. Draw shadow layer if enabled
            if (config.visual.shadowEnabled) {
                drawText(
                    textLayoutResult = charLayout,
                    color = config.visual.shadowColor.copy(alpha = 0.3f),
                    topLeft = Offset(
                        charX + config.visual.shadowOffset.x,
                        charY + config.visual.shadowOffset.y
                    )
                )
            }

            // 2. Draw glow layers if enabled
            if (config.visual.glowEnabled && charProgress > 0f) {
                listOf(
                    Offset(-2f, -2f) to 0.2f,
                    Offset(2f, 2f) to 0.2f,
                    Offset(-1f, -1f) to 0.3f,
                    Offset(1f, 1f) to 0.3f
                ).forEach { (offset, alpha) ->
                    drawText(
                        textLayoutResult = charLayout,
                        color = config.visual.glowColor.copy(alpha = alpha),
                        topLeft = Offset(charX + offset.x, charY + offset.y)
                    )
                }
            }

            // 3. Draw main character with appropriate effect
            when {
                config.animation.enableShimmer && shimmerProgress > 0f -> {
                    val shimmerGradient = createShimmerGradient(
                        progress = (charX / size.width + shimmerProgress) % 1f,
                        baseColor = charColor,
                        shimmerColor = Color(
                            red = minOf(1f, charColor.red + 0.3f),
                            green = minOf(1f, charColor.green + 0.3f),
                            blue = minOf(1f, charColor.blue + 0.3f),
                            alpha = charColor.alpha
                        ),
                        width = charLayout.size.width.toFloat()
                    )
                    drawText(
                        textLayoutResult = charLayout,
                        brush = shimmerGradient,
                        topLeft = Offset(charX, charY)
                    )
                }
                config.visual.gradientEnabled && charProgress > 0f -> {
                    val gradient = createCharacterGradient(
                        charLayout = charLayout,
                        charProgress = charProgress,
                        config = config,
                        baseColor = charColor
                    )
                    drawText(
                        textLayoutResult = charLayout,
                        brush = gradient,
                        topLeft = Offset(charX, charY)
                    )
                }
                else -> {
                    drawText(
                        textLayoutResult = charLayout,
                        color = charColor,
                        topLeft = Offset(charX, charY)
                    )
                }
            }
        }
    }

    /**
     * Create gradient brush for character based on configuration.
     */
    private fun createCharacterGradient(
        charLayout: TextLayoutResult,
        charProgress: Float,
        config: KaraokeLibraryConfig,
        baseColor: Color
    ): Brush = when (config.visual.gradientType) {
        GradientType.PROGRESS -> {
            createProgressGradient(
                progress = charProgress,
                baseColor = baseColor,
                highlightColor = config.visual.colors.active,
                width = charLayout.size.width.toFloat()
            )
        }
        GradientType.MULTI_COLOR -> {
            val colors = config.visual.playingGradientColors.takeIf { it.size > 1 }
                ?: listOf(config.visual.colors.active, config.visual.colors.sung)
            createMultiColorGradient(
                colors = colors,
                angle = config.visual.gradientAngle,
                width = charLayout.size.width.toFloat(),
                height = charLayout.size.height.toFloat()
            )
        }
        GradientType.PRESET -> {
            val presetColors = getPresetColors(config.visual.gradientPreset)
                ?: listOf(config.visual.colors.active, config.visual.colors.sung)
            createMultiColorGradient(
                colors = presetColors,
                angle = config.visual.gradientAngle,
                width = charLayout.size.width.toFloat(),
                height = charLayout.size.height.toFloat()
            )
        }
        else -> {
            createLinearGradient(
                colors = listOf(config.visual.colors.active, config.visual.colors.sung),
                angle = config.visual.gradientAngle,
                width = charLayout.size.width.toFloat(),
                height = charLayout.size.height.toFloat()
            )
        }
    }

    /**
     * Create a linear gradient brush based on angle.
     */
    private fun createLinearGradient(
        colors: List<Color>,
        angle: Float = 45f,
        width: Float = 1000f,
        height: Float = 100f
    ): Brush {
        val angleRad = angle * PI / 180
        val cos = cos(angleRad).toFloat()
        val sin = sin(angleRad).toFloat()
        val halfWidth = width / 2
        val halfHeight = height / 2

        return Brush.linearGradient(
            colors = colors,
            start = Offset(
                halfWidth - halfWidth * cos - halfHeight * sin,
                halfHeight - halfWidth * sin + halfHeight * cos
            ),
            end = Offset(
                halfWidth + halfWidth * cos + halfHeight * sin,
                halfHeight + halfWidth * sin - halfHeight * cos
            )
        )
    }

    /**
     * Create a progress-based gradient for highlighting.
     */
    private fun createProgressGradient(
        progress: Float,
        baseColor: Color,
        highlightColor: Color,
        width: Float = 1000f
    ): Brush {
        if (progress <= 0f) {
            return Brush.linearGradient(
                colors = listOf(baseColor, baseColor),
                start = Offset.Zero,
                end = Offset(width, 0f)
            )
        }

        if (progress >= 1f) {
            return Brush.linearGradient(
                colors = listOf(highlightColor, highlightColor),
                start = Offset.Zero,
                end = Offset(width, 0f)
            )
        }

        val stopPosition = progress.coerceIn(0f, 1f)

        return Brush.linearGradient(
            colorStops = arrayOf(
                0f to highlightColor,
                stopPosition to highlightColor,
                stopPosition to baseColor,
                1f to baseColor
            ),
            start = Offset.Zero,
            end = Offset(width, 0f)
        )
    }

    /**
     * Create a shimmer gradient effect.
     */
    private fun createShimmerGradient(
        progress: Float,
        baseColor: Color,
        shimmerColor: Color,
        width: Float = 1000f
    ): Brush {
        val shimmerWidth = 0.3f
        val position = progress.coerceIn(0f, 1f)

        return Brush.linearGradient(
            colorStops = arrayOf(
                0f to baseColor,
                (position - shimmerWidth).coerceAtLeast(0f) to baseColor,
                position to shimmerColor,
                (position + shimmerWidth).coerceAtMost(1f) to baseColor,
                1f to baseColor
            ),
            start = Offset.Zero,
            end = Offset(width, 0f)
        )
    }

    /**
     * Create a multi-color gradient.
     */
    private fun createMultiColorGradient(
        colors: List<Color>,
        angle: Float = 45f,
        width: Float = 1000f,
        height: Float = 100f
    ): Brush {
        if (colors.size < 2) {
            return Brush.linearGradient(
                colors = listOf(colors.firstOrNull() ?: Color.White, colors.firstOrNull() ?: Color.White)
            )
        }

        val stops = colors.mapIndexed { index, color ->
            (index.toFloat() / (colors.size - 1)) to color
        }.toTypedArray()

        val angleRad = angle * PI / 180
        val cos = cos(angleRad).toFloat()
        val sin = sin(angleRad).toFloat()
        val halfWidth = width / 2
        val halfHeight = height / 2

        return Brush.linearGradient(
            colorStops = stops,
            start = Offset(
                halfWidth - halfWidth * cos - halfHeight * sin,
                halfHeight - halfWidth * sin + halfHeight * cos
            ),
            end = Offset(
                halfWidth + halfWidth * cos + halfHeight * sin,
                halfHeight + halfWidth * sin - halfHeight * cos
            )
        )
    }

    /**
     * Get preset gradient colors.
     */
    private fun getPresetColors(preset: GradientPreset?): List<Color>? = when (preset) {
        GradientPreset.RAINBOW -> listOf(
            Color(0xFFFF0000),
            Color(0xFFFF7F00),
            Color(0xFFFFFF00),
            Color(0xFF00FF00),
            Color(0xFF0000FF),
            Color(0xFF4B0082),
            Color(0xFF9400D3)
        )
        GradientPreset.SUNSET -> listOf(
            Color(0xFFFF6B6B),
            Color(0xFFFFE66D),
            Color(0xFF4ECDC4)
        )
        GradientPreset.OCEAN -> listOf(
            Color(0xFF006BA6),
            Color(0xFF0496FF),
            Color(0xFF87CEEB)
        )
        GradientPreset.FIRE -> listOf(
            Color(0xFFFF0000),
            Color(0xFFFFA500),
            Color(0xFFFFFF00)
        )
        GradientPreset.NEON -> listOf(
            Color(0xFF00FFF0),
            Color(0xFFFF00FF),
            Color(0xFFFFFF00)
        )
        null -> null
    }

    /**
     * Apply blur effect to modifier.
     */
    fun Modifier.applyConditionalBlur(
        isPlaying: Boolean,
        hasPlayed: Boolean,
        distance: Int = 0,
        distanceThreshold: Int = 3,
        playedBlur: Dp = 2.dp,
        upcomingBlur: Dp = 3.dp,
        distantBlur: Dp = 5.dp,
        enableBlur: Boolean = true,
        blurIntensity: Float = 1.0f
    ): Modifier {
        if (!enableBlur) return this

        val baseBlurRadius = when {
            isPlaying -> 0.dp
            hasPlayed -> 0.dp
            distance > distanceThreshold -> distantBlur
            else -> upcomingBlur
        }

        val adjustedBlurRadius = (baseBlurRadius.value * blurIntensity).dp

        return if (adjustedBlurRadius > 0.dp) {
            this.blur(radius = adjustedBlurRadius)
        } else {
            this
        }
    }

    /**
     * Apply shadow effect to modifier.
     */
    fun Modifier.applyShadow(
        enableShadow: Boolean,
        color: Color = Color.Black.copy(alpha = 0.3f),
        elevation: Dp = 4.dp,
        shape: Shape = RectangleShape
    ): Modifier {
        return if (enableShadow) {
            this.shadow(
                elevation = elevation,
                shape = shape,
                clip = false
            )
        } else {
            this
        }
    }

    /**
     * Calculate opacity based on state and distance.
     */
    fun calculateOpacity(
        isPlaying: Boolean,
        hasPlayed: Boolean,
        distance: Int,
        playingOpacity: Float = 1f,
        playedOpacity: Float = 0.25f,
        upcomingOpacity: Float = 0.6f,
        opacityFalloff: Float = 0.1f
    ): Float {
        return when {
            isPlaying -> playingOpacity
            hasPlayed -> playedOpacity
            else -> {
                val distanceReduction = (distance * opacityFalloff).coerceAtMost(0.4f)
                (upcomingOpacity - distanceReduction).coerceAtLeast(0.2f)
            }
        }
    }

    /**
     * Calculate the color for a character based on its timing state
     */
    fun calculateCharacterColor(
        currentTimeMs: Int,
        charStartTime: Int,
        charEndTime: Int,
        baseColor: Color,
        playingColor: Color,
        playedColor: Color
    ): Color {
        return when {
            // Character has finished playing
            currentTimeMs > charEndTime -> playedColor

            // Character is currently playing
            currentTimeMs >= charStartTime -> {
                val progress = calculateColorProgress(currentTimeMs, charStartTime, charEndTime)
                lerpColor(baseColor, playingColor, progress)
            }

            // Character hasn't started yet
            else -> baseColor
        }
    }

    /**
     * Calculate line-level color based on state
     */
    fun calculateLineColor(
        isPlaying: Boolean,
        hasPlayed: Boolean,
        isAccompaniment: Boolean,
        playingTextColor: Color,
        playedTextColor: Color,
        upcomingTextColor: Color,
        accompanimentTextColor: Color
    ): Color {
        return when {
            isAccompaniment -> accompanimentTextColor
            isPlaying -> upcomingTextColor // Base color for unplayed chars in active line
            hasPlayed -> playedTextColor
            else -> upcomingTextColor
        }
    }

    /**
     * Calculate progress between start and end times for color interpolation
     */
    private fun calculateColorProgress(
        currentTime: Int,
        startTime: Int,
        endTime: Int
    ): Float {
        return if (endTime > startTime) {
            ((currentTime - startTime).toFloat() / (endTime - startTime))
                .coerceIn(0f, 1f)
        } else {
            1f
        }
    }

    /**
     * Interpolate between two colors
     */
    private fun lerpColor(start: Color, end: Color, fraction: Float): Color {
        return Color(
            red = start.red + (end.red - start.red) * fraction,
            green = start.green + (end.green - start.green) * fraction,
            blue = start.blue + (end.blue - start.blue) * fraction,
            alpha = start.alpha + (end.alpha - start.alpha) * fraction
        )
    }
}