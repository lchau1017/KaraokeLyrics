package com.karaokelyrics.ui.rendering.effects

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Visual effects utilities for karaoke display.
 * Provides modifiers and helpers for applying various visual effects.
 */
object VisualEffects {

    /**
     * Apply blur effect based on configuration.
     *
     * @param enableBlur Whether blur is enabled
     * @param blurRadius Blur radius in Dp
     */
    fun Modifier.applyBlur(
        enableBlur: Boolean,
        blurRadius: Dp
    ): Modifier {
        return if (enableBlur && blurRadius > 0.dp) {
            this.blur(radius = blurRadius)
        } else {
            this
        }
    }

    /**
     * Apply conditional blur based on state.
     *
     * @param isPlaying Whether the element is currently playing
     * @param hasPlayed Whether the element has already played
     * @param playedBlur Blur for played elements
     * @param upcomingBlur Blur for upcoming elements
     * @param distantBlur Blur for distant elements
     * @param distanceThreshold Distance threshold for applying distant blur
     */
    fun Modifier.applyConditionalBlur(
        isPlaying: Boolean,
        hasPlayed: Boolean,
        playedBlur: Dp = 2.dp,
        upcomingBlur: Dp = 3.dp,
        distantBlur: Dp = 5.dp,
        distance: Int = 0,
        distanceThreshold: Int = 3
    ): Modifier {
        val blurRadius = when {
            isPlaying -> 0.dp
            hasPlayed -> playedBlur
            distance > distanceThreshold -> distantBlur
            else -> upcomingBlur
        }

        return if (blurRadius > 0.dp) {
            this.blur(radius = blurRadius)
        } else {
            this
        }
    }

    /**
     * Apply shadow effect.
     *
     * @param enableShadow Whether shadow is enabled
     * @param color Shadow color
     * @param elevation Shadow elevation
     * @param offset Shadow offset
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
     * Apply multiple effects in sequence.
     *
     * @param effects List of effect configurations to apply
     */
    fun Modifier.applyEffects(vararg effects: EffectConfig): Modifier {
        return effects.fold(this) { modifier, effect ->
            when (effect) {
                is EffectConfig.Blur -> modifier.applyBlur(effect.enabled, effect.radius)
                is EffectConfig.Shadow -> modifier.applyShadow(
                    effect.enabled,
                    effect.color,
                    effect.elevation,
                    effect.shape
                )
                is EffectConfig.Opacity -> modifier.then(
                    Modifier // Note: opacity should be handled via alpha in Color
                )
            }
        }
    }

    /**
     * Calculate blur radius based on distance from focus.
     *
     * @param distance Distance from focused element
     * @param maxBlur Maximum blur radius
     * @param blurFalloff Rate at which blur increases with distance
     */
    fun calculateDistanceBlur(
        distance: Int,
        maxBlur: Dp = 10.dp,
        blurFalloff: Float = 0.5f
    ): Dp {
        if (distance == 0) return 0.dp

        val blurValue = (distance * blurFalloff).coerceAtMost(maxBlur.value)
        return blurValue.dp
    }

    /**
     * Calculate opacity based on state and distance.
     *
     * @param isPlaying Whether element is currently playing
     * @param hasPlayed Whether element has already played
     * @param distance Distance from focused element
     * @param playingOpacity Opacity for playing elements
     * @param playedOpacity Opacity for played elements
     * @param upcomingOpacity Base opacity for upcoming elements
     * @param opacityFalloff Rate at which opacity decreases with distance
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
     * Configuration for different types of effects.
     */
    sealed class EffectConfig {
        data class Blur(
            val enabled: Boolean,
            val radius: Dp
        ) : EffectConfig()

        data class Shadow(
            val enabled: Boolean,
            val color: Color,
            val elevation: Dp,
            val shape: Shape = RectangleShape
        ) : EffectConfig()

        data class Opacity(
            val value: Float
        ) : EffectConfig()
    }

    /**
     * Preset effect combinations.
     */
    object Presets {
        val Subtle = listOf(
            EffectConfig.Blur(enabled = true, radius = 2.dp),
            EffectConfig.Shadow(
                enabled = true,
                color = Color.Black.copy(alpha = 0.1f),
                elevation = 2.dp
            )
        )

        val Dramatic = listOf(
            EffectConfig.Blur(enabled = true, radius = 5.dp),
            EffectConfig.Shadow(
                enabled = true,
                color = Color.Black.copy(alpha = 0.5f),
                elevation = 8.dp
            )
        )

        val Clean = emptyList<EffectConfig>()
    }
}