package com.kyrics.demo.presentation.viewmodel

/**
 * Sealed interface representing one-time side effects in the Demo screen.
 */
sealed interface DemoEffect {
    data class ShowToast(val message: String) : DemoEffect
    data class ShowError(val message: String) : DemoEffect
    data object PresetLoaded : DemoEffect
}
