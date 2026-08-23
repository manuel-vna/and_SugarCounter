package com.jumparoundcreations.mva_sugarcounter.features.entryListDisplayingFeature

import com.jumparoundcreations.mva_sugarcounter.data.EntryGroup
import com.jumparoundcreations.mva_sugarcounter.data.SugarEntry
import com.jumparoundcreations.mva_sugarcounter.features.entrySavingFeature.data.GramCountMode

sealed class EntryListDisplayingStates {
    object Loading : EntryListDisplayingStates()

    data class Success(
        val data: SuccessData,
    ) : EntryListDisplayingStates()

    data class Error(
        val message: String,
    ) : EntryListDisplayingStates()
}

data class SuccessData(
    val showCardItemBottomSheet: Boolean = false,
    val entryInCardItem: SugarEntry =
        SugarEntry(
            id = 0,
            currentTimestamp = 0L,
            date = "",
            category = "",
            entryType = GramCountMode.PerHundred,
            gramPerHundred = 0.0,
            gramPerPiece = 0.0,
            quantity = 0.0,
            amount = 0.0,
            gramTotal = 0.0,
        ),
    val entryDeletionConfirmationDialogShown: Boolean = false,
    val valueCategory: String = "",
    val valueGramPerHundred: String = "",
    val valueGramPerPiece: String = "",
    val valueQuantity: String = "",
    val valueAmount: String = "",
    val entriesGroupedPerDayCounter: List<EntryGroup> = listOf(),
    val entriesGroupedPerDayHistory: List<EntryGroup> = listOf(),
    val entriesGroupedPerDayUnfilteredHistory: List<EntryGroup> = listOf(),
    val searchFieldShown: Boolean = false,
    val searchFieldText: String = "",
)
