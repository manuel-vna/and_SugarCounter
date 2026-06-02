package com.jumparoundcreations.mva_sugarcounter.features.settingsFeature

import com.jumparoundcreations.mva_sugarcounter.data.settingsData.BottomSheetsSettings

data class SettingsStates(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val faqExpandedId: Long = -1L,
    val gramThresholdSlider: Float = 0F,
    val gramThresholdDialogCheck: Boolean = false,
    val exportProgressIndicator: Float = 0.1f,
    val exportProgressIndicatorShown: Boolean = false,
    val dataSuccessfullyExportedShown: Boolean = false,
    val exportSuccessfully: Boolean = true,
    val entriesDeletionActivated: Boolean = false,
    val dynamicColorActivated: Boolean = false,
    val bottomSheetsSettings: BottomSheetsSettings = BottomSheetsSettings.NONE,
    val deletionWorkerSlider: Int = 3,
)
