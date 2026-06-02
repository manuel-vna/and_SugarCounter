package com.jumparoundcreations.mva_sugarcounter.features.entryGraphDisplayingFeature

import com.jumparoundcreations.mva_sugarcounter.data.EntryGroup

sealed class EntryGraphDisplayingStates {
    object Loading : EntryGraphDisplayingStates()

    data class Success(
        val data: SuccessData,
    ) : EntryGraphDisplayingStates()

    data class Error(
        val message: String,
    ) : EntryGraphDisplayingStates()
}

data class SuccessData(
    val entriesGroupedPerDay: List<EntryGroup> = listOf(),
)
