package com.jumparoundcreations.sugarcounter.ui.events

sealed class ScanUiEvents {
    object ScanResultNoCategoryForBarcode : ScanUiEvents()
    object ScanResultFailed : ScanUiEvents()
}