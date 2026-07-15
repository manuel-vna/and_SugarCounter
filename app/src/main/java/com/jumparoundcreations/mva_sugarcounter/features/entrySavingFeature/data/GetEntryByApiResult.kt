package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data

sealed class GetEntryByApiResult {

    object ProductNotFound: GetEntryByApiResult()

    data class ProductFound(
        val entryType: GramCountMode,
        val category: String,
        val gram: Double,
    ) : GetEntryByApiResult()

}