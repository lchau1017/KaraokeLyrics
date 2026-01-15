# KaraokeLyrics

A modern Android karaoke application with syllable-level synchronization and smooth animations.

## Features

- **Syllable-level synchronization**: Real-time karaoke effect with word-by-word highlighting
- **Multi-line support**: Automatic text wrapping for long lyrics
- **Character animations**: Dynamic effects including bounce, swell, and dip animations
- **Background vocals**: Support for accompaniment tracks with visual distinction
- **Gradient effects**: Smooth visual transitions during playback
- **TTML support**: Native parsing of Timed Text Markup Language format

## Architecture

The app follows Clean Architecture principles with MVI pattern:

### Clean Architecture Layers

```
app/
├── domain/          # Business logic
│   ├── model/       # Domain models
│   ├── repository/  # Repository interfaces
│   └── usecase/     # Business use cases
│
├── data/            # Data layer
│   ├── parser/      # TTML parser implementation
│   └── repository/  # Repository implementations
│
├── presentation/    # UI layer
│   ├── viewmodel/   # MVI ViewModels
│   ├── state/       # UI state models
│   ├── intent/      # User actions
│   ├── effect/      # One-time events
│   ├── ui/          # Composable UI components
│   └── service/     # Media playback service
│
└── di/              # Dependency injection
```

## Technologies

- **UI**: Jetpack Compose with Canvas API for custom rendering
- **Architecture**: MVI + Clean Architecture
- **Dependency Injection**: Dagger Hilt
- **Async**: Kotlin Coroutines + Flow
- **Media Playback**: Media3/ExoPlayer
- **XML Parsing**: Built-in XmlPullParser for TTML format

## Features Implemented

- ✅ Custom TTML parser with syllable-level timing
- ✅ Synchronized playback with Media3
- ✅ Smooth gradient animation for karaoke effect
- ✅ Multi-line text with automatic word wrapping
- ✅ Character animations (Bounce, Swell, DipAndRise)
- ✅ Auto-scroll to current line
- ✅ Tap to seek to specific line
- ✅ Playback controls (play/pause, seek)
- ✅ Background vocals rendering
- ✅ RTL/LTR text support

## Requirements

- Android SDK 29+
- JDK 17 or higher
- Android Studio Hedgehog or newer

## Building

```bash
# Build the project
./gradlew build

# Install debug version
./gradlew installDebug

# Run tests
./gradlew test
```

## Project Structure

The application implements a fully self-contained karaoke system:

- **Custom TTML Parser**: Handles Apple Music format with background vocals
- **Layout Calculator**: Greedy word-wrapping algorithm for multi-line support
- **Animation System**: Per-character animations with easing functions
- **Gradient Renderer**: Real-time karaoke effect calculation

## License

This project is licensed under the MIT License - see the LICENSE file for details.