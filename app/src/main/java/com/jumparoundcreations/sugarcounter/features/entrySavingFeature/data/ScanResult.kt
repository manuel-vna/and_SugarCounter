package com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data

sealed class ScanResult {
    data class FoundCategoryForBarcode(
        val category: String,
        val barcode: String
    ) : ScanResult()

    data class NoCategoryForBarcode(
        val barcode: String
    ) : ScanResult()

    data class Failed(
        val reason: Throwable?
    ) : ScanResult()
}