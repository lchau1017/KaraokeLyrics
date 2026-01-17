package com.karaokelyrics.app.presentation.features.player.effect

sealed class PlayerEffect {
    data class ShowError(val message: String) : PlayerEffect()
    object PlaybackStarted : PlayerEffect()
    object PlaybackPaused : PlayerEffect()
    data class SeekCompleted(val position: Long) : PlayerEffect()
}