package com.jumparoundcreations.sugarcounter.features.entrySavingFeature


data class EntrySavingStates(
    val datePickerShown: Boolean = false,
    val dateOfEntryEpochSec: Long = System.currentTimeMillis() / 1000,
    val barcodeNumber: String = "",
    val categorySelected: String = "",
    val entryFieldGram: Double,
    val entryFieldQuantity: Double
)

