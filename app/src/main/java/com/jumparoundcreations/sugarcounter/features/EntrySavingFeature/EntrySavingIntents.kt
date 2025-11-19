package com.jumparoundcreations.sugarcounter.features.EntrySavingFeature

sealed class EntrySavingIntents {

    object OpenAndCloseDatePicker : EntrySavingIntents()

    object SaveSelectedDate : EntrySavingIntents()

    object ScanBarcodeAction : EntrySavingIntents()

    object OpenCategoryFieldAction : EntrySavingIntents()

    data class SaveCategory(
        val tbd: String
    ) : EntrySavingIntents()

}