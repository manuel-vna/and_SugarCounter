package com.jumparoundcreations.sugarcounter.data.counterData

sealed class EntryStoringState {
    object Loading : EntryStoringState()
    object Error : EntryStoringState()
    data class Saved(val data: InputData) : EntryStoringState()
    object Idle : EntryStoringState()
}

data class InputData(
    val datePickerShown: Boolean,
    val dateOfEntryEpochSec: Long,
) {
    companion object {
        val Default =
            InputData(
                datePickerShown = false,
                dateOfEntryEpochSec = System.currentTimeMillis() / 1000
            )
    }
}

