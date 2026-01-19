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

The Kyrics library is available via JitPack:

```kotlin
implementation("com.github.lchau1017:Kyrics:v1.1.1")
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

### 2. TTML Parsing with DSL

The `TtmlParserImpl` uses Kyrics DSL to create synchronized lyrics:

```kotlin
kyricsLine(start = start, end = end) {
    alignment("center")
    if (isAccompaniment) accompaniment()
    syllable(text, start = syllableStart, end = syllableEnd)
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
│  - Use Cases, Repository Interfaces                     │
│  - Business Models                                      │
├─────────────────────────────────────────────────────────┤
│  Data Layer                                             │
│  - TtmlParserImpl (kyricsLine DSL)                      │
│  - Repository Implementations                           │
│  - Data Sources (with DispatcherProvider DI)            │
├─────────────────────────────────────────────────────────┤
│  Kyrics Library                                         │
│  - KyricsViewer, KyricsConfig, SyncedLine               │
└─────────────────────────────────────────────────────────┘
```

## Links

- **Kyrics Library:** [github.com/lchau1017/Kyrics](https://github.com/lchau1017/Kyrics)
- **JitPack:** [jitpack.io/#lchau1017/Kyrics](https://jitpack.io/#lchau1017/Kyrics)
- **Main README:** [../README.md](../README.md)
