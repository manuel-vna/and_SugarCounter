package com.jumparoundcreations.sugarcounter.data.counterData

sealed class CounterIntents {

    data class ChooseEntryDate(
        val datePickerShown: Boolean,
        val newEntryDate: String
    ) : CounterIntents()

    object ScanBarcode : CounterIntents()

    data class SaveEntry(
        val tbd: String
    ) : CounterIntents()

    data class SaveCategory(
        val tbd: String
    ) : CounterIntents()

}