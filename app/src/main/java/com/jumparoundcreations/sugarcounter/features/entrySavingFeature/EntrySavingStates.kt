package com.jumparoundcreations.sugarcounter.features.entrySavingFeature

import com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data.GramCountMode


data class EntrySavingStates(
    val datePickerShown: Boolean = false,
    val dateOfEntryEpochSec: Long = System.currentTimeMillis() / 1000,
    val barcodeNumber: String = "",

    val barcodeError:
    val categorySelected: String = "",
    val gramCountMode: GramCountMode = GramCountMode.PerHundred,
    val gramCountModeTabIndex: Int = 0,
    val entryFieldGram: String = "",
    val entryFieldQuantity: String = ""
)

