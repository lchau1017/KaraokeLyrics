# KaraokeLyrics Refactoring Plan - SOLID Principles

## Goal
Improve code quality by applying SOLID principles, making the codebase more maintainable and easier to understand, especially the animation-lyrics relationship.

## Starting Point: Domain Layer
We start with domain because it's the foundation that other layers depend on.

## Phase 1: Domain Layer Refactoring (Start Here)

### 1.1 Fix UserSettings Model (SRP Violation)
**Problem**: UserSettings has computed properties (business logic)
```kotlin
// BAD - Model has business logic
data class UserSettings {
    val lyricsColorArgb: Int
        get() = if (isDarkMode) darkLyricsColorArgb else lightLyricsColorArgb
}
```

**Solution**: Move to ThemeUseCase
```kotlin
// GOOD - Pure data model
data class UserSettings(
    val darkLyricsColorArgb: Int,
    val lightLyricsColorArgb: Int,
    val isDarkMode: Boolean
)

// GOOD - Business logic in use case
class GetCurrentThemeColorsUseCase {
    operator fun invoke(settings: UserSettings): ThemeColors {
        return if (settings.isDarkMode) {
            ThemeColors(settings.darkLyricsColorArgb, settings.darkBackgroundColorArgb)
        } else {
            ThemeColors(settings.lightLyricsColorArgb, settings.lightBackgroundColorArgb)
        }
    }
}
```

### 1.2 Extract Animation Domain Models
**Problem**: Animation logic scattered across presentation layer
**Solution**: Create proper animation domain
```kotlin
// New domain models
sealed class AnimationType {
    object None : AnimationType()
    object Simple : AnimationType()
    data class Character(val style: CharacterAnimationStyle) : AnimationType()
}

data class AnimationConfig(
    val type: AnimationType,
    val duration: Long,
    val easing: EasingFunction
)

// Animation use case
class DetermineAnimationConfigUseCase {
    operator fun invoke(syllable: KaraokeSyllable, context: AnimationContext): AnimationConfig
}
```

### 1.3 Separate Layout from Animation Concerns
**Problem**: SyllableLayout mixes layout and animation data
**Solution**: Split into focused models
```kotlin
// Layout-only model
data class SyllableLayout(
    val syllable: KaraokeSyllable,
    val width: Float,
    val position: Offset,
    val baseline: Float
)

// Animation-only model
data class SyllableAnimation(
    val config: AnimationConfig,
    val pivot: Offset,
    val characterOffsets: List<Offset>?
)
```

## Phase 2: Data Layer Refactoring

### 2.1 Break Down TtmlParser (SRP)
**Problem**: 263-line class doing parsing + business logic
**Solution**: Separate concerns
```kotlin
// Parser interface for extensibility (OCP)
interface LyricsParser {
    fun parse(content: String): ParsedLyricsData
}

// Separate XML handling
class TtmlXmlParser : LyricsParser {
    override fun parse(content: String): ParsedLyricsData {
        // Only XML parsing logic
    }
}

// Separate time parsing
class TimeFormatParser {
    fun parseTimeExpression(expr: String): Int
}

// Factory for creating domain models
class LyricsFactory {
    fun createSyncedLyrics(data: ParsedLyricsData): SyncedLyrics
}
```

### 2.2 Fix Repository Pattern
**Problem**: Repositories doing too much
**Solution**: Single responsibility per repository
```kotlin
// Separate file operations from lyrics logic
interface FileRepository {
    suspend fun loadAssetFile(fileName: String): Result<String>
}

interface LyricsRepository {
    suspend fun getCurrentLyrics(): SyncedLyrics?
    suspend fun setCurrentLyrics(lyrics: SyncedLyrics)
}
```

## Phase 3: Presentation Layer Refactoring (Most Complex)

### 3.1 Break Down LyricsViewModel
**Problem**: 5+ responsibilities in one class
**Solution**: Delegate to specialized handlers
```kotlin
// Main ViewModel - orchestrates only
class LyricsViewModel(
    private val lyricsHandler: LyricsHandler,
    private val playerHandler: PlayerHandler,
    private val settingsHandler: SettingsHandler
) : ViewModel() {
    fun processIntent(intent: LyricsIntent) {
        when (intent) {
            is LyricsIntent.PlayPause -> playerHandler.togglePlayPause()
            is LyricsIntent.UpdateSettings -> settingsHandler.update(intent)
            // etc.
        }
    }
}

// Specialized handlers
class LyricsHandler {
    fun loadLyrics(fileName: String)
    fun syncWithPlayback(position: Long)
}

class PlayerHandler {
    fun play()
    fun pause()
    fun seekTo(position: Long)
}

class SettingsHandler {
    fun updateColor(color: Int)
    fun updateFontSize(size: FontSize)
}
```

### 3.2 Simplify KaraokeLineContainer
**Problem**: UI component doing business logic
**Solution**: Extract to use cases and helpers
```kotlin
// UI component - composition only
@Composable
fun KaraokeLineContainer(
    line: KaraokeLine,
    layout: LineLayout,          // Pre-calculated
    animation: LineAnimation,     // Pre-calculated
    renderer: SyllableRenderer    // Injected
) {
    // Only composition logic
    Canvas(modifier) {
        renderer.render(this, line, layout, animation)
    }
}

// Layout calculation moved to use case
class CalculateLineLayoutUseCase {
    operator fun invoke(line: KaraokeLine, constraints: Constraints): LineLayout
}
```

### 3.3 Abstract Animation System (OCP)
**Problem**: Hard-coded animation types
**Solution**: Strategy pattern
```kotlin
// Animation strategy interface
interface AnimationStrategy {
    fun calculate(progress: Float, context: AnimationContext): AnimationState
}

// Concrete strategies
class BounceAnimationStrategy : AnimationStrategy { }
class SwellAnimationStrategy : AnimationStrategy { }
class DipAnimationStrategy : AnimationStrategy { }

// Registry for extensibility
class AnimationRegistry {
    private val strategies = mutableMapOf<AnimationType, AnimationStrategy>()

    fun register(type: AnimationType, strategy: AnimationStrategy)
    fun getStrategy(type: AnimationType): AnimationStrategy
}
```

### 3.4 Fix Dependency Injection
**Problem**: Creating use cases in UI components
**Solution**: Proper DI
```kotlin
// BAD - in KaraokeLineContainer
val useCase = GroupSyllablesIntoWordsUseCase()

// GOOD - injected
@Composable
fun KaraokeLineContainer(
    viewModel: KaraokeLineViewModel = hiltViewModel()
) {
    // Use injected dependencies
}
```

## Implementation Order

1. **Week 1: Domain Layer**
   - Fix UserSettings (remove computed properties)
   - Create animation domain models
   - Split SyllableLayout model
   - Create missing use cases

2. **Week 2: Data Layer**
   - Refactor TtmlParser into smaller classes
   - Implement parser abstraction
   - Fix repository responsibilities

3. **Week 3-4: Presentation Layer**
   - Break down LyricsViewModel
   - Extract business logic from UI components
   - Implement animation strategy pattern
   - Fix dependency injection

## Success Metrics
- [ ] No class > 150 lines
- [ ] Each class has single responsibility
- [ ] Animation logic abstracted and extensible
- [ ] UI components contain no business logic
- [ ] All dependencies injected, not created
- [ ] Clear separation between layers

## Key Principles to Follow
1. **SRP**: One class, one responsibility
2. **OCP**: Use abstractions for extensibility
3. **LSP**: Proper inheritance hierarchies
4. **ISP**: Small, focused interfaces
5. **DIP**: Depend on abstractions, not concretions

## Testing Strategy
After each refactoring:
1. Add unit tests for new use cases
2. Test each component in isolation
3. Verify layer boundaries are maintained
4. Ensure no regression in functionality