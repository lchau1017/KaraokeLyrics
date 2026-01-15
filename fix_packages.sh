#!/bin/bash

BASE="/Users/lung_chau/Documents/code/KaraokeLyrics/app/src/main/java/com/karaokelyrics/app/presentation"

# Fix packages in features/lyrics
sed -i '' 's/package com.karaokelyrics.app.presentation.effect/package com.karaokelyrics.app.presentation.features.lyrics.effect/' "$BASE/features/lyrics/effect/LyricsEffect.kt"

# Fix packages in features/settings
for f in "$BASE/features/settings/components/"*.kt; do
  sed -i '' 's/package com.karaokelyrics.app.presentation.ui.components/package com.karaokelyrics.app.presentation.features.settings.components/' "$f"
done
sed -i '' 's/package com.karaokelyrics.app.presentation.ui.components.settings/package com.karaokelyrics.app.presentation.features.settings.viewdata/' "$BASE/features/settings/viewdata/SettingsBottomSheetViewData.kt"
sed -i '' 's/package com.karaokelyrics.app.presentation.mapper/package com.karaokelyrics.app.presentation.features.settings.mapper/' "$BASE/features/settings/mapper/SettingsUiMapper.kt"

# Fix packages in features/player
sed -i '' 's/package com.karaokelyrics.app.presentation.ui.components/package com.karaokelyrics.app.presentation.features.player.components/' "$BASE/features/player/components/PlayerControls.kt"
sed -i '' 's/package com.karaokelyrics.app.presentation.ui.components.player/package com.karaokelyrics.app.presentation.features.player.viewdata/' "$BASE/features/player/viewdata/PlayerControlsViewData.kt"

# Fix packages in features/common
for f in "$BASE/features/common/components/"*.kt; do
  sed -i '' 's/package com.karaokelyrics.app.presentation.ui.components/package com.karaokelyrics.app.presentation.features.common.components/' "$f"
done

# Fix packages in shared
for f in "$BASE/shared/animation/"*.kt; do
  sed -i '' 's/package com.karaokelyrics.app.presentation.ui.components.animation/package com.karaokelyrics.app.presentation.shared.animation/' "$f"
done

for f in "$BASE/shared/rendering/"*.kt; do
  sed -i '' 's/package com.karaokelyrics.app.presentation.ui.components.rendering/package com.karaokelyrics.app.presentation.shared.rendering/' "$f"
done

for f in "$BASE/shared/helper/"*.kt; do
  sed -i '' 's/package com.karaokelyrics.app.presentation.ui.helper/package com.karaokelyrics.app.presentation.shared.helper/' "$f"
done

for f in "$BASE/shared/utils/"*.kt; do
  sed -i '' 's/package com.karaokelyrics.app.presentation.ui.utils/package com.karaokelyrics.app.presentation.shared.utils/' "$f"
done

echo "Package declarations fixed!"

# Now fix imports in all files
find "$BASE/features" "$BASE/shared" -name "*.kt" -type f -exec sed -i '' \
  -e 's/import com.karaokelyrics.app.presentation.ui.components.KaraokeLyricsView/import com.karaokelyrics.app.presentation.features.lyrics.components.KaraokeLyricsView/g' \
  -e 's/import com.karaokelyrics.app.presentation.ui.components.KaraokeLineText/import com.karaokelyrics.app.presentation.features.lyrics.components.KaraokeLineText/g' \
  -e 's/import com.karaokelyrics.app.presentation.viewmodel.LyricsViewModel/import com.karaokelyrics.app.presentation.features.lyrics.viewmodel.LyricsViewModel/g' \
  -e 's/import com.karaokelyrics.app.presentation.state.LyricsUiState/import com.karaokelyrics.app.presentation.features.lyrics.state.LyricsUiState/g' \
  -e 's/import com.karaokelyrics.app.presentation.intent.LyricsIntent/import com.karaokelyrics.app.presentation.features.lyrics.state.LyricsIntent/g' \
  -e 's/import com.karaokelyrics.app.presentation.effect.LyricsEffect/import com.karaokelyrics.app.presentation.features.lyrics.effect.LyricsEffect/g' \
  -e 's/import com.karaokelyrics.app.presentation.ui.components.SettingsBottomSheet/import com.karaokelyrics.app.presentation.features.settings.components.SettingsBottomSheet/g' \
  -e 's/import com.karaokelyrics.app.presentation.ui.components.PlayerControls/import com.karaokelyrics.app.presentation.features.player.components.PlayerControls/g' \
  -e 's/import com.karaokelyrics.app.presentation.ui.components.LoadingScreen/import com.karaokelyrics.app.presentation.features.common.components.LoadingScreen/g' \
  -e 's/import com.karaokelyrics.app.presentation.ui.components.ErrorScreen/import com.karaokelyrics.app.presentation.features.common.components.ErrorScreen/g' \
  -e 's/import com.karaokelyrics.app.presentation.mapper.SettingsUiMapper/import com.karaokelyrics.app.presentation.features.settings.mapper.SettingsUiMapper/g' \
  -e 's/import com.karaokelyrics.app.presentation.ui.components.animation/import com.karaokelyrics.app.presentation.shared.animation/g' \
  -e 's/import com.karaokelyrics.app.presentation.ui.components.rendering/import com.karaokelyrics.app.presentation.shared.rendering/g' \
  -e 's/import com.karaokelyrics.app.presentation.ui.helper/import com.karaokelyrics.app.presentation.shared.helper/g' \
  -e 's/import com.karaokelyrics.app.presentation.ui.utils/import com.karaokelyrics.app.presentation.shared.utils/g' {} \;

echo "Imports fixed!"