# Karaoke UI Library

A configurable, reusable Compose library for displaying synchronized karaoke lyrics with animations and effects.

## Features

- **Syllable-level synchronization**: Highlight individual syllables as they play
- **Rich animations**: Character animations, line scaling, fade effects
- **Visual effects**: Blur (opt-in), shadows, gradients
- **Fully configurable**: Control every visual aspect through configuration
- **Performance optimized**: Efficient rendering with lazy loading
- **RTL/LTR support**: Automatic text direction detection

## Usage

### Basic Usage

```kotlin
@Composable
fun MyKaraokeScreen() {
    KaraokeLibrary.KaraokeLyricsDisplay(
        lines = lyricsLines,
        currentTimeMs = currentPlaybackTime,
        config = KaraokeLibraryConfig.Default
    )
}
```

### Custom Configuration

```kotlin
val customConfig = KaraokeLibraryConfig(
    visual = VisualConfig(
        fontSize = 40.sp,
        playingTextColor = Color.Yellow,
        fontWeight = FontWeight.ExtraBold,
        enableGradients = true
    ),
    animation = AnimationConfig(
        enableCharacterAnimations = true,
        characterMaxScale = 1.2f
    ),
    effects = EffectsConfig(
        enableBlur = true  // Blur is disabled by default
    )
)

KaraokeLibrary.KaraokeLyricsDisplay(
    lines = lyricsLines,
    currentTimeMs = currentPlaybackTime,
    config = customConfig,
    onLineClick = { line, index ->
        // Handle line click
    }
)
```

### Mapping User Settings to Library Config

```kotlin
// In your app
fun mapUserSettingsToConfig(userSettings: UserSettings): KaraokeLibraryConfig {
    return KaraokeLibraryConfig(
        visual = VisualConfig(
            fontSize = userSettings.fontSize.sp,
            playingTextColor = Color(userSettings.textColorHex),
            textAlign = when(userSettings.alignment) {
                "left" -> TextAlign.Left
                "right" -> TextAlign.Right
                else -> TextAlign.Center
            }
        ),
        animation = AnimationConfig(
            enableCharacterAnimations = userSettings.enableAnimations
        ),
        effects = EffectsConfig(
            enableBlur = userSettings.enableBlur
        )
    )
}
```

## Configuration Options

### Visual Config
- Text colors (playing, played, upcoming, accompaniment)
- Font settings (size, family, weight, spacing)
- Text alignment
- Background colors
- Gradient settings

### Animation Config
- Character animations (scale, float, rotation)
- Line animations
- Transition animations
- Animation timing and easing

### Layout Config
- Line padding and spacing
- Word and character spacing
- Line height multipliers
- Container settings
- Text direction

### Effects Config
- Blur effects with intensity control (disabled by default, opt-in feature)
- Shadow effects
- Opacity settings for different states

### Behavior Config
- Scroll behavior (smooth, instant, paged)
- Interaction settings (click, long press, swipe)
- Performance optimization settings

## Integration

Add to your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":karaoke-ui-library"))
}
```

## License

Internal library for KaraokeLyrics app.