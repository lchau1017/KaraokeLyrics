# Presentation Layer Refactoring Guide

## Current Structure Issues
- Mixed concerns in single folders
- No clear feature boundaries
- Difficult to navigate and maintain
- Components scattered across multiple directories

## Proposed Feature-Based Architecture

### 1. Features Module Structure
```
presentation/
├── features/
│   ├── lyrics/
│   │   ├── screen/
│   │   │   └── LyricsScreen.kt
│   │   ├── components/
│   │   │   ├── KaraokeLyricsView.kt
│   │   │   ├── KaraokeLineText.kt
│   │   │   └── karaoke/
│   │   │       └── KaraokeLineContainer.kt
│   │   ├── viewmodel/
│   │   │   └── LyricsViewModel.kt
│   │   ├── state/
│   │   │   ├── LyricsUiState.kt
│   │   │   └── LyricsIntent.kt
│   │   └── effect/
│   │       └── LyricsEffect.kt
│   │
│   ├── settings/
│   │   ├── components/
│   │   │   ├── SettingsBottomSheet.kt
│   │   │   ├── SettingsPanel.kt
│   │   │   ├── ColorSwatch.kt
│   │   │   └── FontSizeChip.kt
│   │   ├── viewdata/
│   │   │   └── SettingsBottomSheetViewData.kt
│   │   └── mapper/
│   │       └── SettingsUiMapper.kt
│   │
│   ├── player/
│   │   ├── components/
│   │   │   └── PlayerControls.kt
│   │   └── viewdata/
│   │       └── PlayerControlsViewData.kt
│   │
│   └── common/
│       └── components/
│           ├── ErrorScreen.kt
│           └── LoadingScreen.kt
│
├── shared/
│   ├── animation/
│   │   ├── AnimationCalculator.kt
│   │   └── AnimationStateManager.kt
│   ├── rendering/
│   │   ├── SyllableRenderer.kt
│   │   └── GradientBrushFactory.kt
│   ├── helper/
│   │   └── TextCharacteristicsProcessor.kt
│   └── utils/
│       ├── TextUtils.kt
│       └── EasingFunctions.kt
│
├── ui/
│   ├── core/
│   │   ├── AppButton.kt
│   │   ├── AppChip.kt
│   │   ├── AppIconButton.kt
│   │   ├── AppSurface.kt
│   │   ├── AppText.kt
│   │   └── ViewData.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── KaraokeConfig.kt
│
└── MainActivity.kt
```

## Benefits of New Structure

### 1. **Feature Isolation**
Each feature (lyrics, settings, player) is self-contained with its own:
- Components
- ViewModels
- State management
- Effects

### 2. **Shared Resources**
Common utilities are centralized:
- Animation logic
- Rendering utilities
- Text processing helpers
- UI components

### 3. **Clean Imports**
```kotlin
// Before
import com.karaokelyrics.app.presentation.ui.components.KaraokeLyricsView
import com.karaokelyrics.app.presentation.viewmodel.LyricsViewModel
import com.karaokelyrics.app.presentation.state.LyricsUiState

// After
import com.karaokelyrics.app.presentation.features.lyrics.components.KaraokeLyricsView
import com.karaokelyrics.app.presentation.features.lyrics.viewmodel.LyricsViewModel
import com.karaokelyrics.app.presentation.features.lyrics.state.LyricsUiState
```

### 4. **Scalability**
New features can be added as independent modules:
```
features/
├── sync/        # New sync feature
├── export/      # New export feature
└── analytics/   # New analytics feature
```

## Migration Steps

### Step 1: Create Directory Structure
```bash
# Create feature directories
mkdir -p presentation/features/{lyrics,settings,player,common}/{components,viewmodel,state,effect}
mkdir -p presentation/shared/{animation,rendering,helper,utils}
```

### Step 2: Move Files
```bash
# Move lyrics feature files
mv ui/screen/LyricsScreen.kt features/lyrics/screen/
mv ui/components/KaraokeLyricsView.kt features/lyrics/components/
mv ui/components/KaraokeLineText.kt features/lyrics/components/
mv ui/components/karaoke/KaraokeLineContainer.kt features/lyrics/components/karaoke/
mv viewmodel/LyricsViewModel.kt features/lyrics/viewmodel/
mv state/LyricsUiState.kt features/lyrics/state/
mv intent/LyricsIntent.kt features/lyrics/state/
mv effect/LyricsEffect.kt features/lyrics/effect/

# Move settings feature files
mv ui/components/SettingsBottomSheet.kt features/settings/components/
mv ui/components/SettingsPanel.kt features/settings/components/
mv ui/components/ColorSwatch.kt features/settings/components/
mv ui/components/FontSizeChip.kt features/settings/components/
mv ui/components/settings/SettingsBottomSheetViewData.kt features/settings/viewdata/
mv mapper/SettingsUiMapper.kt features/settings/mapper/

# Move player feature files
mv ui/components/PlayerControls.kt features/player/components/
mv ui/components/player/PlayerControlsViewData.kt features/player/viewdata/

# Move common components
mv ui/components/ErrorScreen.kt features/common/components/
mv ui/components/LoadingScreen.kt features/common/components/

# Move shared utilities
mv ui/components/animation/*.kt shared/animation/
mv ui/components/rendering/*.kt shared/rendering/
mv ui/helper/*.kt shared/helper/
mv ui/utils/*.kt shared/utils/
```

### Step 3: Update Imports
Update all import statements to reflect new package structure.

### Step 4: Update Build Configuration
Ensure build.gradle recognizes new package structure.

## Code Quality Improvements

### 1. **Naming Conventions**
- Features: `features.<feature_name>`
- Shared: `shared.<utility_type>`
- UI Core: `ui.core`
- Theme: `ui.theme`

### 2. **Component Organization**
- Stateless components in `components/`
- ViewModels in `viewmodel/`
- State/Intent in `state/`
- Side effects in `effect/`

### 3. **Dependency Rules**
- Features can depend on shared and ui
- Features cannot depend on other features
- Shared modules are independent
- UI core has no dependencies

## Testing Structure
```
test/
├── features/
│   ├── lyrics/
│   ├── settings/
│   └── player/
├── shared/
└── ui/
```

## Documentation
Each feature should have:
- README.md explaining the feature
- Architecture diagram
- Component documentation
- API documentation

## Conclusion
This refactoring provides:
- ✅ Better code organization
- ✅ Improved maintainability
- ✅ Enhanced scalability
- ✅ Clear separation of concerns
- ✅ Easier navigation
- ✅ Better team collaboration
- ✅ Reduced coupling
- ✅ Increased cohesion