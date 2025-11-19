package com.jumparoundcreations.sugarcounter.features.EntrySavingFeature

sealed class EntrySavingStates {
    object Loading : EntrySavingStates()
    object Error : EntrySavingStates()
    data class SavingData(val data: InputData) : EntrySavingStates()
    object Idle : EntrySavingStates()
}

data class InputData(
    val datePickerShown: Boolean,
    val dateOfEntryEpochSec: Long,
    val barcodeNumber: String
) {
    companion object {
        val Default =
            InputData(
                datePickerShown = false,
                dateOfEntryEpochSec = System.currentTimeMillis() / 1000,
                barcodeNumber = ""
            )
    }
}

