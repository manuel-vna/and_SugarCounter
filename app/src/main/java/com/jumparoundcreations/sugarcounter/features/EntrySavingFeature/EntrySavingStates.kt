package com.jumparoundcreations.sugarcounter.features.EntrySavingFeature


data class EntrySavingStates(
    val datePickerShown: Boolean = false,
    val dateOfEntryEpochSec: Long = System.currentTimeMillis() / 1000,
    val barcodeNumber: String = ""
)

