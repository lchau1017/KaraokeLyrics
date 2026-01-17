package com.karaokelyrics.app.presentation.features.player.intent

sealed class PlayerIntent {
    object PlayPause : PlayerIntent()
    data class SeekToPosition(val position: Long) : PlayerIntent()
    data class LoadMedia(val fileName: String) : PlayerIntent()
}
