package com.kyrics.config

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * DSL marker for karaoke configuration builders.
 */
@DslMarker
annotation class KyricsConfigDsl

/**
 * Creates a [KyricsConfig] using a type-safe DSL builder.
 *
 * Example usage:
 * ```kotlin
 * val config = kyricsConfig {
 *     colors {
 *         playing = Color.Yellow
 *         played = Color.Green
 *         upcoming = Color.White
 *     }
 *     typography {
 *         fontSize = 34.sp
 *         fontWeight = FontWeight.Bold
 *     }
 *     animations {
 *         characterAnimations = true
 *         lineAnimations = true
 *         characterScale = 1.2f
 *     }
 *     effects {
 *         blur = true
 *         shadows = true
 *     }
 *     viewer {
 *         type = ViewerType.SMOOTH_SCROLL
 *     }
 * }
 * ```
 */
fun kyricsConfig(block: KyricsConfigBuilder.() -> Unit): KyricsConfig = KyricsConfigBuilder().apply(block).build()

/**
 * Main builder for [KyricsConfig].
 */
@KyricsConfigDsl
class KyricsConfigBuilder {
    private var colorsBuilder = ColorsBuilder()
    private var typographyBuilder = TypographyBuilder()
    private var animationsBuilder = AnimationsBuilder()
    private var effectsBuilder = EffectsBuilder()
    private var viewerBuilder = ViewerBuilder()
    private var layoutBuilder = LayoutBuilder()
    private var behaviorBuilder = BehaviorBuilder()
    private var gradientBuilder: GradientBuilder? = null

    /**
     * Configure text colors for different line states.
     */
    fun colors(block: ColorsBuilder.() -> Unit) {
        colorsBuilder.apply(block)
    }

    /**
     * Configure typography settings (font size, weight, family).
     */
    fun typography(block: TypographyBuilder.() -> Unit) {
        typographyBuilder.apply(block)
    }

    /**
     * Configure animation settings.
     */
    fun animations(block: AnimationsBuilder.() -> Unit) {
        animationsBuilder.apply(block)
    }

    /**
     * Configure visual effects (blur, shadows, opacity).
     */
    fun effects(block: EffectsBuilder.() -> Unit) {
        effectsBuilder.apply(block)
    }

    /**
     * Configure the viewer type and behavior.
     */
    fun viewer(block: ViewerBuilder.() -> Unit) {
        viewerBuilder.apply(block)
    }

    /**
     * Configure layout settings (spacing, padding).
     */
    fun layout(block: LayoutBuilder.() -> Unit) {
        layoutBuilder.apply(block)
    }

    /**
     * Configure behavior settings (scrolling, interaction).
     */
    fun behavior(block: BehaviorBuilder.() -> Unit) {
        behaviorBuilder.apply(block)
    }

    /**
     * Configure gradient effects.
     */
    fun gradient(block: GradientBuilder.() -> Unit) {
        gradientBuilder = GradientBuilder().apply(block)
    }

    internal fun build(): KyricsConfig {
        val colors = colorsBuilder.build()
        val typography = typographyBuilder.build()
        val gradient = gradientBuilder?.build()
        val animations = animationsBuilder.build()
        val effects = effectsBuilder.build()
        val viewer = viewerBuilder.build()
        val layout = layoutBuilder.build(viewer)
        val behavior = behaviorBuilder.build()

        return KyricsConfig(
            visual = VisualConfig(
                playingTextColor = colors.playing,
                playedTextColor = colors.played,
                upcomingTextColor = colors.upcoming,
                accompanimentTextColor = colors.accompaniment,
                fontSize = typography.fontSize,
                accompanimentFontSize = typography.accompanimentFontSize,
                fontFamily = typography.fontFamily,
                fontWeight = typography.fontWeight,
                letterSpacing = typography.letterSpacing,
                textAlign = typography.textAlign,
                backgroundColor = colors.background,
                lineBackgroundColor = colors.lineBackground,
                playingGradientColors = gradient?.colors ?: listOf(Color(0xFF00BCD4), Color(0xFFE91E63)),
                gradientAngle = gradient?.angle ?: 45f,
                gradientEnabled = gradient?.enabled ?: false,
                gradientType = gradient?.type ?: GradientType.LINEAR,
                gradientPreset = gradient?.preset,
                colors = ColorConfig(
                    sung = colors.sung,
                    unsung = colors.unsung,
                    active = colors.active
                )
            ),
            animation = animations,
            layout = layout,
            effects = effects,
            behavior = behavior
        )
    }
}

/**
 * Builder for color configuration.
 */
@KyricsConfigDsl
class ColorsBuilder {
    /** Color for the currently playing line */
    var playing: Color = Color.White

    /** Color for lines that have been sung */
    var played: Color = Color.Gray

    /** Color for upcoming lines */
    var upcoming: Color = Color.White.copy(alpha = 0.8f)

    /** Color for accompaniment/background vocals */
    var accompaniment: Color = Color(0xFFFFE082)

    /** Background color for the entire viewer */
    var background: Color = Color.Transparent

    /** Background color for individual lines */
    var lineBackground: Color = Color.Transparent

    /** Color for sung portions in gradient mode */
    var sung: Color = Color.Green

    /** Color for unsung portions in gradient mode */
    var unsung: Color = Color.White

    /** Color for active character in gradient mode */
    var active: Color = Color.Yellow

    internal fun build() = this
}

/**
 * Builder for typography configuration.
 */
@KyricsConfigDsl
class TypographyBuilder {
    /** Main font size for lyrics */
    var fontSize: TextUnit = 34.sp

    /** Font size for accompaniment text */
    var accompanimentFontSize: TextUnit = 20.sp

    /** Font family (null = system default) */
    var fontFamily: FontFamily? = null

    /** Font weight */
    var fontWeight: FontWeight = FontWeight.Bold

    /** Letter spacing */
    var letterSpacing: TextUnit = 0.sp

    /** Text alignment */
    var textAlign: TextAlign = TextAlign.Center

    internal fun build() = this
}

/**
 * Builder for animation configuration.
 */
@KyricsConfigDsl
class AnimationsBuilder {
    // Character animations
    /** Enable per-character animations (scale, float, rotate) */
    var characterAnimations: Boolean = true

    /** Duration of character animations in milliseconds */
    var characterDuration: Float = 800f

    /** Maximum scale for character pop effect */
    var characterScale: Float = 1.15f

    /** Vertical float offset for characters */
    var characterFloat: Float = 6f

    /** Rotation angle for character animations */
    var characterRotation: Float = 3f

    // Line animations
    /** Enable line-level animations */
    var lineAnimations: Boolean = true

    /** Scale factor when line is playing */
    var lineScale: Float = 1.05f

    /** Duration of line animations in milliseconds */
    var lineDuration: Float = 700f

    // Pulse
    /** Enable pulsing effect on active line */
    var pulse: Boolean = false

    /** Minimum scale during pulse */
    var pulseMin: Float = 0.98f

    /** Maximum scale during pulse */
    var pulseMax: Float = 1.02f

    /** Pulse cycle duration in milliseconds */
    var pulseDuration: Int = 1500

    // Color transition
    /** Enable smooth color transitions */
    var colorTransition: Boolean = true

    /** Duration of color transitions in milliseconds */
    var colorTransitionDuration: Int = 300

    // Fade
    /** Fade in duration in milliseconds */
    var fadeIn: Float = 300f

    /** Fade out duration in milliseconds */
    var fadeOut: Float = 500f

    /** Animation easing curve */
    var easing: Easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)

    internal fun build() = AnimationConfig(
        enableCharacterAnimations = characterAnimations,
        characterAnimationDuration = characterDuration,
        characterMaxScale = characterScale,
        characterFloatOffset = characterFloat,
        characterRotationDegrees = characterRotation,
        enableLineAnimations = lineAnimations,
        lineScaleOnPlay = lineScale,
        lineAnimationDuration = lineDuration,
        enablePulse = pulse,
        pulseMinScale = pulseMin,
        pulseMaxScale = pulseMax,
        pulseDuration = pulseDuration,
        enableColorTransition = colorTransition,
        colorTransitionDuration = colorTransitionDuration,
        fadeInDuration = fadeIn,
        fadeOutDuration = fadeOut,
        animationEasing = easing
    )
}

/**
 * Builder for effects configuration.
 */
@KyricsConfigDsl
class EffectsBuilder {
    // Blur
    /** Enable blur effect on non-active lines */
    var blur: Boolean = false

    /** Blur intensity multiplier */
    var blurIntensity: Float = 1.0f

    /** Blur radius for played lines */
    var playedBlur: Dp = 2.dp

    /** Blur radius for upcoming lines */
    var upcomingBlur: Dp = 3.dp

    /** Blur radius for distant lines */
    var distantBlur: Dp = 5.dp

    // Shadows
    /** Enable text shadows */
    var shadows: Boolean = true

    /** Shadow color */
    var shadowColor: Color = Color.Black.copy(alpha = 0.3f)

    /** Shadow offset */
    var shadowOffset: Offset = Offset(2f, 2f)

    /** Shadow blur radius */
    var shadowRadius: Float = 4f

    // Opacity
    /** Opacity for playing line */
    var playingOpacity: Float = 1f

    /** Opacity for played lines */
    var playedOpacity: Float = 0.25f

    /** Opacity for upcoming lines */
    var upcomingOpacity: Float = 0.6f

    /** Opacity for distant lines */
    var distantOpacity: Float = 0.3f

    // Visibility
    /** Number of lines visible around current */
    var visibleRange: Int = 3

    /** Opacity falloff per line distance */
    var opacityFalloff: Float = 0.1f

    internal fun build() = EffectsConfig(
        enableBlur = blur,
        blurIntensity = blurIntensity,
        playedLineBlur = playedBlur,
        upcomingLineBlur = upcomingBlur,
        distantLineBlur = distantBlur,
        enableShadows = shadows,
        textShadowColor = shadowColor,
        textShadowOffset = shadowOffset,
        textShadowRadius = shadowRadius,
        playingLineOpacity = playingOpacity,
        playedLineOpacity = playedOpacity,
        upcomingLineOpacity = upcomingOpacity,
        distantLineOpacity = distantOpacity,
        visibleLineRange = visibleRange,
        opacityFalloff = opacityFalloff
    )
}

/**
 * Builder for viewer configuration.
 */
@KyricsConfigDsl
class ViewerBuilder {
    /** Type of viewer to use */
    var type: ViewerType = ViewerType.SMOOTH_SCROLL

    /** Scroll position for SMOOTH_SCROLL (0.0-1.0, where 0.33 = top third) */
    var scrollPosition: Float = 0.33f

    internal fun build() = ViewerConfig(
        type = type,
        scrollPosition = scrollPosition
    )
}

/**
 * Builder for layout configuration.
 */
@KyricsConfigDsl
class LayoutBuilder {
    /** Padding around each line */
    var linePadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 12.dp)

    /** Vertical spacing between lines */
    var lineSpacing: Dp = 12.dp

    /** Spacing between words */
    var wordSpacing: Dp = 4.dp

    /** Spacing between characters */
    var characterSpacing: Dp = 0.dp

    /** Line height multiplier */
    var lineHeight: Float = 1.2f

    /** Line height multiplier for accompaniment */
    var accompanimentLineHeight: Float = 1.0f

    /** Container padding */
    var containerPadding: PaddingValues = PaddingValues(16.dp)

    /** Maximum line width (null = full width) */
    var maxLineWidth: Dp? = null

    /** Force text direction (null = auto-detect) */
    var textDirection: LayoutDirection? = null

    internal fun build(viewerConfig: ViewerConfig) = LayoutConfig(
        viewerConfig = viewerConfig,
        linePadding = linePadding,
        lineSpacing = lineSpacing,
        wordSpacing = wordSpacing,
        characterSpacing = characterSpacing,
        lineHeightMultiplier = lineHeight,
        accompanimentLineHeightMultiplier = accompanimentLineHeight,
        containerPadding = containerPadding,
        maxLineWidth = maxLineWidth,
        forceTextDirection = textDirection
    )
}

/**
 * Builder for behavior configuration.
 */
@KyricsConfigDsl
class BehaviorBuilder {
    /** Scroll behavior mode */
    var scrollBehavior: ScrollBehavior = ScrollBehavior.SMOOTH_CENTER

    /** Scroll animation duration in milliseconds */
    var scrollDuration: Int = 500

    /** Scroll offset from edge */
    var scrollOffset: Dp = 100.dp

    /** Enable line click interactions */
    var lineClickEnabled: Boolean = true

    internal fun build() = BehaviorConfig(
        scrollBehavior = scrollBehavior,
        scrollAnimationDuration = scrollDuration,
        scrollOffset = scrollOffset,
        enableLineClick = lineClickEnabled
    )
}

/**
 * Builder for gradient configuration.
 */
@KyricsConfigDsl
class GradientBuilder {
    /** Enable gradient effects */
    var enabled: Boolean = true

    /** Gradient type */
    var type: GradientType = GradientType.LINEAR

    /** Gradient angle in degrees */
    var angle: Float = 45f

    /** Colors for multi-color gradient */
    var colors: List<Color> = listOf(Color(0xFF00BCD4), Color(0xFFE91E63))

    /** Preset gradient pattern */
    var preset: GradientPreset? = null

    internal fun build() = this
}
