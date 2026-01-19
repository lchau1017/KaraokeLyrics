# KaraokeLyrics

**A sample Android application demonstrating the [Kyrics](https://github.com/lchau1017/Kyrics) library - a modern, highly customizable karaoke lyrics library built with Jetpack Compose**

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![API](https://img.shields.io/badge/API-31%2B-brightgreen.svg)](https://android-arsenal.com/api?level=31)
[![Kotlin](https://img.shields.io/badge/kotlin-2.1.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-2024.12.01-blue.svg)](https://developer.android.com/compose)
[![JitPack](https://jitpack.io/v/lchau1017/Kyrics.svg)](https://jitpack.io/#lchau1017/Kyrics)

<div align="center">
  <h3>See It In Action</h3>
  <table>
    <tr>
      <td align="center">
        <img src="./docs/images/main-karaoke-player.png" alt="Main Karaoke Player" width="250"/>
        <br/><sub><b>Main Karaoke Player</b></sub>
      </td>
      <td align="center">
        <img src="./docs/images/kyrics-demo.png" alt="Kyrics Library Demo" width="250"/>
        <br/><sub><b>Kyrics Library Demo</b></sub>
      </td>
      <td align="center">
        <img src="./docs/images/settings-screen.png" alt="Settings Screen" width="250"/>
        <br/><sub><b>Settings & Customization</b></sub>
      </td>
    </tr>
  </table>
  <br/>

  **Demo Videos:**
  <a href="./docs/videos/karaoke-demo.webm"><b>Watch Full Demo</b></a> (45MB) |
  <a href="./docs/videos/kyrics-demo.webm"><b>Kyrics Library Demo</b></a> (19MB)

  <br/>
  <i>Character-by-character karaoke synchronization with 12+ viewer modes and full customization</i>
</div>

## About the Kyrics Library

[Kyrics](https://github.com/lchau1017/Kyrics) is a powerful Android library for displaying synchronized karaoke lyrics with rich visual effects. This sample application demonstrates how to integrate and use the library in your projects.

### Key Features

- **Synchronized Lyrics Display** - Real-time character-by-character highlighting
- **Multiple Viewer Types** - 12+ different display modes (Stacked, Wave, Spiral, 3D-Carousel, etc.)
- **Rich Customization** - Font settings, animations, gradients, and visual effects
- **Built-in Presets** - 10 ready-to-use theme presets (Neon, Rainbow, Fire, Ocean, etc.)
- **Clean Architecture** - Easy integration with any Android project
- **Performance Optimized** - Efficient rendering with minimal recomposition

## Installation

### Step 1: Add JitPack Repository

In your `settings.gradle.kts` (root level):

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2: Add the Kyrics Dependency

In your `app/build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.lchau1017:Kyrics:v1.1.1")
}
```

Or using Version Catalog (`gradle/libs.versions.toml`):

```toml
[versions]
kyrics = "v1.1.1"

[libraries]
kyrics = { group = "com.github.lchau1017", name = "Kyrics", version.ref = "kyrics" }
```

Then in your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(libs.kyrics)
}
```

## Basic Usage

```kotlin
import com.kyrics.KyricsViewer
import com.kyrics.config.KyricsConfig

@Composable
fun KaraokeScreen() {
    val lines = remember { loadLyricLines() }
    val currentTime by musicPlayer.currentTime.collectAsState()

    KyricsViewer(
        lines = lines,
        currentTimeMs = currentTime,
        config = KyricsConfig.Default,
        modifier = Modifier.fillMaxSize()
    )
}
```

## Advanced Configuration with DSL

The Kyrics library provides a powerful DSL for configuration:

```kotlin
import com.kyrics.config.kyricsConfig
import com.kyrics.config.ViewerType

val customConfig = kyricsConfig {
    colors {
        playing = Color.Yellow
        played = Color.Yellow.copy(alpha = 0.7f)
        upcoming = Color.Yellow.copy(alpha = 0.4f)
        accompaniment = Color.Yellow.copy(alpha = 0.5f)
        background = Color.Black
    }

    typography {
        fontSize = 36.sp
        fontWeight = FontWeight.Bold
        textAlign = TextAlign.Center
    }

    animations {
        characterAnimations = true
        characterDuration = 800f
        characterScale = 1.15f
        lineAnimations = true
        lineScale = 1.05f
    }

    effects {
        blur = true
        blurIntensity = 1.0f
    }

    gradient {
        enabled = true
        angle = 45f
    }

    viewer {
        type = ViewerType.SMOOTH_SCROLL
    }

    layout {
        lineSpacing = 12.dp
    }
}

KyricsViewer(
    lines = lines,
    currentTimeMs = currentTime,
    config = customConfig
)
```

## Creating Lyrics with DSL

Use the `kyricsLine()` DSL to create synchronized lyrics:

```kotlin
import com.kyrics.kyricsLine

val line = kyricsLine(start = 0, end = 5000) {
    alignment("center")
    syllable("Hel", start = 0, end = 500)
    syllable("lo ", start = 500, end = 1000)
    syllable("World", start = 1000, end = 2000)
}

// For accompaniment/background vocals
val bgLine = kyricsLine(start = 2000, end = 4000) {
    alignment("center")
    accompaniment()
    syllable("(ooh)", start = 2000, end = 3000)
}
```

## Viewer Types

Choose from 12+ different viewing experiences:

| Viewer | Description | Best For |
|--------|-------------|----------|
| `CENTER_FOCUSED` | Active line centered, others fade | Traditional karaoke |
| `SMOOTH_SCROLL` | Continuous smooth scrolling | Modern apps |
| `STACKED` | Show active + next line | Singer preparation |
| `HORIZONTAL_PAGED` | Horizontal page transitions | Tablet interfaces |
| `WAVE_FLOW` | Flowing wave motion effects | Creative displays |
| `SPIRAL` | Spiral arrangement animation | Artistic presentations |
| `CAROUSEL_3D` | 3D carousel transitions | Premium experiences |
| `SPLIT_DUAL` | Split screen dual view | Duet performances |
| `ELASTIC_BOUNCE` | Bouncy elastic animations | Playful experiences |
| `FADE_THROUGH` | Smooth fade transitions | Elegant displays |
| `RADIAL_BURST` | Radial burst animations | Dynamic presentations |
| `FLIP_CARD` | Card flip transitions | Interactive experiences |

## Built-in Presets

The Kyrics library includes 10 ready-to-use theme presets:

```kotlin
import com.kyrics.KyricsPresets

KyricsPresets.Classic   // Simple, clean karaoke style
KyricsPresets.Neon      // Cyan/magenta with gradient effects
KyricsPresets.Rainbow   // Multi-color rainbow gradient
KyricsPresets.Fire      // Warm orange/red colors
KyricsPresets.Ocean     // Cool blue/turquoise
KyricsPresets.Retro     // 80s style with pink/cyan
KyricsPresets.Minimal   // Clean black/gray on white
KyricsPresets.Elegant   // Gold/silver with subtle effects
KyricsPresets.Party     // All effects maxed out
KyricsPresets.Matrix    // Green monospace on black
```

## Project Architecture

This sample application follows **Clean Architecture** with **MVI (Model-View-Intent)** pattern:

```
app/
├── di/                              # Dependency Injection
│   ├── AppModule.kt                 # Hilt module for DI
│   ├── DispatcherProvider.kt        # Interface for coroutine dispatchers
│   └── DefaultDispatcherProvider.kt # Default dispatcher implementation
│
├── infrastructure/                  # Android Framework
│   └── service/
│       └── PlaybackService.kt       # Media3 playback service
│
├── data/                            # Data Layer
│   ├── parser/
│   │   ├── TtmlParser.kt            # TTML lyrics parser (uses Kyrics DSL)
│   │   ├── TimeParser.kt            # Time format parsing utilities
│   │   └── XmlPullParserExtensions.kt # XML navigation helpers
│   ├── repository/
│   │   ├── LyricsRepositoryImpl.kt  # Lyrics data repository
│   │   └── SettingsRepositoryImpl.kt # User settings repository
│   └── source/local/
│       ├── AssetDataSource.kt       # Asset file access
│       ├── MediaContentProvider.kt  # Media content provider
│       └── PreferencesDataSource.kt # SharedPreferences access
│
├── domain/                          # Domain Layer
│   ├── model/
│   │   ├── SyncedLyrics.kt          # Wrapper for Kyrics SyncedLine
│   │   ├── UserSettings.kt          # User preferences model
│   │   ├── MediaContent.kt          # Media content model
│   │   └── LyricsSyncState.kt       # Lyrics sync state
│   ├── parser/
│   │   └── TtmlParser.kt            # Parser interface
│   ├── repository/
│   │   ├── LyricsRepository.kt      # Lyrics repository interface
│   │   └── SettingsRepository.kt    # Settings repository interface
│   └── usecase/
│       ├── LoadLyricsUseCase.kt     # Load and parse lyrics
│       ├── ParseTtmlUseCase.kt      # TTML parsing
│       ├── SyncLyricsUseCase.kt     # Lyrics synchronization
│       ├── ProcessLyricsDataUseCase.kt # Process lyrics data
│       └── ...                      # Other use cases
│
└── presentation/                    # Presentation Layer (MVI)
    ├── mapper/
    │   └── LibraryConfigMapper.kt   # Maps UserSettings to KyricsConfig
    ├── player/
    │   ├── PlayerController.kt      # Player interface
    │   └── MediaPlayerController.kt # Media3 player implementation
    ├── ui/
    │   ├── core/                    # Reusable UI components
    │   └── theme/                   # App theming
    └── features/
        ├── lyrics/
        │   ├── viewmodel/           # LyricsViewModel
        │   ├── screen/              # LyricsScreen composable
        │   ├── components/          # KaraokeLyricsView
        │   ├── coordinator/         # PlaybackSyncCoordinator
        │   ├── intent/              # MVI intents
        │   └── effect/              # MVI effects
        ├── settings/
        │   ├── viewmodel/           # SettingsViewModel
        │   ├── components/          # Settings UI components
        │   └── mapper/              # Settings UI mapper
        ├── player/
        │   ├── viewmodel/           # PlayerViewModel
        │   ├── components/          # Player controls
        │   └── intent/              # Player intents
        └── common/
            └── components/          # Shared components
```

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                      Presentation Layer                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │ LyricsScreen│  │   Settings  │  │    PlayerControls       │  │
│  │  (Compose)  │  │ BottomSheet │  │      (Compose)          │  │
│  └──────┬──────┘  └──────┬──────┘  └───────────┬─────────────┘  │
│         │                │                      │                │
│  ┌──────▼──────┐  ┌──────▼──────┐  ┌───────────▼─────────────┐  │
│  │   Lyrics    │  │  Settings   │  │       Player            │  │
│  │  ViewModel  │  │  ViewModel  │  │      ViewModel          │  │
│  └──────┬──────┘  └──────┬──────┘  └───────────┬─────────────┘  │
│         │                │                      │                │
│  ┌──────▼────────────────▼──────────────────────▼─────────────┐ │
│  │              LibraryConfigMapper (DSL)                      │ │
│  │         UserSettings → KyricsConfig (kyricsConfig {})       │ │
│  └──────────────────────────┬──────────────────────────────────┘ │
└─────────────────────────────┼────────────────────────────────────┘
                              │
┌─────────────────────────────▼────────────────────────────────────┐
│                        Domain Layer                               │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────────┐  │
│  │ LoadLyricsUse  │  │ SyncLyricsUse  │  │  ObserveSettings   │  │
│  │     Case       │  │     Case       │  │     UseCase        │  │
│  └───────┬────────┘  └───────┬────────┘  └─────────┬──────────┘  │
│          │                   │                     │              │
│  ┌───────▼───────────────────▼─────────────────────▼────────────┐│
│  │                    Repository Interfaces                      ││
│  │         LyricsRepository    SettingsRepository               ││
│  └──────────────────────────────────────────────────────────────┘│
└──────────────────────────────┬───────────────────────────────────┘
                               │
┌──────────────────────────────▼───────────────────────────────────┐
│                         Data Layer                                │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │                    TtmlParserImpl                           │  │
│  │           Uses kyricsLine() DSL for parsing                 │  │
│  │    ┌──────────────┐    ┌─────────────────────────────────┐ │  │
│  │    │  TimeParser  │    │  XmlPullParserExtensions        │ │  │
│  │    └──────────────┘    └─────────────────────────────────┘ │  │
│  └────────────────────────────────────────────────────────────┘  │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐   │
│  │ AssetDataSource │  │PreferencesData  │  │MediaContent     │   │
│  │ (Dispatcher DI) │  │    Source       │  │   Provider      │   │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘   │
└──────────────────────────────────────────────────────────────────┘
                               │
┌──────────────────────────────▼───────────────────────────────────┐
│                       Kyrics Library                              │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────────┐  │
│  │  KyricsViewer  │  │  KyricsConfig  │  │    SyncedLine      │  │
│  │   (Compose)    │  │     (DSL)      │  │     (Model)        │  │
│  └────────────────┘  └────────────────┘  └────────────────────┘  │
└──────────────────────────────────────────────────────────────────┘
```

## Running the Sample App

1. Clone the repository:
   ```bash
   git clone https://github.com/lchau1017/KaraokeLyrics.git
   cd KaraokeLyrics
   ```

2. Open in Android Studio

3. Run the app:
   ```bash
   ./gradlew :app:installDebug
   ```

## Code Quality

```bash
# Code formatting check
./gradlew ktlintCheck

# Auto-fix formatting issues
./gradlew ktlintFormat

# Static analysis
./gradlew detekt

# Run unit tests
./gradlew test
```

## Tech Stack

- **Jetpack Compose** - Modern Android UI toolkit
- **Kotlin 2.1.0** - Primary development language
- **Kyrics Library v1.1.1** - Karaoke lyrics display (via JitPack)
- **Media3/ExoPlayer** - Audio playback
- **Dagger Hilt** - Dependency injection
- **Coroutines & Flow** - Asynchronous programming
- **MVI Architecture** - Unidirectional data flow

## Requirements

- Android API 31+ (Android 12+)
- Kotlin 2.1.0+
- Jetpack Compose 2024.12.01+

## Links

- **Kyrics Library:** [github.com/lchau1017/Kyrics](https://github.com/lchau1017/Kyrics)
- **JitPack:** [jitpack.io/#lchau1017/Kyrics](https://jitpack.io/#lchau1017/Kyrics)

## License

```
MIT License

Copyright (c) 2025 KaraokeLyrics Contributors

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

---

<div align="center">

**Built with the [Kyrics](https://github.com/lchau1017/Kyrics) library**

</div>
