package com.karaokelyrics.app.presentation.features.player.effect

sealed class PlayerEffect {
    data class ShowError(val message: String) : PlayerEffect()
}
