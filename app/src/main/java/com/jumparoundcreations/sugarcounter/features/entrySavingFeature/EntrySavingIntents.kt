package com.jumparoundcreations.sugarcounter.features.entrySavingFeature

sealed interface EntrySavingIntents {

    data object OpenAndCloseDatePicker : EntrySavingIntents
    data class ChangeSelectedDate(val epochTime: Long) : EntrySavingIntents
    data object ScanBarcode : EntrySavingIntents
    data class ChangeEntryFieldGram(val entryFieldGram: Double) : EntrySavingIntents

    data class ChangeEntryFieldQuantity(val entryFieldQuantity: Double) : EntrySavingIntents

}