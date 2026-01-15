#!/bin/bash

# Base path
BASE_PATH="/Users/lung_chau/Documents/code/KaraokeLyrics/app/src/main/java/com/karaokelyrics/app/presentation"

# Create feature directories
mkdir -p "$BASE_PATH/features/lyrics/viewmodel"
mkdir -p "$BASE_PATH/features/lyrics/state"
mkdir -p "$BASE_PATH/features/lyrics/effect"
mkdir -p "$BASE_PATH/features/lyrics/screen"

mkdir -p "$BASE_PATH/features/settings/components"
mkdir -p "$BASE_PATH/features/settings/viewdata"
mkdir -p "$BASE_PATH/features/settings/mapper"

mkdir -p "$BASE_PATH/features/player/components"
mkdir -p "$BASE_PATH/features/player/viewdata"

mkdir -p "$BASE_PATH/features/common/components"

mkdir -p "$BASE_PATH/shared/animation"
mkdir -p "$BASE_PATH/shared/rendering"
mkdir -p "$BASE_PATH/shared/helper"
mkdir -p "$BASE_PATH/shared/utils"

# Move lyrics feature files
echo "Moving lyrics feature files..."
cp "$BASE_PATH/viewmodel/LyricsViewModel.kt" "$BASE_PATH/features/lyrics/viewmodel/" 2>/dev/null
cp "$BASE_PATH/state/LyricsUiState.kt" "$BASE_PATH/features/lyrics/state/" 2>/dev/null
cp "$BASE_PATH/intent/LyricsIntent.kt" "$BASE_PATH/features/lyrics/state/" 2>/dev/null
cp "$BASE_PATH/effect/LyricsEffect.kt" "$BASE_PATH/features/lyrics/effect/" 2>/dev/null
cp "$BASE_PATH/ui/screen/LyricsScreen.kt" "$BASE_PATH/features/lyrics/screen/" 2>/dev/null

# Move settings feature files
echo "Moving settings feature files..."
cp "$BASE_PATH/ui/components/SettingsBottomSheet.kt" "$BASE_PATH/features/settings/components/" 2>/dev/null
cp "$BASE_PATH/ui/components/SettingsPanel.kt" "$BASE_PATH/features/settings/components/" 2>/dev/null
cp "$BASE_PATH/ui/components/ColorSwatch.kt" "$BASE_PATH/features/settings/components/" 2>/dev/null
cp "$BASE_PATH/ui/components/FontSizeChip.kt" "$BASE_PATH/features/settings/components/" 2>/dev/null
cp "$BASE_PATH/ui/components/settings/SettingsBottomSheetViewData.kt" "$BASE_PATH/features/settings/viewdata/" 2>/dev/null
cp "$BASE_PATH/mapper/SettingsUiMapper.kt" "$BASE_PATH/features/settings/mapper/" 2>/dev/null

# Move player feature files
echo "Moving player feature files..."
cp "$BASE_PATH/ui/components/PlayerControls.kt" "$BASE_PATH/features/player/components/" 2>/dev/null
cp "$BASE_PATH/ui/components/player/PlayerControlsViewData.kt" "$BASE_PATH/features/player/viewdata/" 2>/dev/null

# Move common components
echo "Moving common components..."
cp "$BASE_PATH/ui/components/ErrorScreen.kt" "$BASE_PATH/features/common/components/" 2>/dev/null
cp "$BASE_PATH/ui/components/LoadingScreen.kt" "$BASE_PATH/features/common/components/" 2>/dev/null

# Move shared utilities
echo "Moving shared utilities..."
cp "$BASE_PATH/ui/components/animation/"*.kt "$BASE_PATH/shared/animation/" 2>/dev/null
cp "$BASE_PATH/ui/components/rendering/"*.kt "$BASE_PATH/shared/rendering/" 2>/dev/null
cp "$BASE_PATH/ui/helper/"*.kt "$BASE_PATH/shared/helper/" 2>/dev/null
cp "$BASE_PATH/ui/utils/"*.kt "$BASE_PATH/shared/utils/" 2>/dev/null

echo "Reorganization complete!"
echo ""
echo "New structure created:"
echo "- features/lyrics/ - Lyrics display feature"
echo "- features/settings/ - Settings management"
echo "- features/player/ - Player controls"
echo "- features/common/ - Shared components"
echo "- shared/ - Shared utilities and helpers"
echo ""
echo "Note: Original files are preserved. You can delete them after verifying the new structure works."