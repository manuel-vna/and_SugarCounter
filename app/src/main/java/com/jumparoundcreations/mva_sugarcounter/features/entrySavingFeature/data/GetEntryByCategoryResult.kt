package com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data

import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry

sealed class GetEntryByCategoryResult {
    object NoEntryFound : GetEntryByCategoryResult()

    data class EntryFound(
        val entry: SugarEntry,
    ) : GetEntryByCategoryResult()
}
