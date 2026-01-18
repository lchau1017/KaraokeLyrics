package com.kyrics.config

/**
 * Complete configuration for the Karaoke UI Library.
 * This is completely independent from any app user settings.
 * The app is responsible for mapping its user settings to this configuration.
 */
data class KyricsConfig(
    val visual: VisualConfig = VisualConfig(),
    val animation: AnimationConfig = AnimationConfig(),
    val layout: LayoutConfig = LayoutConfig(),
    val effects: EffectsConfig = EffectsConfig(),
    val behavior: BehaviorConfig = BehaviorConfig()
) {
    companion object {
        /**
         * Default configuration with balanced settings
         */
        val Default = KyricsConfig()

        /**
         * Minimal configuration with most effects disabled
         */
        val Minimal = KyricsConfig(
            effects = EffectsConfig(
                enableBlur = false,
                enableShadows = false
            ),
            animation = AnimationConfig(
                enableCharacterAnimations = false,
                enableLineAnimations = false
            )
        )

        /**
         * Dramatic configuration with enhanced effects
         */
        val Dramatic = KyricsConfig(
            animation = AnimationConfig(
                characterMaxScale = 1.3f,
                characterFloatOffset = 10f,
                characterRotationDegrees = 5f
            ),
            effects = EffectsConfig(
                enableBlur = true,
                blurIntensity = 1.5f
            )
        )
    }
}
