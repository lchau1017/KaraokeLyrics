# Documentation

This folder contains media files and documentation for the KaraokeLyrics sample application.

## Media Files

### Screenshots

- **main-karaoke-player.png** - Main karaoke player app showing synchronized lyrics
- **kyrics-demo.png** - Kyrics library demo with configuration panel
- **settings-screen.png** - Settings screen with color and animation options

### Videos

- **karaoke-demo.webm** - Full demonstration of karaoke functionality
- **kyrics-demo.webm** - Kyrics library customization demo

## About

This sample application demonstrates how to integrate and use the [Kyrics](https://github.com/lchau1017/Kyrics) library for karaoke lyrics display in Android applications.

### Key Features (v1.2.0)

- **Multi-Format Support** - TTML, LRC, and Enhanced LRC formats with auto-detection
- **Synchronized Lyrics Display** - Real-time character-by-character highlighting
- **Multiple Viewer Types** - 12+ different display modes
- **Rich Customization** - Font settings, animations, gradients, and visual effects
- **Built-in Presets** - 10 ready-to-use theme presets

The Kyrics library is available via JitPack:

```kotlin
implementation("com.github.lchau1017:Kyrics:v1.2.0")
```

## Key Integration Points

### 1. Configuration with DSL

The app uses `LibraryConfigMapper` to convert user settings to Kyrics configuration using the DSL:

```kotlin
val config = kyricsConfig {
    colors {
        playing = primaryColor
        played = primaryColor.copy(alpha = 0.7f)
        upcoming = primaryColor.copy(alpha = 0.4f)
        accompaniment = primaryColor.copy(alpha = 0.5f)
        background = bgColor
    }
    typography { ... }
    animations { ... }
    effects { ... }
}
```

### 2. Lyrics Parsing with Auto-Detection

The `ParseLyricsUseCase` uses Kyrics library's `parseLyrics()` function which auto-detects the format:

```kotlin
import com.kyrics.parseLyrics
import com.kyrics.parser.ParseResult

// Supports TTML, LRC, and Enhanced LRC formats
when (val result = parseLyrics(content)) {
    is ParseResult.Success -> SyncedLyrics(result.lines)
    is ParseResult.Failure -> SyncedLyrics(emptyList())
}
```

### 3. Displaying Lyrics

The `KaraokeLyricsView` component wraps `KyricsViewer`:

```kotlin
KyricsViewer(
    lines = lyrics.lines,
    currentTimeMs = currentTimeMs,
    config = config,
    modifier = modifier
)
```

## Project Structure

```
docs/
├── images/
│   ├── main-karaoke-player.png  # Main app screenshot
│   ├── kyrics-demo.png          # Library demo screenshot
│   └── settings-screen.png      # Settings interface
├── videos/
│   ├── karaoke-demo.webm        # Main functionality demo
│   └── kyrics-demo.webm         # Library customization demo
└── README.md                    # This file
```

## Architecture Overview

The sample app follows Clean Architecture with MVI pattern:

```
┌─────────────────────────────────────────────────────────┐
│  Presentation Layer (MVI)                               │
│  - ViewModels, Screens, Components                      │
│  - LibraryConfigMapper (kyricsConfig DSL)               │
├─────────────────────────────────────────────────────────┤
│  Domain Layer                                           │
│  - Use Cases (ParseLyricsUseCase uses parseLyrics())    │
│  - Repository Interfaces, Business Models               │
├─────────────────────────────────────────────────────────┤
│  Data Layer                                             │
│  - Repository Implementations                           │
│  - Data Sources (with DispatcherProvider DI)            │
├─────────────────────────────────────────────────────────┤
│  Kyrics Library (v1.2.0)                                │
│  - KyricsViewer, KyricsConfig, parseLyrics()            │
│  - Multi-format support (TTML, LRC, Enhanced LRC)       │
└─────────────────────────────────────────────────────────┘
```

## Links

- **Kyrics Library:** [github.com/lchau1017/Kyrics](https://github.com/lchau1017/Kyrics)
- **JitPack:** [jitpack.io/#lchau1017/Kyrics](https://jitpack.io/#lchau1017/Kyrics)
- **Main README:** [../README.md](../README.md)
