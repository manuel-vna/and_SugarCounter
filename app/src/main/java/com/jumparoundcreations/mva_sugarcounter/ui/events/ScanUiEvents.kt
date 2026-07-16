package com.jumparoundcreations.mva_sugarcounter.ui.events

sealed class ScanUiEvents {
    object ScanResultNoEntryInDbForBarcode : ScanUiEvents()

    object ScanResultNoProductFoundViaApi : ScanUiEvents()

    object ScanResultFailed : ScanUiEvents()

    object CategoryEditNoDataForChosenCategory : ScanUiEvents()
}
