package com.karaokelyrics.ui.rendering.animation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Manages animation states for karaoke display.
 * Handles transitions, timing, and animation orchestration.
 */
class AnimationStateManager {

    /**
     * State for line-level animations.
     */
    @Stable
    data class LineAnimationState(
        val scale: Float = 1f,
        val opacity: Float = 1f,
        val blur: Float = 0f,
        val offset: Offset = Offset.Zero,
        val isPlaying: Boolean = false,
        val isUpcoming: Boolean = false,
        val hasPlayed: Boolean = false
    )

    /**
     * Compose function to animate a line based on timing.
     */
    @Composable
    fun animateLine(
        lineStartTime: Int,
        lineEndTime: Int,
        currentTime: Int,
        scaleOnPlay: Float = 1.05f,
        animationDuration: Int = 700
    ): LineAnimationState {
        var state by remember { mutableStateOf(LineAnimationState()) }
        val scope = rememberCoroutineScope()

        val scale by animateFloatAsState(
            targetValue = when {
                currentTime in lineStartTime..lineEndTime -> scaleOnPlay
                else -> 1f
            },
            animationSpec = tween(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing
            ),
            label = "lineScale"
        )

        // Enhanced fade animations with smoother transitions
        val opacity by animateFloatAsState(
            targetValue = when {
                currentTime < lineStartTime - 3000 -> 0f  // Far upcoming lines are invisible
                currentTime < lineStartTime - 1000 -> 0.3f  // Fade in starts
                currentTime < lineStartTime -> 0.6f  // Almost ready
                currentTime in lineStartTime..lineEndTime -> 1f  // Fully visible when active
                currentTime > lineEndTime + 2000 -> 0.1f  // Fade out completed
                else -> 0.25f  // Just played
            },
            animationSpec = tween(
                durationMillis = 500,  // Smoother fade transition
                easing = FastOutSlowInEasing
            ),
            label = "lineOpacity"
        )

        val blur by animateFloatAsState(
            targetValue = when {
                currentTime < lineStartTime - 2000 -> 5f
                currentTime < lineStartTime -> 3f
                currentTime in lineStartTime..lineEndTime -> 0f
                else -> 2f
            },
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            ),
            label = "lineBlur"
        )

        LaunchedEffect(currentTime, lineStartTime, lineEndTime) {
            state = LineAnimationState(
                scale = scale,
                opacity = opacity,
                blur = blur,
                isPlaying = currentTime in lineStartTime..lineEndTime,
                isUpcoming = currentTime < lineStartTime,
                hasPlayed = currentTime > lineEndTime
            )
        }

        return state
    }

    /**
     * Animate syllable progress within a line.
     */
    @Composable
    fun animateSyllableProgress(
        syllableStartTime: Int,
        syllableEndTime: Int,
        currentTime: Int
    ): Float {
        val progress by animateFloatAsState(
            targetValue = when {
                currentTime <= syllableStartTime -> 0f
                currentTime >= syllableEndTime -> 1f
                else -> {
                    val duration = syllableEndTime - syllableStartTime
                    if (duration > 0) {
                        ((currentTime - syllableStartTime).toFloat() / duration).coerceIn(0f, 1f)
                    } else {
                        1f
                    }
                }
            },
            animationSpec = tween(
                durationMillis = 100,
                easing = LinearEasing
            ),
            label = "syllableProgress"
        )

        return progress
    }

    /**
     * Create a pulsing animation for active elements.
     */
    @Composable
    fun animatePulse(
        enabled: Boolean,
        minScale: Float = 0.95f,
        maxScale: Float = 1.05f,
        duration: Int = 1000
    ): Float {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")

        val scale by infiniteTransition.animateFloat(
            initialValue = minScale,
            targetValue = maxScale,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )

        return if (enabled) scale else 1f
    }

    /**
     * Animate color transition based on timing.
     */
    @Composable
    fun animateColorTransition(
        startTime: Int,
        endTime: Int,
        currentTime: Int,
        playingColor: androidx.compose.ui.graphics.Color,
        playedColor: androidx.compose.ui.graphics.Color,
        upcomingColor: androidx.compose.ui.graphics.Color
    ): androidx.compose.ui.graphics.Color {
        val targetColor = when {
            currentTime < startTime -> upcomingColor
            currentTime in startTime..endTime -> playingColor
            else -> playedColor
        }

        val animatedColor by animateColorAsState(
            targetValue = targetColor,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            ),
            label = "colorTransition"
        )

        return animatedColor
    }

    /**
     * Create a shimmer effect for loading or highlight states.
     */
    @Composable
    fun animateShimmer(
        enabled: Boolean,
        duration: Int = 2000
    ): Float {
        val infiniteTransition = rememberInfiniteTransition(label = "shimmer")

        val shimmerProgress by infiniteTransition.animateFloat(
            initialValue = -1f,
            targetValue = 2f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmerProgress"
        )

        return if (enabled) shimmerProgress else 0f
    }

    /**
     * Orchestrate multiple animations with delays.
     */
    @Composable
    fun orchestrateAnimations(
        items: List<Any>,
        delayPerItem: Long = 50L
    ): List<Boolean> {
        val animationStates = remember(items.size) {
            mutableStateListOf(*Array(items.size) { false })
        }

        LaunchedEffect(items) {
            items.indices.forEach { index ->
                launch {
                    delay(index * delayPerItem)
                    animationStates[index] = true
                }
            }
        }

        return animationStates
    }

    companion object {
        /**
         * Calculate timing offset for smooth transitions.
         */
        fun calculateTimingOffset(
            lineIndex: Int,
            totalLines: Int,
            baseOffset: Int = 200
        ): Int {
            val position = lineIndex.toFloat() / (totalLines - 1).coerceAtLeast(1)
            return (baseOffset * (1 - position)).toInt()
        }

        /**
         * Determine if animation should be simplified based on performance.
         */
        fun shouldSimplifyAnimation(
            totalLines: Int,
            enableComplexAnimations: Boolean,
            performanceThreshold: Int = 50
        ): Boolean {
            return !enableComplexAnimations || totalLines > performanceThreshold
        }
    }
}