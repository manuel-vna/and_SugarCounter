package com.jumparoundcreations.sugarcounter.features.entrySavingFeature.data

import com.jumparoundcreations.sugarcounter.data.SugarEntry

sealed class GetEntryByCategoryResult {
    object NoEntryFound : GetEntryByCategoryResult()
    data class EntryFound(val entry: SugarEntry) : GetEntryByCategoryResult()
}