package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature

import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.CheckThresholdResult
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode

data class EntrySavingStates(
    val datePickerShown: Boolean = false,
    val dateOfEntryEpochSec: Long = System.currentTimeMillis() / 1000,
    val barcodeNumber: String = "",
    val barcodeNotPresentInDb: Boolean = false,
    val barcodeInfoSheetShown: Boolean = false,
    val categoryInField: String = "",
    val categoryInDropdown: String = "",
    val categoryListInDropdown: List<String> = listOf(),
    val categoryDropdownExpanded: Boolean = false,
    val gramCountMode: GramCountMode = GramCountMode.PerHundred,
    val gramCountModeTabIndex: Int = 0,
    val entryFieldGram: String = "",
    val entryFieldQuantity: String = "",
    val savingProcessMissingCategoryData: Boolean = false,
    val savingProcessMissingSugarData: Boolean = false,
    val savingProcessDailyGramThreshold: CheckThresholdResult =
        CheckThresholdResult.WithinDailyThresholdBoundaries,
)
