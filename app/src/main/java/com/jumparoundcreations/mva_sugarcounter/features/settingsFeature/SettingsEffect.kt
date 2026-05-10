package com.jumparoundcreations.mva_sugarcounter.features.settingsFeature


sealed interface SettingsSnackbarMessage {
    data class GramThresholdChangeSuccess(val newThreshold: String) : SettingsSnackbarMessage
    data object GramThresholdChangeCanceled : SettingsSnackbarMessage
}

sealed interface SettingsEffect {
    data class ShowSnackbar(val message: SettingsSnackbarMessage) : SettingsEffect
}