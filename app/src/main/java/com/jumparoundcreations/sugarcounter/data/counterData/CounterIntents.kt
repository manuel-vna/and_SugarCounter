package com.jumparoundcreations.sugarcounter.data.counterData

sealed class EntryStoringActions {

    object OpenAndCloseDatePicker : EntryStoringActions()

    object SaveSelectedDate : EntryStoringActions()

    object ScanBarcodeAction : EntryStoringActions()

    object OpenCategoryFieldAction : EntryStoringActions()

    data class SaveCategory(
        val tbd: String
    ) : EntryStoringActions()

}