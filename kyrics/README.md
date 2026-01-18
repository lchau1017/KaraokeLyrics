# Kyrics

A Jetpack Compose library for displaying synchronized karaoke lyrics with customizable animations, effects, and multiple viewer styles.

## Features

- **Syllable-level synchronization** - Precise timing for each syllable
- **Character animations** - Scale, float, and rotate effects per character
- **Gradient text effects** - Progress-based and multi-color gradients
- **12 viewer types** - Scroll, stacked, carousel, wave, spiral, and more
- **10 built-in presets** - Ready-to-use styles (Neon, Fire, Ocean, etc.)
- **Compose-friendly DSL** - Type-safe configuration builder
- **Highly customizable** - Colors, fonts, animations, effects all configurable

## Usage

This library is part of the KaraokeLyrics project. To use it in your app, add the `kyrics` module as a dependency in your project.

### Basic Usage

```kotlin
import com.kyrics.KyricsViewer
import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable

@Composable
fun MyKaraokeScreen() {
    // Create your lyrics data
    val lyrics = listOf(
        KyricsLine(
            start = 0,
            end = 3000,
            syllables = listOf(
                KyricsSyllable("Hel", start = 0, end = 500),
                KyricsSyllable("lo ", start = 500, end = 1000),
                KyricsSyllable("World", start = 1000, end = 2500)
            )
        ),
        // ... more lines
    )

    // Track playback position (from your media player)
    var currentTimeMs by remember { mutableIntStateOf(0) }

    // Display the karaoke viewer
    KyricsViewer(
        lines = lyrics,
        currentTimeMs = currentTimeMs
    )
}
```

### Using Presets

```kotlin
import com.kyrics.KyricsViewer
import com.kyrics.KyricsPresets

KyricsViewer(
    lines = lyrics,
    currentTimeMs = currentTimeMs,
    config = KyricsPresets.Neon  // Or: Classic, Rainbow, Fire, Ocean, etc.
)
```

### Custom Configuration with DSL

```kotlin
import com.kyrics.KyricsViewer
import com.kyrics.config.kyricsConfig

val customConfig = kyricsConfig {
    colors {
        playing = Color.Yellow
        played = Color.Green
        upcoming = Color.White.copy(alpha = 0.6f)
        sung = Color.Cyan      // For gradient sung portion
        unsung = Color.White   // For gradient unsung portion
    }

    typography {
        fontSize = 36.sp
        fontWeight = FontWeight.Bold
    }

    animations {
        characterAnimations = true
        characterScale = 1.2f
        characterFloat = 8f
        lineAnimations = true
        lineScale = 1.05f
    }

    effects {
        blur = true
        blurIntensity = 0.8f
        shadows = true
        playedOpacity = 0.3f
        upcomingOpacity = 0.6f
    }

    gradient {
        enabled = true
        type = GradientType.PROGRESS
        angle = 0f
    }

    viewer {
        type = ViewerType.SMOOTH_SCROLL
        scrollPosition = 0.33f  // Top third
    }
}

KyricsViewer(
    lines = lyrics,
    currentTimeMs = currentTimeMs,
    config = customConfig
)
```

### Inline DSL Configuration

```kotlin
KyricsViewer(
    lines = lyrics,
    currentTimeMs = currentTimeMs
) {
    colors {
        playing = Color.Magenta
        sung = Color.Cyan
    }
    animations {
        characterScale = 1.3f
    }
}
```

## Data Models

### KyricsLine

```kotlin
data class KyricsLine(
    val syllables: List<KyricsSyllable>,
    override val start: Int,     // Start time in milliseconds
    override val end: Int,       // End time in milliseconds
    val metadata: Map<String, String> = emptyMap()
) : ISyncedLine
```

### KyricsSyllable

```kotlin
data class KyricsSyllable(
    val content: String,         // Text content (e.g., "Hel")
    val start: Int,              // Start time in milliseconds
    val end: Int                 // End time in milliseconds
)
```

### Custom Line Implementation

You can implement `ISyncedLine` for custom data sources:

```kotlin
interface ISyncedLine {
    val start: Int
    val end: Int
    fun getContent(): String
}
```

## Viewer Types

| Type | Description |
|------|-------------|
| `CENTER_FOCUSED` | Active line always centered, played lines fade above |
| `SMOOTH_SCROLL` | Natural reading flow, multiple lines visible |
| `STACKED` | Z-layer overlapping with depth effect |
| `HORIZONTAL_PAGED` | Swipe between lines as pages |
| `WAVE_FLOW` | Lines flow in sinusoidal wave pattern |
| `SPIRAL` | Circular arrangement with active line at center |
| `CAROUSEL_3D` | 3D cylindrical rotation |
| `SPLIT_DUAL` | Shows current and next line simultaneously |
| `ELASTIC_BOUNCE` | Physics-based spring animations |
| `FADE_THROUGH` | Pure opacity transitions |
| `RADIAL_BURST` | Lines emerge from center with ripple effect |
| `FLIP_CARD` | 3D card flip transitions |

## Built-in Presets

| Preset | Style |
|--------|-------|
| `Classic` | Simple yellow/green karaoke style |
| `Neon` | Vibrant cyan/magenta with gradients |
| `Rainbow` | Multi-color gradient animation |
| `Fire` | Warm orange/red flickering effect |
| `Ocean` | Cool blue tones with wave motion |
| `Retro` | 80s style with bold effects |
| `Minimal` | Clean, no-frills black/white |
| `Elegant` | Subtle gold/silver styling |
| `Party` | Maximum effects for high energy |
| `Matrix` | Green monospace cyber style |

## Configuration Reference

### Colors

```kotlin
colors {
    playing = Color.White           // Currently playing line
    played = Color.Gray             // Already sung lines
    upcoming = Color.White.copy(0.8f) // Upcoming lines
    accompaniment = Color(0xFFFFE082) // Background vocals
    background = Color.Transparent  // Viewer background
    lineBackground = Color.Transparent // Per-line background
    sung = Color.Green              // Gradient: sung portion
    unsung = Color.White            // Gradient: unsung portion
    active = Color.Yellow           // Gradient: active character
}
```

### Typography

```kotlin
typography {
    fontSize = 34.sp
    accompanimentFontSize = 20.sp
    fontFamily = FontFamily.Default
    fontWeight = FontWeight.Bold
    letterSpacing = 0.sp
    textAlign = TextAlign.Center
}
```

### Animations

```kotlin
animations {
    // Character animations
    characterAnimations = true
    characterDuration = 800f        // ms
    characterScale = 1.15f          // Max scale
    characterFloat = 6f             // Vertical offset
    characterRotation = 3f          // Degrees

    // Line animations
    lineAnimations = true
    lineScale = 1.05f
    lineDuration = 700f

    // Pulse effect
    pulse = false
    pulseMin = 0.98f
    pulseMax = 1.02f
    pulseDuration = 1500

    // Transitions
    colorTransition = true
    colorTransitionDuration = 300
    fadeIn = 300f
    fadeOut = 500f
}
```

### Effects

```kotlin
effects {
    // Blur
    blur = false
    blurIntensity = 1.0f
    playedBlur = 2.dp
    upcomingBlur = 3.dp
    distantBlur = 5.dp

    // Shadows
    shadows = true
    shadowColor = Color.Black.copy(alpha = 0.3f)
    shadowOffset = Offset(2f, 2f)
    shadowRadius = 4f

    // Opacity
    playingOpacity = 1f
    playedOpacity = 0.25f
    upcomingOpacity = 0.6f
    distantOpacity = 0.3f

    // Visibility
    visibleRange = 3
    opacityFalloff = 0.1f
}
```

### Gradient

```kotlin
gradient {
    enabled = true
    type = GradientType.PROGRESS    // LINEAR, PROGRESS, MULTI_COLOR, PRESET
    angle = 45f
    colors = listOf(Color.Cyan, Color.Magenta)
    preset = GradientPreset.RAINBOW // RAINBOW, SUNSET, OCEAN, FIRE, NEON
}
```

### Viewer

```kotlin
viewer {
    type = ViewerType.SMOOTH_SCROLL
    scrollPosition = 0.33f          // 0.0-1.0 (where active line appears)
}
```

### Behavior

```kotlin
behavior {
    scrollBehavior = ScrollBehavior.SMOOTH_CENTER
    scrollDuration = 500
    scrollOffset = 100.dp
    lineClickEnabled = true
}
```

## Line Click Handling

```kotlin
KyricsViewer(
    lines = lyrics,
    currentTimeMs = currentTimeMs,
    onLineClick = { line, index ->
        // Seek to the clicked line
        mediaPlayer.seekTo(line.start.toLong())
    }
)
```

## Requirements

- Android API 31+ (Android 12)
- Jetpack Compose
- Kotlin 1.9+

## License

```
MIT License

Copyright (c) 2024 Lung Chau

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
