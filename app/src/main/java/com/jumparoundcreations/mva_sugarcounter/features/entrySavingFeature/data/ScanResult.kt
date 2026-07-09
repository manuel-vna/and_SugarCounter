package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data

sealed class ScanResult {
    data class FoundCategoryForBarcode(
        val category: String,
        val barcode: String,
    ) : ScanResult()

    data class ScanResultNoEntryInDbForBarcode(
        val barcode: String,
    ) : ScanResult()

    data class Failed(
        val reason: Throwable?,
    ) : ScanResult()
}
